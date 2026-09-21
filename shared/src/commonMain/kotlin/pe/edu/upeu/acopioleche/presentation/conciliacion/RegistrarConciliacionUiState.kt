package pe.edu.upeu.acopioleche.presentation.conciliacion

data class RegistrarConciliacionUiState(
    val entregaId: String = "",
    val nombreProveedor: String = "",
    val volumenCampoLitros: Double = 0.0,
    val volumenPlantaTexto: String = "",
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val diferenciaLitros: Double? = null,
) {
    val volumenPlanta: Double? get() = volumenPlantaTexto.toDoubleOrNull()
}
