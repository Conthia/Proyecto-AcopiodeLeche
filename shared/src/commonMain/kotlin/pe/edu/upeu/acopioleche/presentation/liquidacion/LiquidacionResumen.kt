package pe.edu.upeu.acopioleche.presentation.liquidacion

/** Proyección de presentación, no de dominio: une un proveedor con su [pe.edu.upeu.acopioleche.domain.model.Liquidacion] (si ya existe) de la semana mostrada. */
data class LiquidacionResumen(
    val proveedorId: String,
    val nombreProveedor: String,
    val litrosAceptados: Double,
    val montoFinal: Double,
    val tieneSancionPendienteDeMonto: Boolean,
    val generada: Boolean,
)
