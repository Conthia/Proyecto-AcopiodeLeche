package pe.edu.upeu.acopioleche.ui.dashboard.tecnico

enum class EstadoIndicador { OK, ALERTA }

data class IndicadorCalidad(
    val etiqueta: String,
    val valor: String,
    val meta: String,
    val estado: EstadoIndicador,
)
