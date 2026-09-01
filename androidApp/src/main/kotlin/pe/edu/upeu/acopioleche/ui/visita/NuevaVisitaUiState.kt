package pe.edu.upeu.acopioleche.ui.visita

import pe.edu.upeu.acopioleche.domain.model.Proveedor

data class NuevaVisitaUiState(
    val proveedorSeleccionado: Proveedor? = null,
    val textoBusqueda: String = "",
    val mostrarBusqueda: Boolean = false,
    val tipoVisita: String = "Calidad",
    val resultado: String = "Óptimo",
    val observaciones: String = "",
    val guardado: Boolean = false,
) {
    val puedeGuardar: Boolean get() = proveedorSeleccionado != null
}
