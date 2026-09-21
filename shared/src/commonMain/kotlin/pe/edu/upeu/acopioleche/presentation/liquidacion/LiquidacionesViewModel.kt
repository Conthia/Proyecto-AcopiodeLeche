package pe.edu.upeu.acopioleche.presentation.liquidacion

import kotlin.time.Clock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.domain.service.GeneradorNotificaciones
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

/**
 * RF-06/RF-07. No está confirmado si la liquidación se genera sola cada viernes o si un
 * Administrador la dispara a mano — se implementan ambas variantes, como se pidió:
 * - **Automática**: al abrir esta pantalla, si ya pasó la fecha de pago de la última semana
 *   cerrada (jueves a miércoles) y a algún proveedor activo le falta su liquidación, se genera
 *   sola. Esta app no tiene un cron/backend real, así que "automática" aquí significa "al usar
 *   la app después del viernes de pago", no un job en segundo plano.
 * - **Manual**: [onGenerarClick] fuerza la generación de lo que falte para la semana mostrada,
 *   sin esperar a que se cumpla la fecha de pago.
 * Ninguna de las dos regenera una liquidación que ya existe para esa semana (idempotente).
 */
class LiquidacionesViewModel(
    scope: CoroutineScope,
    private val proveedorRepository: ProveedorRepository,
    private val entregaRepository: EntregaRepository,
    private val sancionRepository: SancionRepository,
    private val liquidacionRepository: LiquidacionRepository,
    private val notificacionRepository: NotificacionRepository,
    private val precioTemporadaRepository: PrecioTemporadaRepository,
) : AppViewModel(scope = scope) {

    private val semanaMostrada = CicloSemanal.inicioDeSemana(Clock.System.todayIn(TimeZone.currentSystemDefault()))
        .minus(7, DateTimeUnit.DAY)

    /** Fijas para la vida del ViewModel (no dependen de ningún repositorio): siempre visibles, incluso en Cargando/Error/Vacio. */
    val semanaInicio: LocalDate = semanaMostrada
    val fechaPago: LocalDate = CicloSemanal.fechaDePago(semanaMostrada)

    private val _uiState = MutableStateFlow<UiState<LiquidacionesUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<LiquidacionesUiState>> = _uiState.asStateFlow()

    private val _generando = MutableStateFlow(false)
    val generando: StateFlow<Boolean> = _generando.asStateFlow()

    /** Mensaje de éxito/error de `onGenerarClick`, separado de [uiState]: un fallo al regenerar no debe reemplazar la lista ya cargada por una pantalla de Error. */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val yaSePuedePagar = hoy >= CicloSemanal.fechaDePago(semanaMostrada)
        scope.launch {
            try {
                val resumenes = cargar(generarSiFalta = yaSePuedePagar, automatica = true)
                _uiState.value = aUiState(resumenes)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                AppLogger.error(TAG, "Error al cargar liquidaciones", error)
                _uiState.value = UiState.Error("No se pudo cargar la información")
            }
        }
    }

    fun onGenerarClick() {
        scope.launch {
            _generando.value = true
            _mensaje.value = null
            try {
                val resumenes = cargar(generarSiFalta = true, automatica = false)
                _uiState.value = aUiState(resumenes)
                _mensaje.value = "Liquidaciones de la semana actualizadas"
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                AppLogger.error(TAG, "Error al generar liquidaciones", error)
                _mensaje.value = "No se pudo generar la liquidación. Intenta nuevamente."
            } finally {
                _generando.value = false
            }
        }
    }

    private fun aUiState(resumenes: List<LiquidacionResumen>): UiState<LiquidacionesUiState> =
        if (resumenes.isEmpty()) UiState.Vacio else UiState.Exito(LiquidacionesUiState(resumenes = resumenes))

    private suspend fun cargar(generarSiFalta: Boolean, automatica: Boolean): List<LiquidacionResumen> {
        val proveedoresActivos = proveedorRepository.observarProveedores().first().filter { it.activo }
        // Un solo precio para toda la semana (ver CalculadoraLiquidacion.fechaReferenciaPrecio):
        // se consulta una sola vez por carga, no por proveedor, y solo si de verdad puede hacer
        // falta para generar algo -- evita una lectura y un posible AppLogger.warn de más cuando
        // todos los proveedores ya tienen su liquidación persistida.
        val precioPorLitro = if (generarSiFalta) obtenerPrecioPorLitroDeLaSemana() else null
        return proveedoresActivos.map { proveedor ->
            val liquidacion = liquidacionRepository.buscar(proveedorId = proveedor.id, semanaInicio = semanaMostrada)
                ?: if (generarSiFalta) {
                    generarPara(proveedorId = proveedor.id, automatica = automatica, precioPorLitro = requireNotNull(precioPorLitro))
                } else {
                    null
                }
            LiquidacionResumen(
                proveedorId = proveedor.id,
                nombreProveedor = proveedor.nombre,
                litrosAceptados = liquidacion?.litrosAceptados ?: 0.0,
                montoFinal = liquidacion?.montoFinal ?: 0.0,
                tieneSancionPendienteDeMonto = liquidacion?.tieneSancionPendienteDeMonto ?: false,
                generada = liquidacion != null,
            )
        }
    }

    private suspend fun obtenerPrecioPorLitroDeLaSemana(): Double {
        val fechaReferencia = CalculadoraLiquidacion.fechaReferenciaPrecio(semanaMostrada)
        val precioVigente = precioTemporadaRepository.obtenerPrecioVigenteEn(fechaReferencia)
        if (precioVigente.esRespaldo) {
            AppLogger.warn(
                TAG,
                "No hay PrecioTemporada vigente para $fechaReferencia; usando el respaldo S/ ${precioVigente.precioPorLitro}/L",
            )
        }
        return precioVigente.precioPorLitro
    }

    private suspend fun generarPara(proveedorId: String, automatica: Boolean, precioPorLitro: Double): Liquidacion {
        val semanaFin = semanaMostrada.plus(6, DateTimeUnit.DAY)
        val entregasDeLaSemana = entregaRepository.observarEntregasDe(proveedorId).first().filter { entrega ->
            entrega.fecha in semanaMostrada..semanaFin && entrega.estado is EstadoEntrega.Aceptada
        }
        val litrosAceptados = entregasDeLaSemana.sumOf { it.volumenLitros }

        val tieneSancionPendiente = sancionRepository.observarSancionesDe(proveedorId).first().any { sancion ->
            sancion is SancionAplicada.ReduccionPrecioSemanal && sancion.semanaInicio == semanaMostrada
        }

        val liquidacion = CalculadoraLiquidacion.calcular(
            id = "LIQ-${Clock.System.now().toEpochMilliseconds()}-$proveedorId",
            proveedorId = proveedorId,
            semanaInicio = semanaMostrada,
            litrosAceptados = litrosAceptados,
            precioPorLitroVigente = precioPorLitro,
            tieneSancionReduccionPendiente = tieneSancionPendiente,
            generadaAutomaticamente = automatica,
        )
        liquidacionRepository.registrar(liquidacion = liquidacion)

        marcarEntregasComoLiquidadas(entregas = entregasDeLaSemana, liquidacionId = liquidacion.id)

        // RF-11/14/15/16: resumen semanal para el proveedor, generado junto con su liquidación.
        notificacionRepository.registrar(
            GeneradorNotificaciones.resumenEntregaSemanal(
                id = "N-${Clock.System.now().toEpochMilliseconds()}-$proveedorId",
                proveedorId = proveedorId,
                litrosSemana = liquidacion.litrosAceptados,
                montoFinal = liquidacion.montoFinal,
                fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ),
        )

        return liquidacion
    }

    private suspend fun marcarEntregasComoLiquidadas(entregas: List<Entrega>, liquidacionId: String) {
        entregas.forEach { entrega ->
            entregaRepository.registrar(entrega.copy(estado = EstadoEntrega.Liquidada(liquidacionId = liquidacionId)))
        }
    }

    private companion object {
        const val TAG = "LiquidacionesViewModel"
    }
}
