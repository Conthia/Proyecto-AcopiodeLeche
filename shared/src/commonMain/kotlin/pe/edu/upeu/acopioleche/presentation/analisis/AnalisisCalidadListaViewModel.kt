package pe.edu.upeu.acopioleche.presentation.analisis

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

/** Lista, para el Administrador, las entregas de hoy que aún no tienen análisis de calidad (RF-04/05). */
class AnalisisCalidadListaViewModel(
    scope: CoroutineScope,
    entregaRepository: EntregaRepository,
    proveedorRepository: ProveedorRepository,
    analisisCalidadRepository: AnalisisCalidadRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<AnalisisCalidadListaUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<AnalisisCalidadListaUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                entregaRepository.observarEntregasDeHoy(),
                proveedorRepository.observarProveedores(),
                analisisCalidadRepository.observarAnalisisRecientes(),
            ) { entregas, proveedores, analisis ->
                val entregasAnalizadas = analisis.map { it.entregaId }.toSet()
                AnalisisCalidadListaUiState(
                    pendientes = entregas.filter { entrega -> entrega.id !in entregasAnalizadas }.map { entrega ->
                        val proveedor = proveedores.find { it.id == entrega.proveedorId }
                        EntregaPendienteAnalisis(
                            entregaId = entrega.id,
                            nombreProveedor = proveedor?.nombre ?: entrega.proveedorId,
                            volumenLitros = entrega.volumenLitros,
                        )
                    },
                )
            }
                .map<AnalisisCalidadListaUiState, UiState<AnalisisCalidadListaUiState>> { estado ->
                    if (estado.pendientes.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar entregas pendientes de análisis", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    private companion object {
        const val TAG = "AnalisisCalidadListaViewModel"
    }
}
