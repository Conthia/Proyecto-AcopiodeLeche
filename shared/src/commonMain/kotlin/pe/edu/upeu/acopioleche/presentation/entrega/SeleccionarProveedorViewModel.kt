package pe.edu.upeu.acopioleche.presentation.entrega

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class SeleccionarProveedorViewModel(
    scope: CoroutineScope,
    proveedorRepository: ProveedorRepository,
    rutaRepository: RutaRepository,
    acopiadorId: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(SeleccionarProveedorUiState())
    val uiState: StateFlow<SeleccionarProveedorUiState> = _uiState.asStateFlow()

    init {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        scope.launch {
            combine(
                proveedorRepository.observarProveedores(),
                rutaRepository.observarRutaDelDia(acopiadorId = acopiadorId, fecha = hoy),
            ) { proveedores, rutaDelDia ->
                // Los proveedores disponibles para registrar una entrega son los de la ruta que
                // el admin asignó HOY (RF-15), no un padrón fijo aparte — de lo contrario una
                // ruta recién asignada no serviría para nada en este flujo.
                val idsDeLaRuta = rutaDelDia?.paradas.orEmpty().map { it.proveedorId }.toSet()
                // RN-11/RN-12 (Fase 4): un proveedor retirado del padrón (activo = false) no debe
                // poder recibir nuevas entregas aunque siga en la lista de paradas.
                proveedores.filter { it.activo && idsDeLaRuta.contains(it.id) }
            }.collect { proveedores -> _uiState.value = _uiState.value.copy(proveedores = proveedores) }
        }
    }

    fun onTextoBusquedaChange(texto: String) {
        _uiState.value = _uiState.value.copy(textoBusqueda = texto)
    }
}
