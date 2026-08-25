package pe.edu.upeu.acopioleche.domain.model

data class Proveedor(
    val id: String,
    val nombre: String,
    val documento: String,
    val telefono: String,
    val sector: String,
    val entregaDirectaEnPlanta: Boolean,
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del proveedor no puede estar vacio" }
        require(documento.isNotBlank()) { "El documento del proveedor no puede estar vacio" }
        require(sector.isNotBlank()) { "El sector del proveedor no puede estar vacio" }
    }
}
