package pe.edu.upeu.acopioleche.presentation.agenda

data class AsistenciaUiState(
    val temaReunion: String = "",
    val fechaLugar: String = "",
    val convocados: List<ConvocadoUiModel> = emptyList(),
) {
    val numeroPresentes: Int get() = convocados.count { it.presente }
}
