package pe.edu.upeu.acopioleche.domain.model

data class Entrega(
    val id: String,
    val proveedorId: String,
    val acopiadorId: String?,
    val centroAcopioId: String,
    val fecha: String,
    val turno: String,
    val volumenLitros: Double,
    val estado: EstadoEntrega,
) {
    init {
        require(proveedorId.isNotBlank()) { "La entrega debe estar asociada a un proveedor" }
        require(centroAcopioId.isNotBlank()) { "La entrega debe estar asociada a un centro de acopio" }
        require(turno.isNotBlank()) { "La entrega debe indicar un turno" }
        require(volumenLitros > 0) { "El volumen de la entrega debe ser mayor a cero" }
    }
}
