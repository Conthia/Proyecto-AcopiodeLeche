package pe.edu.upeu.acopioleche.presentation.conciliacion

/** Proyección de presentación: una entrega de hoy que todavía no tiene volumen de planta (RF-21). */
data class EntregaPendienteConciliacion(
    val entregaId: String,
    val nombreProveedor: String,
    val volumenCampoLitros: Double,
)
