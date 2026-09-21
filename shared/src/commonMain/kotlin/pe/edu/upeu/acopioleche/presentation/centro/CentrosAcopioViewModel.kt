package pe.edu.upeu.acopioleche.presentation.centro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.model.EstadoConexion
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class CentrosAcopioViewModel(
    scope: CoroutineScope,
    private val centroAcopioRepository: CentroAcopioRepository,
    private val entregaRepository: EntregaRepository,
    private val equipoCampoRepository: EquipoCampoRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(CentrosAcopioUiState())
    val uiState: StateFlow<CentrosAcopioUiState> = _uiState.asStateFlow()

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
            }.collect { estado -> _uiState.value = estado }
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
}
