package pe.edu.upeu.acopioleche.ui.asistencia

import pe.edu.upeu.acopioleche.domain.model.Reunion

data class ControlAsistenciaUiState(
    val reunion: Reunion,
    val asistentes: List<AsistenteUi> = emptyList(),
    val presentes: Map<String, Boolean> = emptyMap(),
) {
    val totalPresentes: Int get() = presentes.values.count { it }
    val totalAusentes: Int get() = asistentes.size - totalPresentes
    val porcentajePresentes: Int
        get() = if (asistentes.isEmpty()) 0 else (totalPresentes * 100) / asistentes.size
}
