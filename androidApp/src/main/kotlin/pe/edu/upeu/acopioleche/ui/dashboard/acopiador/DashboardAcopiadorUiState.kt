package pe.edu.upeu.acopioleche.ui.dashboard.acopiador

import pe.edu.upeu.acopioleche.ui.entrega.EntregaUi

data class DashboardAcopiadorUiState(
    val nombreUsuario: String = "",
    val sectoresAsignados: List<String> = emptyList(),
    val entregas: List<EntregaUi> = emptyList(),
    val horaActual: String = "",
) {
    val litrosHoy: Double get() = entregas.sumOf { it.entrega.volumenLitros }
    val cantidadEntregas: Int get() = entregas.size
}
