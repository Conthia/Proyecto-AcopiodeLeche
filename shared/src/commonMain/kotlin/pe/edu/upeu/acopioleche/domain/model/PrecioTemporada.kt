package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

data class PrecioTemporada(
    val id: String,
    val nombreTemporada: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val precioPorLitro: Double,
)
