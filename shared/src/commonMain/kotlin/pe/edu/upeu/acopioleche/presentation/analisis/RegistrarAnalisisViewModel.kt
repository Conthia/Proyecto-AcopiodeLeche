package pe.edu.upeu.acopioleche.presentation.analisis

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad
import pe.edu.upeu.acopioleche.domain.model.CapacitacionCorrectiva
import pe.edu.upeu.acopioleche.domain.model.CriterioAnalisis
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.LecturaLactoescan
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.OrigenDatoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.CapacitacionCorrectivaRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.domain.service.EvaluadorCalidad
import pe.edu.upeu.acopioleche.domain.service.GeneradorNotificaciones
import pe.edu.upeu.acopioleche.domain.service.MotorSanciones
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class RegistrarAnalisisViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    private val proveedorRepository: ProveedorRepository,
    private val analisisCalidadRepository: AnalisisCalidadRepository,
    private val sancionRepository: SancionRepository,
    private val capacitacionCorrectivaRepository: CapacitacionCorrectivaRepository,
    private val notificacionRepository: NotificacionRepository,
    private val reglasNegocio: ReglasNegocio,
    private val entregaId: String,
    private val tecnicoId: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(RegistrarAnalisisUiState(entregaId = entregaId))
    val uiState: StateFlow<RegistrarAnalisisUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            val entrega = entregaRepository.buscarPorId(entregaId)
            val proveedor = entrega?.let { e ->
                proveedorRepository.observarProveedores().first().find { it.id == e.proveedorId }
            }
            _uiState.value = _uiState.value.copy(nombreProveedor = proveedor?.nombre ?: entrega?.proveedorId.orEmpty())
        }
    }

    fun onDensidadChange(texto: String) = actualizarCampo { copy(densidadTexto = filtrarNumero(texto)) }
    fun onGrasaChange(texto: String) = actualizarCampo { copy(grasaTexto = filtrarNumero(texto)) }
    fun onProteinaChange(texto: String) = actualizarCampo { copy(proteinaTexto = filtrarNumero(texto)) }
    fun onLactosaChange(texto: String) = actualizarCampo { copy(lactosaTexto = filtrarNumero(texto)) }
    fun onTemperaturaChange(texto: String) = actualizarCampo { copy(temperaturaTexto = filtrarNumero(texto)) }
    fun onPhChange(texto: String) = actualizarCampo { copy(phTexto = filtrarNumero(texto)) }
    fun onPorcentajeAguaChange(texto: String) = actualizarCampo { copy(porcentajeAguaTexto = filtrarNumero(texto)) }

    fun onCriterioSeleccionChange(criterio: CriterioAnalisis) {
        _uiState.value = _uiState.value.copy(criterioSeleccion = criterio)
    }

    fun onOrigenDatoChange(origen: OrigenDatoAnalisis) {
        _uiState.value = _uiState.value.copy(origenDato = origen)
    }

    fun onFirmaProductorChange(presente: Boolean) {
        _uiState.value = _uiState.value.copy(firmaProductorPresente = presente)
    }

    fun simularEscaneoOCRTicket() {
        _uiState.value = _uiState.value.copy(
            densidadTexto = "1.031",
            grasaTexto = "3.5",
            proteinaTexto = "3.2",
            lactosaTexto = "4.6",
            temperaturaTexto = "4.0",
            phTexto = "6.6",
            porcentajeAguaTexto = "0.0",
            origenDato = OrigenDatoAnalisis.OCR_COMPROBANTE,
            mensajeError = null,
        )
    }

    private fun actualizarCampo(transformar: RegistrarAnalisisUiState.() -> RegistrarAnalisisUiState) {
        _uiState.value = _uiState.value.transformar().copy(mensajeError = null)
    }

    private fun filtrarNumero(texto: String): String =
        texto.filterIndexed { index, c -> c.isDigit() || (c == '.' && !texto.take(index).contains('.')) }

    fun onGuardarClick() {
        val estado = _uiState.value
        val lectura = construirLectura(estado)
        if (lectura == null) {
            _uiState.value = estado.copy(mensajeError = "Completa los 7 valores del lactoescan")
            return
        }
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)

            val resultado = EvaluadorCalidad.evaluar(lectura)
            val analisis = AnalisisCalidad(
                id = "AC-${Clock.System.now().toEpochMilliseconds()}",
                entregaId = entregaId,
                tecnicoId = tecnicoId,
                fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                resultado = resultado,
                criterioSeleccion = estado.criterioSeleccion,
                origenDato = estado.origenDato,
                firmaProductorPresente = estado.firmaProductorPresente,
            )
            analisisCalidadRepository.registrar(analisis = analisis)

            val entrega = entregaRepository.buscarPorId(entregaId)
            var sancionAplicada: ResultadoSancion? = null
            var advertenciaEstado: String? = null

            if (entrega != null) {
                notificacionRepository.registrar(
                    GeneradorNotificaciones.resultadoDensidad(
                        id = "N-${Clock.System.now().toEpochMilliseconds()}-densidad",
                        proveedorId = entrega.proveedorId,
                        densidad = lectura.densidad,
                        fecha = analisis.fecha,
                    ),
                )

                // RN-20: una entrega EnTransitoAPlanta/Liquidada no puede sobrescribirse, ni
                // siquiera para aplicar el rechazo automático de RF-05 — eso borraría evidencia
                // de un estado ya consolidado. Se reporta la inconsistencia en vez de fallar
                // silenciosamente (queda para revisión manual).
                suspend fun sobrescribirEstadoOAdvertir(nuevoEstado: EstadoEntrega, motivoCambio: String) {
                    if (entrega.estado is EstadoEntrega.EnTransitoAPlanta || entrega.estado is EstadoEntrega.Liquidada) {
                        advertenciaEstado = "No se pudo aplicar '$motivoCambio' a la entrega $entregaId: " +
                            "ya está en estado ${entrega.estado::class.simpleName} y no admite modificación (RN-20). Requiere revisión manual."
                    } else {
                        entregaRepository.registrar(entrega.copy(estado = nuevoEstado))
                    }
                }

                when (resultado) {
                    is ResultadoAnalisis.FueraDeRango -> {
                        sobrescribirEstadoOAdvertir(
                            nuevoEstado = EstadoEntrega.Rechazada(motivo = resultado.motivo),
                            motivoCambio = "rechazo automático (${resultado.motivo})",
                        )
                        if (resultado.motivo == MotivoRechazo.ACIDEZ_FUERA_DE_RANGO) {
                            generarCapacitacionCorrectiva(proveedorId = entrega.proveedorId)
                        }
                    }
                    is ResultadoAnalisis.Adulterada -> {
                        val decision = aplicarSancionPorAdulteracion(proveedorId = entrega.proveedorId, porcentajeAgua = resultado.porcentajeAgua)
                        sancionAplicada = decision
                        val esAceptada = decision is ResultadoSancion.ReducirPrecioSemanal
                        sobrescribirEstadoOAdvertir(
                            nuevoEstado = if (esAceptada) EstadoEntrega.Aceptada else EstadoEntrega.Rechazada(motivo = MotivoRechazo.ADULTERACION_DETECTADA),
                            motivoCambio = if (esAceptada) "aceptación con sanción" else "rechazo por adulteración",
                        )
                        notificacionRepository.registrar(
                            GeneradorNotificaciones.alertaAdulteracion(
                                id = "N-${Clock.System.now().toEpochMilliseconds()}-adulteracion",
                                proveedorId = entrega.proveedorId,
                                porcentajeAgua = resultado.porcentajeAgua,
                                decision = decision,
                                fecha = analisis.fecha,
                            ),
                        )
                    }
                    is ResultadoAnalisis.Normal -> Unit
                }
            }

            _uiState.value = _uiState.value.copy(
                guardando = false,
                resultadoGuardado = resultado,
                sancionAplicada = sancionAplicada,
                advertenciaEstado = advertenciaEstado,
            )
        }
    }

    private suspend fun aplicarSancionPorAdulteracion(proveedorId: String, porcentajeAgua: Double): ResultadoSancion {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val adulteracionesPrevias = sancionRepository.observarSancionesDe(proveedorId).first().size
        val decision = MotorSanciones.evaluarAdulteracion(
            proveedorId = proveedorId,
            porcentajeAgua = porcentajeAgua,
            numeroAdulteracionesPrevias = adulteracionesPrevias,
            reglas = reglasNegocio,
        )

        val idSancion = "SAN-${Clock.System.now().toEpochMilliseconds()}"
        val registro = when (decision) {
            is ResultadoSancion.ReducirPrecioSemanal -> SancionAplicada.ReduccionPrecioSemanal(
                id = idSancion,
                proveedorId = proveedorId,
                fecha = hoy,
                semanaInicio = CicloSemanal.inicioDeSemana(hoy),
            )
            is ResultadoSancion.RetirarYMultar -> SancionAplicada.RetiroPorAdulteracion(
                id = idSancion,
                proveedorId = proveedorId,
                fecha = hoy,
                montoMulta = decision.montoMulta,
            )
            is ResultadoSancion.RetirarInmediato -> SancionAplicada.RetiroPorAdulteracion(
                id = idSancion,
                proveedorId = proveedorId,
                fecha = hoy,
                montoMulta = 0.0,
            )
        }
        sancionRepository.registrar(sancion = registro)

        if (decision !is ResultadoSancion.ReducirPrecioSemanal) {
            val proveedor = proveedorRepository.observarProveedores().first().find { it.id == proveedorId }
            if (proveedor != null) {
                proveedorRepository.actualizar(proveedor.copy(activo = false))
            }
        }

        return decision
    }

    private suspend fun generarCapacitacionCorrectiva(proveedorId: String) {
        val motivo = "Acidez fuera de rango"
        capacitacionCorrectivaRepository.registrar(
            CapacitacionCorrectiva(
                id = "CAP-${Clock.System.now().toEpochMilliseconds()}",
                proveedorId = proveedorId,
                motivo = motivo,
                fecha = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            ),
        )
        notificacionRepository.registrar(
            GeneradorNotificaciones.avisoCapacitacion(
                id = "N-${Clock.System.now().toEpochMilliseconds()}-capacitacion",
                proveedorId = proveedorId,
                motivo = motivo,
                fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ),
        )
    }

    private fun construirLectura(estado: RegistrarAnalisisUiState): LecturaLactoescan? {
        val densidad = estado.densidad ?: return null
        val grasa = estado.grasa ?: return null
        val proteina = estado.proteina ?: return null
        val lactosa = estado.lactosa ?: return null
        val temperatura = estado.temperatura ?: return null
        val ph = estado.ph ?: return null
        val porcentajeAgua = estado.porcentajeAgua ?: return null
        return LecturaLactoescan(
            densidad = densidad,
            grasa = grasa,
            proteina = proteina,
            lactosa = lactosa,
            temperatura = temperatura,
            ph = ph,
            porcentajeAguaAnadida = porcentajeAgua,
        )
    }
}
