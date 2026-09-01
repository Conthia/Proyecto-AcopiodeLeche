package pe.edu.upeu.acopioleche.ui.proveedor

data class NuevoProveedorUiState(
    val nombre: String = "",
    val documento: String = "",
    val telefono: String = "",
    val sector: String = "",
    val entregaDirectaEnPlanta: Boolean = true,
    val guardado: Boolean = false,
) {
    val puedeGuardar: Boolean get() = nombre.isNotBlank() && documento.isNotBlank() && sector.isNotBlank()
}
