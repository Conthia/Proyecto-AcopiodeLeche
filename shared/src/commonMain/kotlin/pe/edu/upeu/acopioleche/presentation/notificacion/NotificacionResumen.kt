package pe.edu.upeu.acopioleche.presentation.notificacion

import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion

/** Proyección de presentación: une una Notificacion con el nombre de su destinatario. */
data class NotificacionResumen(
    val id: String,
    val nombreDestinatario: String,
    val tipo: TipoNotificacion,
    val mensaje: String,
    val fechaEnvio: LocalDateTime,
    val sonidoDistintivo: Boolean,
    val leida: Boolean,
)
