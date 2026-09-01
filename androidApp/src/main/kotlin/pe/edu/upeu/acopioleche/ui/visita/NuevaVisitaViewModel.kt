package pe.edu.upeu.acopioleche.ui.visita

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.ui.data.DatosDemo

class NuevaVisitaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaVisitaUiState())
    val uiState: StateFlow<NuevaVisitaUiState> = _uiState.asStateFlow()

    val proveedoresFiltrados: List<Proveedor>
        get() {
            val texto = _uiState.value.textoBusqueda.lowercase()
            return DatosDemo.proveedores.filter {
                it.nombre.lowercase().contains(texto) || it.sector.lowercase().contains(texto)
            }
        }

    fun cambiarBusqueda(texto: String) = _uiState.update { it.copy(textoBusqueda = texto, mostrarBusqueda = true) }
    fun mostrarBusqueda(mostrar: Boolean) = _uiState.update { it.copy(mostrarBusqueda = mostrar) }

    fun seleccionarProveedor(proveedor: Proveedor) = _uiState.update {
        it.copy(proveedorSeleccionado = proveedor, textoBusqueda = "", mostrarBusqueda = false)
    }

    fun quitarProveedor() = _uiState.update {
        it.copy(proveedorSeleccionado = null, textoBusqueda = "", mostrarBusqueda = true)
    }

    fun seleccionarTipoVisita(tipo: String) = _uiState.update { it.copy(tipoVisita = tipo) }
    fun seleccionarResultado(resultado: String) = _uiState.update { it.copy(resultado = resultado) }
    fun cambiarObservaciones(texto: String) = _uiState.update { it.copy(observaciones = texto) }

    fun guardar() {
        if (!_uiState.value.puedeGuardar) return
        _uiState.update { it.copy(guardado = true) }
    }

    fun nuevaVisita() = _uiState.update {
        NuevaVisitaUiState()
    }
}
