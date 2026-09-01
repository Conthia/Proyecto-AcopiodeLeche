package pe.edu.upeu.acopioleche.ui.entrega

import pe.edu.upeu.acopioleche.domain.model.Proveedor

data class RegistrarEntregaUiState(
    val turno: String = "Mañana",
    val proveedorSeleccionado: Proveedor? = null,
    val textoBusqueda: String = "",
    val mostrarBusqueda: Boolean = false,
    val litros: String = "",
    val guardado: Boolean = false,
    val horaActual: String = "",
    val entregasRecientes: List<EntregaUi> = emptyList(),
) {
    val puedeGuardar: Boolean
        get() = proveedorSeleccionado != null && (litros.toDoubleOrNull() ?: 0.0) > 0.0
}
