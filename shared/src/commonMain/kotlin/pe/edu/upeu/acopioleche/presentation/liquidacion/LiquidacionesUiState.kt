package pe.edu.upeu.acopioleche.presentation.liquidacion

import kotlinx.datetime.LocalDate

data class LiquidacionesUiState(
    val semanaInicio: LocalDate? = null,
    val fechaPago: LocalDate? = null,
    val resumenes: List<LiquidacionResumen> = emptyList(),
    val generando: Boolean = false,
    val mensaje: String? = null,
)
