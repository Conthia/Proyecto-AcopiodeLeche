package pe.edu.upeu.acopioleche.presentation.analisis

/**
 * Proyección de presentación, no una entidad de dominio: una [pe.edu.upeu.acopioleche.domain.model.Entrega]
 * de hoy que todavía no tiene un [pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad] asociado.
 */
data class EntregaPendienteAnalisis(
    val entregaId: String,
    val nombreProveedor: String,
    val volumenLitros: Double,
)
