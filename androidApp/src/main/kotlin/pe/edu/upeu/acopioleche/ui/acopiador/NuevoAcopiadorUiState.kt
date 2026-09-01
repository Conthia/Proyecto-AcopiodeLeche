package pe.edu.upeu.acopioleche.ui.acopiador

data class NuevoAcopiadorUiState(
    val nombre: String = "",
    val vehiculo: String = "Turbón",
    val sectoresSeleccionados: List<String> = emptyList(),
    val guardado: Boolean = false,
) {
    val puedeGuardar: Boolean get() = nombre.isNotBlank() && sectoresSeleccionados.isNotEmpty()
}
