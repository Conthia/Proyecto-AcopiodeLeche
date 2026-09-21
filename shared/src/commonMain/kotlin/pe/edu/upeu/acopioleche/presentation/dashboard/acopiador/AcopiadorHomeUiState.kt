package pe.edu.upeu.acopioleche.presentation.dashboard.acopiador

import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas

data class AcopiadorHomeUiState(
    val nombreAcopiador: String = "",
    val litrosHoy: Double = 0.0,
    val numeroEntregas: Int = 0,
    val numeroPendientes: Int = 0,
    val rutaActual: RutaAcopio? = null,
    val ultimasEntregas: List<EntregaResumen> = emptyList(),
    val proveedoresPendientesHoy: List<ProveedorConEntregas> = emptyList(),
)
