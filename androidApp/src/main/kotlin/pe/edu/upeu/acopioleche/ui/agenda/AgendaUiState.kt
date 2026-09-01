package pe.edu.upeu.acopioleche.ui.agenda

import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.ui.common.horaAMinutosDelDia

data class AgendaUiState(
    val eventos: List<Reunion> = emptyList(),
    val convocadosPorEvento: Map<String, Int> = emptyMap(),
    val mostrarFormularioNuevo: Boolean = false,
    val nuevoTema: String = "",
    val nuevaFecha: String = "",
    val nuevaHoraInicio: String = "",
    val nuevaHoraFin: String = "",
) {
    val puedeCrearEvento: Boolean
        get() {
            if (nuevoTema.isBlank()) return false
            val inicio = nuevaHoraInicio.toMinutosOrNull() ?: return false
            val fin = nuevaHoraFin.toMinutosOrNull() ?: return false
            return fin > inicio
        }
}

private fun String.toMinutosOrNull(): Int? = runCatching { horaAMinutosDelDia(this) }.getOrNull()
