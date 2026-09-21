package pe.edu.upeu.acopioleche.presentation.dashboard.lacteos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.domain.repository.InsumoRepository
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

class LacteosHomeViewModel(
    scope: CoroutineScope,
    private val produccionDerivadoRepository: ProduccionDerivadoRepository,
    private val insumoRepository: InsumoRepository,
    private val nombreResponsable: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<LacteosHomeUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<LacteosHomeUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                produccionDerivadoRepository.observarProduccion(),
                insumoRepository.observarInsumos(),
            ) { producciones, insumos ->
                LacteosHomeUiState(
                    nombreResponsable = nombreResponsable,
                    producciones = producciones,
                    insumos = insumos,
                )
            }
                .map<LacteosHomeUiState, UiState<LacteosHomeUiState>> { estado ->
                    if (estado.producciones.isEmpty() && estado.insumos.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar producción de lácteos", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun registrarProduccion(produccion: ProduccionDerivado) {
        scope.launch { produccionDerivadoRepository.guardar(produccion) }
    }

    fun eliminarProduccion(id: String) {
        scope.launch { produccionDerivadoRepository.eliminar(id) }
    }

    fun registrarInsumo(insumo: InsumoLacteo) {
        scope.launch { insumoRepository.guardar(insumo) }
    }

    fun eliminarInsumo(id: String) {
        scope.launch { insumoRepository.eliminar(id) }
    }

    private companion object {
        const val TAG = "LacteosHomeViewModel"
    }
}
