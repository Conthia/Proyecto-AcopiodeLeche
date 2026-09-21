package pe.edu.upeu.acopioleche.presentation.dashboard.productor

import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen

data class ProductorHomeUiState(
    val proveedorId: String = "",
    val nombreProductor: String = "",
    val litrosHoy: Double = 0.0,
    val litrosAcumuladoSemana: Double = 0.0,
    val misEntregas: List<EntregaResumen> = emptyList(),
    val misAnalisis: List<AnalisisCalidad> = emptyList(),
    val misLiquidaciones: List<Liquidacion> = emptyList(),
)
