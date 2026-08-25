package pe.edu.upeu.acopioleche.domain.model

data class Notificacion(
    val id: String,
    val destinatarioId: String,
    val tipo: TipoNotificacion,
    val mensaje: String,
    val fechaEnvio: String,
    val sonidoDistintivo: Boolean = true,
) {
    init {
        require(destinatarioId.isNotBlank()) { "La notificacion debe tener un destinatario" }
        require(mensaje.isNotBlank()) { "La notificacion debe tener un mensaje" }
    }
}
