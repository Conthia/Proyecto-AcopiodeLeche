package pe.edu.upeu.acopioleche.presentation.conciliacion

data class ConciliacionListaUiState(
    val pendientes: List<EntregaPendienteConciliacion> = emptyList(),
)
