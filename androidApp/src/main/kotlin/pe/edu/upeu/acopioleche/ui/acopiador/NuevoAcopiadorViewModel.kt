package pe.edu.upeu.acopioleche.ui.acopiador

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Acopiador

class NuevoAcopiadorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoAcopiadorUiState())
    val uiState: StateFlow<NuevoAcopiadorUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor) }
    fun seleccionarVehiculo(valor: String) = _uiState.update { it.copy(vehiculo = valor) }

    fun alternarSector(sector: String) = _uiState.update {
        val actuales = it.sectoresSeleccionados
        it.copy(
            sectoresSeleccionados = if (sector in actuales) actuales - sector else actuales + sector,
        )
    }

    fun guardar() {
        val estado = _uiState.value
        if (!estado.puedeGuardar) return

        Acopiador(
            id = "acop-${System.currentTimeMillis()}",
            nombre = estado.nombre,
            vehiculo = estado.vehiculo,
            sectoresAsignados = estado.sectoresSeleccionados,
        )
        _uiState.update { it.copy(guardado = true) }
    }

    fun nuevoRegistro() {
        _uiState.value = NuevoAcopiadorUiState()
    }
}
