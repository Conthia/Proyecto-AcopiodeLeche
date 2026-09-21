package pe.edu.upeu.acopioleche.presentation.dashboard.lacteos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.domain.repository.InsumoRepository
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class LacteosHomeViewModel(
    scope: CoroutineScope,
    private val produccionDerivadoRepository: ProduccionDerivadoRepository,
    private val insumoRepository: InsumoRepository,
    nombreResponsable: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(LacteosHomeUiState(nombreResponsable = nombreResponsable))
    val uiState: StateFlow<LacteosHomeUiState> = _uiState.asStateFlow()

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
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun registrarProduccion(produccion: ProduccionDerivado) {
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            produccionDerivadoRepository.guardar(produccion)
            _uiState.value = _uiState.value.copy(
                guardando = false,
                mensajeNotificacion = "Lote '${produccion.codigoLote}' (${produccion.tipoProducto}) registrado correctamente.",
            )
        }
    }

    fun eliminarProduccion(id: String) {
        scope.launch {
            produccionDerivadoRepository.eliminar(id)
            _uiState.value = _uiState.value.copy(mensajeNotificacion = "Lote de producción eliminado.")
        }
    }

    fun registrarInsumo(insumo: InsumoLacteo) {
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            insumoRepository.guardar(insumo)
            _uiState.value = _uiState.value.copy(
                guardando = false,
                mensajeNotificacion = "Insumo '${insumo.nombreInsumo}' (${insumo.cantidad} ${insumo.unidadMedida}) registrado.",
            )
        }
    }

    fun eliminarInsumo(id: String) {
        scope.launch {
            insumoRepository.eliminar(id)
            _uiState.value = _uiState.value.copy(mensajeNotificacion = "Registro de insumo eliminado.")
        }
    }
}
