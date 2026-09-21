package pe.edu.upeu.acopioleche.presentation.notificacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

/** Bandeja de notificaciones para el Administrador (RF-11/14/15/16): todo lo generado para todos los proveedores. */
class NotificacionesViewModel(
    scope: CoroutineScope,
    private val notificacionRepository: NotificacionRepository,
    proveedorRepository: ProveedorRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<NotificacionesUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<NotificacionesUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                notificacionRepository.observarTodas(),
                proveedorRepository.observarProveedores(),
            ) { notificaciones, proveedores ->
                NotificacionesUiState(
                    notificaciones = notificaciones.map { notificacion ->
                        val proveedor = proveedores.find { it.id == notificacion.destinatarioId }
                        NotificacionResumen(
                            id = notificacion.id,
                            nombreDestinatario = proveedor?.nombre ?: notificacion.destinatarioId,
                            tipo = notificacion.tipo,
                            mensaje = notificacion.mensaje,
                            fechaEnvio = notificacion.fechaEnvio,
                            sonidoDistintivo = notificacion.sonidoDistintivo,
                            leida = notificacion.leida,
                        )
                    },
                )
            }
                .map<NotificacionesUiState, UiState<NotificacionesUiState>> { estado ->
                    if (estado.notificaciones.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar notificaciones", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun onMarcarLeidaClick(notificacionId: String) {
        scope.launch { notificacionRepository.marcarLeida(notificacionId) }
    }

    private companion object {
        const val TAG = "NotificacionesViewModel"
    }
}
