package pe.edu.upeu.acopioleche.ui.dashboard.admin

enum class TipoActividad { ENTREGA, REGISTRO, EVENTO, SINCRONIZACION }

data class ActividadReciente(
    val descripcion: String,
    val tiempoRelativo: String,
    val tipo: TipoActividad,
)
