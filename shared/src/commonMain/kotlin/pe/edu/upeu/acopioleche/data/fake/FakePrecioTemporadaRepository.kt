package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.PrecioVigente
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion

class FakePrecioTemporadaRepository : PrecioTemporadaRepository {

    private val _precios: MutableStateFlow<List<PrecioTemporada>> = MutableStateFlow(seed())
    private val precios: StateFlow<List<PrecioTemporada>> = _precios.asStateFlow()

    override fun observarPrecios(): StateFlow<List<PrecioTemporada>> = precios

    override suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioVigente {
        val coincidentes = _precios.value.filter { fecha >= it.fechaInicio && fecha <= it.fechaFin }
        val masReciente = coincidentes.maxByOrNull { it.fechaInicio }
        return if (masReciente != null) {
            PrecioVigente(precioPorLitro = masReciente.precioPorLitro, esRespaldo = false)
        } else {
            PrecioVigente(precioPorLitro = CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO, esRespaldo = true)
        }
    }

    override suspend fun guardar(precio: PrecioTemporada) {
        _precios.value = _precios.value.filterNot { it.id == precio.id } + precio
    }

    // Datos de ejemplo NO confirmados por el cliente (ver PENDIENTES.md): fechas y precios de
    // temporada inventados para poder probar el flujo. Tampoco existe hoy edición ni eliminación
    // de precios de temporada por el Administrador (`PrecioTemporadaRepository` solo tiene
    // `guardar`; `PagosHomeScreen.kt` únicamente permite crear uno nuevo y listarlos).
    private fun seed(): List<PrecioTemporada> =
        listOf(
            PrecioTemporada(
                id = "PT-2026-01",
                nombreTemporada = "Alta producción (Lluvias)",
                fechaInicio = LocalDate(2026, 1, 1),
                fechaFin = LocalDate(2026, 6, 30),
                precioPorLitro = 1.60,
            ),
            PrecioTemporada(
                id = "PT-2026-02",
                nombreTemporada = "Baja producción (Estiaje)",
                fechaInicio = LocalDate(2026, 7, 1),
                fechaFin = LocalDate(2026, 12, 31),
                precioPorLitro = 1.90,
            ),
        )
}
