package pe.edu.upeu.acopioleche.presentation.entrega

import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen

data class EntregasDelDiaUiState(
    val totalLitros: Double = 0.0,
    val numeroProveedoresAtendidos: Int = 0,
    val entregas: List<EntregaResumen> = emptyList(),
)
