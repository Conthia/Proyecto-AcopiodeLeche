package pe.edu.upeu.acopioleche.presentation.notificacion

data class NotificacionesUiState(
    val notificaciones: List<NotificacionResumen> = emptyList(),
) {
    val numeroNoLeidas: Int get() = notificaciones.count { !it.leida }
}
