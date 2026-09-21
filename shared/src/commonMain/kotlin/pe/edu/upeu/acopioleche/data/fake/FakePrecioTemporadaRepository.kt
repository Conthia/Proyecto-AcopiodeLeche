package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository

class FakePrecioTemporadaRepository : PrecioTemporadaRepository {

    private val _precios: MutableStateFlow<List<PrecioTemporada>> = MutableStateFlow(seed())
    private val precios: StateFlow<List<PrecioTemporada>> = _precios.asStateFlow()

    override fun observarPrecios(): StateFlow<List<PrecioTemporada>> = precios

    override suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): Double {
        val coincidentes = _precios.value.filter { fecha >= it.fechaInicio && fecha <= it.fechaFin }
        val masReciente = coincidentes.maxByOrNull { it.fechaInicio }
        return masReciente?.precioPorLitro ?: PRECIO_BASE_FALLBACK
    }

    override suspend fun guardar(precio: PrecioTemporada) {
        _precios.value = _precios.value.filterNot { it.id == precio.id } + precio
    }

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

    companion object {
        const val PRECIO_BASE_FALLBACK: Double = 1.80
    }
}
