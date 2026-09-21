package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository

class FakeAnalisisCalidadRepository : AnalisisCalidadRepository {

    private val _analisis: MutableStateFlow<List<AnalisisCalidad>> = MutableStateFlow(seed())

    private val analisis: StateFlow<List<AnalisisCalidad>> = _analisis.asStateFlow()

    override fun observarAnalisisRecientes(): StateFlow<List<AnalisisCalidad>> = analisis

    override suspend fun registrar(analisis: AnalisisCalidad) {
        _analisis.value = listOf(analisis) + _analisis.value
    }

    private fun seed(): List<AnalisisCalidad> =
        listOf(
            AnalisisCalidad(
                id = "AC-2200",
                entregaId = "E-1040",
                tecnicoId = "T-01",
                fecha = LocalDateTime(2026, 9, 5, 5, 20),
                resultado = ResultadoAnalisis.Normal(
                    densidad = 1.031,
                    acidez = 0.0,
                    grasa = 3.6,
                    proteina = 3.2,
                    lactosa = 4.6,
                    temperatura = 4.0,
                    ph = 6.7,
                ),
            ),
            AnalisisCalidad(
                id = "AC-2201",
                entregaId = "E-1039",
                tecnicoId = "T-01",
                fecha = LocalDateTime(2026, 9, 5, 5, 12),
                resultado = ResultadoAnalisis.Normal(
                    densidad = 1.030,
                    acidez = 0.0,
                    grasa = 3.3,
                    proteina = 3.1,
                    lactosa = 4.5,
                    temperatura = 4.5,
                    ph = 6.7,
                ),
            ),
            AnalisisCalidad(
                id = "AC-2202",
                entregaId = "E-1038",
                tecnicoId = "T-01",
                fecha = LocalDateTime(2026, 9, 5, 5, 5),
                resultado = ResultadoAnalisis.Adulterada(
                    indicio = "Agua añadida detectada: 6.5 % · proveedor P-008",
                    porcentajeAgua = 6.5,
                ),
            ),
            AnalisisCalidad(
                id = "AC-2203",
                entregaId = "E-1041",
                tecnicoId = "T-01",
                fecha = LocalDateTime(2026, 9, 5, 5, 31),
                resultado = ResultadoAnalisis.FueraDeRango(
                    motivo = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO,
                    valorMedido = 1.027,
                    rangoPermitido = 1.028..1.034,
                ),
            ),
        )
}
