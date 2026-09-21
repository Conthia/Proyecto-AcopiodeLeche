package pe.edu.upeu.acopioleche.presentation.dashboard.admin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.model.Sector
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class PanelControlViewModel(
    scope: CoroutineScope,
    entregaRepository: EntregaRepository,
    proveedorRepository: ProveedorRepository,
    analisisCalidadRepository: AnalisisCalidadRepository,
    equipoCampoRepository: EquipoCampoRepository,
    centroAcopioRepository: CentroAcopioRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(PanelControlUiState())
    val uiState: StateFlow<PanelControlUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                combine(
                    entregaRepository.observarVolumenUltimaSemana(),
                    entregaRepository.observarEntregasDeHoy(),
                ) { volumenSemana, entregasHoy -> volumenSemana to entregasHoy },
                proveedorRepository.observarProveedores(),
                analisisCalidadRepository.observarAnalisisRecientes(),
                equipoCampoRepository.observarEquipos(),
                centroAcopioRepository.observarCentros(),
            ) { (volumenSemana, entregasHoy), proveedores, analisis, equipos, centros ->
                val proveedorMap = proveedores.associateBy { it.id }

                val distribucionSectores = Sector.TODOS.map { sectorNombre ->
                    val litrosSector = entregasHoy
                        .filter { proveedorMap[it.proveedorId]?.sector == sectorNombre }
                        .sumOf { it.volumenLitros }
                    VolumenSector(sector = sectorNombre, litros = litrosSector)
                }

                val centrosAtencion = centros.filter { !it.activo }

                PanelControlUiState(
                    litrosSemana = volumenSemana.sum(),
                    proveedoresActivos = proveedores.size,
                    grasaPromedioPorcentaje = grasaPromedio(analisis = analisis),
                    numeroAlertasAgua = analisis.count { it.resultado is ResultadoAnalisis.Adulterada },
                    volumenPorDia = volumenSemana,
                    volumenPorSectorHoy = distribucionSectores,
                    centrosConAtencion = centrosAtencion,
                    alertas = analisis.mapNotNull { item ->
                        aAlerta(analisis = item, entregas = entregasHoy, proveedores = proveedores)
                    },
                    equipos = equipos,
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    private fun grasaPromedio(analisis: List<AnalisisCalidad>): Double {
        val normales = analisis.mapNotNull { (it.resultado as? ResultadoAnalisis.Normal)?.grasa }
        return if (normales.isEmpty()) 0.0 else normales.average()
    }

    private fun aAlerta(
        analisis: AnalisisCalidad,
        entregas: List<Entrega>,
        proveedores: List<Proveedor>,
    ): AlertaCalidad? {
        val entrega = entregas.find { it.id == analisis.entregaId }
        val proveedor = proveedores.find { it.id == entrega?.proveedorId }
        val nombreProveedor = proveedor?.nombre ?: entrega?.proveedorId ?: "Proveedor desconocido"

        return when (val resultado = analisis.resultado) {
            is ResultadoAnalisis.Adulterada -> AlertaCalidad(
                id = analisis.id,
                titulo = resultado.indicio,
                subtitulo = nombreProveedor,
            )

            is ResultadoAnalisis.FueraDeRango -> AlertaCalidad(
                id = analisis.id,
                titulo = "${etiquetaMotivo(resultado.motivo)}: ${resultado.valorMedido}",
                subtitulo = nombreProveedor,
            )

            is ResultadoAnalisis.Normal -> null
        }
    }

    private fun etiquetaMotivo(motivo: MotivoRechazo): String =
        when (motivo) {
            MotivoRechazo.DENSIDAD_FUERA_DE_RANGO -> "Densidad fuera de rango"
            MotivoRechazo.ACIDEZ_FUERA_DE_RANGO -> "Acidez fuera de rango"
            MotivoRechazo.GRASA_FUERA_DE_RANGO -> "Grasa fuera de rango"
            MotivoRechazo.ADULTERACION_DETECTADA -> "Adulteración detectada"
            MotivoRechazo.PROTEINA_FUERA_DE_RANGO -> "Proteína fuera de rango"
            MotivoRechazo.LACTOSA_FUERA_DE_RANGO -> "Lactosa fuera de rango"
            MotivoRechazo.TEMPERATURA_FUERA_DE_RANGO -> "Temperatura fuera de rango"
            MotivoRechazo.PH_FUERA_DE_RANGO -> "pH fuera de rango"
        }
}
