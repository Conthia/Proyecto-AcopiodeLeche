package pe.edu.upeu.acopioleche.presentation.reportes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class ReportesViewModel(
    scope: CoroutineScope,
    centroAcopioRepository: CentroAcopioRepository,
    entregaRepository: EntregaRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()

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
                litrosPorCentro.map { (nombre, litros) ->
                    VolumenComunidad(nombreCentro = nombre, litros = litros, proporcion = (litros / maximo).toFloat())
                }
            }.collect { volumen ->
                _uiState.value = _uiState.value.copy(volumenPorComunidad = volumen)
            }
        }
    }

    fun onGenerarReporte(reporte: ReporteDescriptor) {
        _uiState.value = _uiState.value.copy(mensaje = "Generando ${reporte.formato} de \"${reporte.titulo}\"…")
    }
}
