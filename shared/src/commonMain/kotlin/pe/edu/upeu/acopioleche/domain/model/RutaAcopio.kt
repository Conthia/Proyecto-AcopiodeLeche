package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class RutaAcopio(
    val id: String,
    val nombre: String,
    val acopiadorId: String,
    val centroSectorId: String,
    val fecha: LocalDate,
    val paradas: List<ParadaRuta>,
    val estado: EstadoRuta = EstadoRuta.EN_CURSO,
    val volumenDescargadoLitros: Double? = null,
    val fechaHoraCierre: LocalDateTime? = null,
)
