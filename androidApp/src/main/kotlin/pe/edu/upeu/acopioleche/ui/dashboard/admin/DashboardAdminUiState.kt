package pe.edu.upeu.acopioleche.ui.dashboard.admin

data class DashboardAdminUiState(
    val fechaHoy: String = "",
    val estadisticas: List<EstadisticaAdmin> = emptyList(),
    val actividadReciente: List<ActividadReciente> = emptyList(),
    val cantidadProveedores: Int = 0,
    val cantidadAcopiadores: Int = 0,
    val cantidadEventos: Int = 0,
)
