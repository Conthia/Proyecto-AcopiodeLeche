package pe.edu.upeu.acopioleche.presentation.entrega

import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen

data class RegistrarEntregaUiState(
    val proveedor: Proveedor? = null,
    val volumenLitrosTexto: String = "",
    val turno: Turno = Turno.MANANA,
    val cantidadPorongos: Int = 1,
    val esNoRecogida: Boolean = false,
    val motivoNoRecogida: String = "No había leche / Sin producción",
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val entregaGuardada: EntregaResumen? = null,
) {
    val volumenLitros: Double get() = if (esNoRecogida) 0.0 else (volumenLitrosTexto.toDoubleOrNull() ?: 0.0)
}
