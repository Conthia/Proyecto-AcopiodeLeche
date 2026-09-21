package pe.edu.upeu.acopioleche.presentation.reportes

data class ReportesUiState(
    val reportes: List<ReporteDescriptor> = ReportesCatalogo.DISPONIBLES,
    val volumenPorComunidad: List<VolumenComunidad> = emptyList(),
)
