package pe.edu.upeu.acopioleche.presentation.notificacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

/** Bandeja de notificaciones para el Administrador (RF-11/14/15/16): todo lo generado para todos los proveedores. */
class NotificacionesViewModel(
    scope: CoroutineScope,
    private val notificacionRepository: NotificacionRepository,
    proveedorRepository: ProveedorRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState.asStateFlow()

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
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun onMarcarLeidaClick(notificacionId: String) {
        scope.launch { notificacionRepository.marcarLeida(notificacionId) }
    }
}
