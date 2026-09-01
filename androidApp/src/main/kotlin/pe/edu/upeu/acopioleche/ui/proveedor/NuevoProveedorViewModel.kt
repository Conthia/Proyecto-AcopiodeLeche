package pe.edu.upeu.acopioleche.ui.proveedor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Proveedor

class NuevoProveedorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoProveedorUiState())
    val uiState: StateFlow<NuevoProveedorUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor) }
    fun cambiarDocumento(valor: String) = _uiState.update { it.copy(documento = valor) }
    fun cambiarTelefono(valor: String) = _uiState.update { it.copy(telefono = valor) }
    fun seleccionarSector(valor: String) = _uiState.update { it.copy(sector = valor) }
    fun seleccionarModalidad(directaEnPlanta: Boolean) =
        _uiState.update { it.copy(entregaDirectaEnPlanta = directaEnPlanta) }

    fun guardar() {
        val estado = _uiState.value
        if (!estado.puedeGuardar) return

        Proveedor(
            id = "prov-${System.currentTimeMillis()}",
            nombre = estado.nombre,
            documento = estado.documento,
            telefono = estado.telefono,
            sector = estado.sector,
            entregaDirectaEnPlanta = estado.entregaDirectaEnPlanta,
        )
        _uiState.update { it.copy(guardado = true) }
    }

    fun nuevoRegistro() {
        _uiState.value = NuevoProveedorUiState()
    }
}
