package pe.edu.upeu.acopioleche.presentation.centro

data class CentrosAcopioUiState(
    val centros: List<CentroConEstadisticas> = emptyList(),
) {
    val numeroActivos: Int get() = centros.count { it.operativo }
}
