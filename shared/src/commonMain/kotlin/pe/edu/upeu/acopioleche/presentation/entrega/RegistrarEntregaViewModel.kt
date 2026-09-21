package pe.edu.upeu.acopioleche.presentation.entrega

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.EstadoParada
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository
import pe.edu.upeu.acopioleche.domain.service.GeneradorNotificaciones
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.aResumen

class RegistrarEntregaViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    private val proveedorRepository: ProveedorRepository,
    private val notificacionRepository: NotificacionRepository,
    private val rutaRepository: RutaRepository,
    private val centroAcopioId: String,
    private val acopiadorId: String,
    proveedorId: String,
) : AppViewModel(scope = scope) {

    private val horaActual: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    private val _uiState = MutableStateFlow(RegistrarEntregaUiState(turno = Turno.deducirDeHora(horaActual)))
    val uiState: StateFlow<RegistrarEntregaUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            val proveedor = proveedorRepository.observarProveedores().first().find { it.id == proveedorId }
            _uiState.value = _uiState.value.copy(proveedor = proveedor)
        }
    }

    fun onVolumenLitrosChange(texto: String) {
        val filtrado = texto.filterIndexed { index, c -> c.isDigit() || (c == '.' && !texto.take(index).contains('.')) }
        _uiState.value = _uiState.value.copy(volumenLitrosTexto = filtrado, mensajeError = null)
    }

    fun onAgregarLitros(cantidad: Int) {
        val actual = _uiState.value.volumenLitros
        onVolumenLitrosChange((actual + cantidad).toString())
    }

    fun onPorongosMenos() {
        _uiState.value = _uiState.value.copy(cantidadPorongos = (_uiState.value.cantidadPorongos - 1).coerceAtLeast(1))
    }

    fun onPorongosMas() {
        _uiState.value = _uiState.value.copy(cantidadPorongos = (_uiState.value.cantidadPorongos + 1).coerceAtMost(12))
    }

    fun onMarcarNoRecogida(noRecogida: Boolean) {
        _uiState.value = _uiState.value.copy(esNoRecogida = noRecogida, mensajeError = null)
    }

    fun onMotivoNoRecogidaChange(motivo: String) {
        _uiState.value = _uiState.value.copy(motivoNoRecogida = motivo)
    }

    fun onGuardarClick() {
        val estado = _uiState.value
        val proveedor = estado.proveedor
        if (proveedor == null) {
            _uiState.value = estado.copy(mensajeError = "Selecciona un proveedor")
            return
        }
        if (!estado.esNoRecogida && estado.volumenLitros <= 0.0) {
            _uiState.value = estado.copy(mensajeError = "Ingresa el volumen en litros")
            return
        }
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            val estadoEntrega = if (estado.esNoRecogida) {
                EstadoEntrega.NoRecogida(motivo = estado.motivoNoRecogida)
            } else {
                EstadoEntrega.Pendiente
            }
            val entrega = Entrega(
                id = "E-${Clock.System.now().toEpochMilliseconds()}",
                proveedorId = proveedor.id,
                acopiadorId = acopiadorId,
                centroAcopioId = centroAcopioId,
                fecha = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                turno = estado.turno,
                volumenLitros = if (estado.esNoRecogida) 0.0 else estado.volumenLitros,
                estado = estadoEntrega,
                cantidadPorongos = if (estado.esNoRecogida) 0 else estado.cantidadPorongos,
            )
            entregaRepository.registrar(entrega = entrega)
            actualizarEstadoParadaEnRuta(proveedorId = proveedor.id, esNoRecogida = estado.esNoRecogida, fecha = entrega.fecha)
            generarResumenDiarioSiFalta(proveedorId = proveedor.id, fecha = entrega.fecha)
            _uiState.value = _uiState.value.copy(
                guardando = false,
                entregaGuardada = entrega.aResumen(proveedores = listOf(proveedor), sincronizada = false),
            )
        }
    }

    private suspend fun actualizarEstadoParadaEnRuta(proveedorId: String, esNoRecogida: Boolean, fecha: LocalDate) {
        val ruta = rutaRepository.observarRutaDelDia(acopiadorId = acopiadorId, fecha = fecha).first() ?: return
        val paradasModificadas = ruta.paradas.map { parada ->
            if (parada.proveedorId == proveedorId) {
                parada.copy(estadoParada = if (esNoRecogida) EstadoParada.OMITIDO else EstadoParada.VISITADO)
            } else {
                parada
            }
        }
        rutaRepository.actualizarRuta(ruta.copy(paradas = paradasModificadas))
    }

    private suspend fun generarResumenDiarioSiFalta(proveedorId: String, fecha: LocalDate) {
        val yaExiste = notificacionRepository.observarDe(proveedorId).first()
            .any { it.tipo == TipoNotificacion.RESUMEN_ENTREGA_DIARIA && it.fechaEnvio.date == fecha }
        if (yaExiste) return

        val litrosHoy = entregaRepository.observarEntregasDeHoy().first()
            .filter { it.proveedorId == proveedorId }
            .sumOf { it.volumenLitros }
        notificacionRepository.registrar(
            GeneradorNotificaciones.resumenEntregaDiaria(
                id = "N-${Clock.System.now().toEpochMilliseconds()}",
                proveedorId = proveedorId,
                litrosHoy = litrosHoy,
                fecha = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            ),
        )
    }
}
