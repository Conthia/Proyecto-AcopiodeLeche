package pe.edu.upeu.acopioleche.presentation.centro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.model.EstadoConexion
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

class CentrosAcopioViewModel(
    scope: CoroutineScope,
    private val centroAcopioRepository: CentroAcopioRepository,
    private val entregaRepository: EntregaRepository,
    private val equipoCampoRepository: EquipoCampoRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<CentrosAcopioUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<CentrosAcopioUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                centroAcopioRepository.observarCentros(),
                entregaRepository.observarEntregasDeHoy(),
                equipoCampoRepository.observarEquipos(),
            ) { centros, entregas, equipos ->
                CentrosAcopioUiState(
                    centros = centros.map { centro ->
                        val entregasDelCentro = entregas.filter { it.centroAcopioId == centro.id }
                        val sinConexion = equipos.any {
                            it.centroAcopioId == centro.id && it.estadoConexion is EstadoConexion.SinConexion
                        }
                        CentroConEstadisticas(
                            id = centro.id,
                            nombre = centro.nombre,
                            ubicacion = centro.ubicacion,
                            capacidadTanqueLitros = centro.capacidadTanqueLitros,
                            capacidadLitrosDia = centro.capacidadLitrosDia,
                            activo = centro.activo,
                            operativo = centro.activo && !sinConexion,
                            numeroProveedores = entregasDelCentro.map { it.proveedorId }.distinct().size,
                            litrosHoy = entregasDelCentro.sumOf { it.volumenLitros },
                            numeroAcopiadores = entregasDelCentro.mapNotNull { it.acopiadorId }.distinct().size,
                        )
                    },
                )
            }
                .map<CentrosAcopioUiState, UiState<CentrosAcopioUiState>> { estado ->
                    if (estado.centros.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar centros de acopio", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun guardarCentro(centro: CentroAcopio) {
        scope.launch {
            centroAcopioRepository.guardar(centro)
        }
    }

    fun toggleActivo(id: String) {
        scope.launch {
            val centro = centroAcopioRepository.observarCentros().first().find { it.id == id }
            if (centro != null) {
                centroAcopioRepository.actualizar(centro.copy(activo = !centro.activo))
            }
        }
    }

    fun eliminarCentro(id: String, alDesactivarPorDependencias: () -> Unit, alEliminarDefinitivo: () -> Unit) {
        scope.launch {
            val entregas = entregaRepository.observarEntregasDeHoy().first()
            val tieneEntregas = entregas.any { it.centroAcopioId == id }
            if (tieneEntregas) {
                val centro = centroAcopioRepository.observarCentros().first().find { it.id == id }
                if (centro != null) {
                    centroAcopioRepository.actualizar(centro.copy(activo = false))
                }
                alDesactivarPorDependencias()
            } else {
                centroAcopioRepository.eliminar(id)
                alEliminarDefinitivo()
            }
        }
    }

    private companion object {
        const val TAG = "CentrosAcopioViewModel"
    }
}
