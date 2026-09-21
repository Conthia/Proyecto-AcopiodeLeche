package pe.edu.upeu.acopioleche.presentation.dashboard.admin

import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.model.EquipoCampo

data class VolumenSector(
    val sector: String,
    val litros: Double,
)

data class PanelControlUiState(
    val litrosSemana: Double = 0.0,
    val proveedoresActivos: Int = 0,
    val grasaPromedioPorcentaje: Double = 0.0,
    val numeroAlertasAgua: Int = 0,
    val volumenPorDia: List<Double> = emptyList(),
    val volumenPorSectorHoy: List<VolumenSector> = emptyList(),
    val centrosConAtencion: List<CentroAcopio> = emptyList(),
    val alertas: List<AlertaCalidad> = emptyList(),
    val equipos: List<EquipoCampo> = emptyList(),
)
