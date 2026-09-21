package pe.edu.upeu.acopioleche.presentation.dashboard.pagos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.model.PagoEntrega
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository
import pe.edu.upeu.acopioleche.domain.repository.PagoRepository
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas

class PagosHomeViewModel(
    scope: CoroutineScope,
    private val liquidacionRepository: LiquidacionRepository,
    private val pagoRepository: PagoRepository,
    private val precioTemporadaRepository: PrecioTemporadaRepository,
    private val sancionRepository: SancionRepository,
    private val reglasNegocio: ReglasNegocio,
    proveedorRepository: ProveedorRepository,
    entregaRepository: EntregaRepository,
    private val encargadoId: String,
    nombreEncargado: String,
    private val centroAcopioId: String? = null,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(
        PagosHomeUiState(
            nombreEncargado = nombreEncargado,
            centroAcopioId = centroAcopioId,
        ),
    )
    val uiState: StateFlow<PagosHomeUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                combine(
                    entregaRepository.observarTodasLasEntregas(),
                    sancionRepository.observarTodas(),
                ) { todasLasEntregas, sanciones -> todasLasEntregas to sanciones },
                liquidacionRepository.observarTodas(),
                pagoRepository.observarPagos(),
                precioTemporadaRepository.observarPrecios(),
                proveedorRepository.observarProveedores(),
            ) { (todasLasEntregas, sanciones), liquidacionesPersistidas, pagos, precios, proveedores ->
                val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val entregasHoy = todasLasEntregas.filter { it.fecha == hoy }

                val proveedoresFiltrados = if (centroAcopioId != null) {
                    val proveedoresCentroIds = entregasHoy.filter { it.centroAcopioId == centroAcopioId }.map { it.proveedorId }.toSet()
                    proveedores.filter { proveedoresCentroIds.contains(it.id) }
                } else {
                    proveedores
                }

                val provIdsPermitidos = proveedoresFiltrados.map { it.id }.toSet()
                val entregasPermitidas = todasLasEntregas.filter { provIdsPermitidos.contains(it.proveedorId) }

                // RF-27: la liquidación de la semana en curso se calcula en vivo a partir de las
                // entregas reales — antes dependía de que alguien generara la Liquidacion
                // manualmente desde el panel de admin, así que una entrega recién registrada nunca
                // aparecía aquí. Las semanas ya cerradas (persistidas) se respetan tal cual,
                // incluidas las que ya tienen un pago registrado.
                val semanaActual = CicloSemanal.inicioDeSemana(hoy)
                val liquidacionesPersistidasPermitidas = liquidacionesPersistidas.filter { provIdsPermitidos.contains(it.proveedorId) }
                val clavesYaPersistidas = liquidacionesPersistidasPermitidas.map { it.proveedorId to it.semanaInicio }.toSet()

                val liquidacionesEnVivo = calcularLiquidacionesSemanaActual(
                    entregas = entregasPermitidas,
                    sanciones = sanciones,
                    semanaInicio = semanaActual,
                ).filter { (it.proveedorId to it.semanaInicio) !in clavesYaPersistidas }

                val liquidacionesFiltradas = (liquidacionesPersistidasPermitidas + liquidacionesEnVivo)
                    .sortedWith(compareByDescending<Liquidacion> { it.semanaInicio }.thenBy { it.proveedorId })

                val pagosMap = pagos.associateBy { it.liquidacionId }
                val pagadas = liquidacionesFiltradas.filter { pagosMap.containsKey(it.id) }
                val pendientes = liquidacionesFiltradas.filter { !pagosMap.containsKey(it.id) }

                val litrosHoyPorProveedor = entregasPermitidas
                    .filter { it.fecha == hoy && esEntregaContable(it) }
                    .groupBy { it.proveedorId }
                    .mapValues { (_, lista) -> lista.sumOf { it.volumenLitros } }

                PagosHomeUiState(
                    nombreEncargado = nombreEncargado,
                    centroAcopioId = centroAcopioId,
                    liquidacionesSemana = liquidacionesFiltradas,
                    proveedores = proveedoresFiltrados.map { p ->
                        ProveedorConEntregas(
                            id = p.id,
                            nombre = p.nombre,
                            sector = p.sector,
                            numeroVacas = p.numeroVacas,
                            litrosHoy = litrosHoyPorProveedor[p.id] ?: 0.0,
                            calificacion = p.calificacion,
                            activo = p.activo,
                        )
                    },
                    pagosRegistrados = pagos,
                    preciosTemporada = precios,
                    totalMontoPagadoSemana = pagadas.sumOf { it.montoFinal },
                    totalMontoPendienteSemana = pendientes.sumOf { it.montoFinal },
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun registrarPagoManual(liquidacion: Liquidacion, metodoPago: String, notas: String) {
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            // La liquidación pudo haberse calculado en vivo (todavía no persistida): al pagarla
            // queda fijada tal como está en este momento, sin quedar sujeta a recálculos
            // posteriores si cambian las entregas de esa semana.
            liquidacionRepository.registrar(liquidacion)
            val pago = PagoEntrega(
                id = "PAGO-${Clock.System.now().toEpochMilliseconds()}",
                liquidacionId = liquidacion.id,
                proveedorId = liquidacion.proveedorId,
                monto = liquidacion.montoFinal,
                fechaHora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                encargadoId = encargadoId,
                metodoPago = metodoPago,
                notas = notas,
            )
            pagoRepository.registrarPago(pago)
            _uiState.value = _uiState.value.copy(
                guardando = false,
                mensajeNotificacion = "Pago de S/ ${liquidacion.montoFinal} registrado correctamente para la liquidación ${liquidacion.id}",
            )
        }
    }

    fun guardarPrecioTemporada(precio: PrecioTemporada) {
        scope.launch {
            precioTemporadaRepository.guardar(precio)
            _uiState.value = _uiState.value.copy(
                mensajeNotificacion = "Tarifa de temporada '${precio.nombreTemporada}' guardada (S/ ${precio.precioPorLitro}/L)",
            )
        }
    }

    private fun esEntregaContable(entrega: Entrega): Boolean =
        entrega.estado !is EstadoEntrega.Cancelada &&
            entrega.estado !is EstadoEntrega.NoRecogida &&
            entrega.estado !is EstadoEntrega.Rechazada

    /**
     * Litros y monto por proveedor de la semana [semanaInicio] (jueves-miércoles, RF-06),
     * calculados con la misma [CalculadoraLiquidacion] y el mismo precio (uno solo por semana,
     * vía [CalculadoraLiquidacion.fechaReferenciaPrecio]) y la misma lógica de sanción (RN-10)
     * que usa `LiquidacionesViewModel.generarPara` al generar la liquidación real — para que la
     * vista en vivo de Pagos y una liquidación ya generada para la misma semana coincidan en
     * `montoFinal`. `internal` (no `private`) para poder probarla directamente.
     */
    internal suspend fun calcularLiquidacionesSemanaActual(
        entregas: List<Entrega>,
        sanciones: List<SancionAplicada>,
        semanaInicio: LocalDate,
    ): List<Liquidacion> {
        val finDeSemana = semanaInicio.plus(6, DateTimeUnit.DAY)
        val fechaReferenciaPrecio = CalculadoraLiquidacion.fechaReferenciaPrecio(semanaInicio)
        val precioVigente = precioTemporadaRepository.obtenerPrecioVigenteEn(fechaReferenciaPrecio)
        if (precioVigente.esRespaldo) {
            AppLogger.warn(
                TAG,
                "No hay PrecioTemporada vigente para $fechaReferenciaPrecio; usando el respaldo S/ ${precioVigente.precioPorLitro}/L",
            )
        }

        return entregas
            .filter { it.fecha in semanaInicio..finDeSemana && esEntregaContable(it) }
            .groupBy { it.proveedorId }
            .map { (proveedorId, entregasDelProveedor) ->
                val litros = entregasDelProveedor.sumOf { it.volumenLitros }
                val tieneSancionPendiente = sanciones.any { sancion ->
                    sancion is SancionAplicada.ReduccionPrecioSemanal &&
                        sancion.proveedorId == proveedorId &&
                        sancion.semanaInicio == semanaInicio
                }
                CalculadoraLiquidacion.calcular(
                    id = "LIQ-$proveedorId-$semanaInicio",
                    proveedorId = proveedorId,
                    semanaInicio = semanaInicio,
                    litrosAceptados = litros,
                    reglas = reglasNegocio,
                    precioPorLitroVigente = precioVigente.precioPorLitro,
                    tieneSancionReduccionPendiente = tieneSancionPendiente,
                    generadaAutomaticamente = true,
                )
            }
    }

    private companion object {
        const val TAG = "PagosHomeViewModel"
    }
}
