package pe.edu.upeu.acopioleche.ui.dashboard.tecnico

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val INDICADORES_DEMO = listOf(
    IndicadorCalidad("Acidez promedio", "16.2°D", "14–18°D", EstadoIndicador.OK),
    IndicadorCalidad("Temperatura recepción", "6.8°C", "< 8°C", EstadoIndicador.OK),
    IndicadorCalidad("Densidad promedio", "1.029", "1.028–1.034", EstadoIndicador.ALERTA),
    IndicadorCalidad("Proveedores con obs.", "3", "< 2 ideal", EstadoIndicador.ALERTA),
)

private val VISITAS_DEMO = listOf(
    VisitaResumen("v1", "Juan Quispe Mamani", "Hoy 09:30", "Calidad", "Óptimo"),
    VisitaResumen("v2", "Rosa Flores Ccapa", "Hoy 11:00", "Sanidad", "Observación"),
    VisitaResumen("v3", "Pedro Apaza Ticona", "Ayer 14:00", "Calidad", "Óptimo"),
)

class DashboardTecnicoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardTecnicoUiState(indicadores = INDICADORES_DEMO, visitasRecientes = VISITAS_DEMO),
    )
    val uiState: StateFlow<DashboardTecnicoUiState> = _uiState.asStateFlow()
}
