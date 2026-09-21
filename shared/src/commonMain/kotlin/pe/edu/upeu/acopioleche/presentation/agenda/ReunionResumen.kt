package pe.edu.upeu.acopioleche.presentation.agenda

import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.TipoEvento

data class ReunionResumen(
    val id: String,
    val tipo: TipoEvento,
    val tema: String,
    val fecha: LocalDate,
    val horaInicioMinutos: Int,
    val horaFinMinutos: Int,
    val lugar: String,
    val yaOcurrio: Boolean,
    val numeroConvocados: Int,
    val numeroAsistentes: Int,
)
