package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime

data class Notificacion(
    val id: String,
    val destinatarioId: String,
    val tipo: TipoNotificacion,
    val mensaje: String,
    val fechaEnvio: LocalDateTime,
    val sonidoDistintivo: Boolean = true,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos.
    // Necesario para que la bandeja de notificaciones (Fase 7) distinga leídas de no leídas.
    val leida: Boolean = false,
)
