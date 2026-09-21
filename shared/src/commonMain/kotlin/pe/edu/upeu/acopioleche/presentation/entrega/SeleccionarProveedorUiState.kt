package pe.edu.upeu.acopioleche.presentation.entrega

import pe.edu.upeu.acopioleche.domain.model.Proveedor

data class SeleccionarProveedorUiState(
    val textoBusqueda: String = "",
    val proveedores: List<Proveedor> = emptyList(),
) {
    val proveedoresFiltrados: List<Proveedor>
        get() = if (textoBusqueda.isBlank()) {
            proveedores
        } else {
            proveedores.filter { proveedor ->
                proveedor.nombre.contains(other = textoBusqueda, ignoreCase = true) ||
                    proveedor.id.contains(other = textoBusqueda, ignoreCase = true)
            }
        }
}
