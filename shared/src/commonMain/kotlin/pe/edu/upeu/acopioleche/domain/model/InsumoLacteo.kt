package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

data class InsumoLacteo(
    val id: String,
    val nombreInsumo: String,
    val cantidad: Double,
    val unidadMedida: String,
    val fechaIngreso: LocalDate,
)
