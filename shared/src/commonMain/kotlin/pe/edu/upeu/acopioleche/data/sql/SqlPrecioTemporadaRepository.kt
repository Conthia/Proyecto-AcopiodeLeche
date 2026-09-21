package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.PrecioTemporadaEntity
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.PrecioVigente
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion

class SqlPrecioTemporadaRepository(
    database: AcopioLecheDatabase,
) : PrecioTemporadaRepository {

    private val queries = database.precioTemporadaQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { guardarSync(it) }
        }
    }

    override fun observarPrecios(): Flow<List<PrecioTemporada>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioVigente {
        val precios = observarPrecios().first()
        val coincidentes = precios.filter { fecha >= it.fechaInicio && fecha <= it.fechaFin }
        val masReciente = coincidentes.maxByOrNull { it.fechaInicio }
        return if (masReciente != null) {
            PrecioVigente(precioPorLitro = masReciente.precioPorLitro, esRespaldo = false)
        } else {
            PrecioVigente(precioPorLitro = CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO, esRespaldo = true)
        }
    }

    override suspend fun guardar(precio: PrecioTemporada) {
        guardarSync(precio)
    }

    private fun guardarSync(precio: PrecioTemporada) {
        queries.insertar(
            id = precio.id,
            nombreTemporada = precio.nombreTemporada,
            fechaInicio = precio.fechaInicio.toString(),
            fechaFin = precio.fechaFin.toString(),
            precioPorLitro = precio.precioPorLitro,
        )
    }

    private fun PrecioTemporadaEntity.toDomain(): PrecioTemporada =
        PrecioTemporada(
            id = id,
            nombreTemporada = nombreTemporada,
            fechaInicio = LocalDate.parse(fechaInicio),
            fechaFin = LocalDate.parse(fechaFin),
            precioPorLitro = precioPorLitro,
        )

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
