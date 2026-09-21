package pe.edu.upeu.acopioleche.presentation.reportes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

/**
 * `reportes` es el catálogo estático de [ReporteDescriptor] (siempre no vacío, ver
 * [ReportesCatalogo]) — información propia de la pantalla, independiente de `volumenPorComunidad`
 * (reactivo). Por eso el estado siempre es [UiState.Exito] una vez cargado, nunca [UiState.Vacio],
 * igual que la tarjeta de reunión en `AsistenciaViewModel`.
 */
class ReportesViewModel(
    scope: CoroutineScope,
    centroAcopioRepository: CentroAcopioRepository,
    entregaRepository: EntregaRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<ReportesUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<ReportesUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                centroAcopioRepository.observarCentros(),
                entregaRepository.observarEntregasDeHoy(),
            ) { centros, entregas ->
                val litrosPorCentro = centros.associate { centro ->
                    centro.nombre to entregas.filter { it.centroAcopioId == centro.id }.sumOf { it.volumenLitros }
                }
                val maximo = litrosPorCentro.values.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
                ReportesUiState(
                    volumenPorComunidad = litrosPorCentro.map { (nombre, litros) ->
                        VolumenComunidad(nombreCentro = nombre, litros = litros, proporcion = (litros / maximo).toFloat())
                    },
                )
            }
                .map<ReportesUiState, UiState<ReportesUiState>> { estado -> UiState.Exito(estado) }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar reportes", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    private companion object {
        const val TAG = "ReportesViewModel"
    }
}
