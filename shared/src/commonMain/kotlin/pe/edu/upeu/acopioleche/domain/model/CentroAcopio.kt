package pe.edu.upeu.acopioleche.domain.model

data class CentroAcopio(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val capacidadLitrosDia: Double,
    val activo: Boolean,
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del centro de acopio no puede estar vacio" }
        require(capacidadLitrosDia > 0) { "La capacidad del centro de acopio debe ser mayor a cero" }
    }
}
