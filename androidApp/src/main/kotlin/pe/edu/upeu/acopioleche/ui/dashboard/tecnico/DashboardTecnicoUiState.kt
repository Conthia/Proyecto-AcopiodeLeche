package pe.edu.upeu.acopioleche.ui.dashboard.tecnico

data class DashboardTecnicoUiState(
    val visitasProgramadasHoy: Int = 3,
    val indicadores: List<IndicadorCalidad> = emptyList(),
    val visitasRecientes: List<VisitaResumen> = emptyList(),
)
