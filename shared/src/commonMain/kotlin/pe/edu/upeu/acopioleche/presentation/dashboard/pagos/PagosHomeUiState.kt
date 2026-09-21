package pe.edu.upeu.acopioleche.presentation.dashboard.pagos

import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.model.PagoEntrega
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas

data class PagosHomeUiState(
    val nombreEncargado: String = "",
    val centroAcopioId: String? = null,
    val liquidacionesSemana: List<Liquidacion> = emptyList(),
    val proveedores: List<ProveedorConEntregas> = emptyList(),
    val pagosRegistrados: List<PagoEntrega> = emptyList(),
    val preciosTemporada: List<PrecioTemporada> = emptyList(),
    val totalMontoPagadoSemana: Double = 0.0,
    val totalMontoPendienteSemana: Double = 0.0,
    val guardando: Boolean = false,
    val mensajeNotificacion: String? = null,
    val mensajeError: String? = null,
)
