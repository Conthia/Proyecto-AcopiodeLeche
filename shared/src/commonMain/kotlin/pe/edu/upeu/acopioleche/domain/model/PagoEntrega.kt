package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime

data class PagoEntrega(
    val id: String,
    val liquidacionId: String,
    val proveedorId: String,
    val monto: Double,
    val fechaHora: LocalDateTime,
    val encargadoId: String,
    val metodoPago: String = "EFECTIVO",
    val notas: String = "",
)
