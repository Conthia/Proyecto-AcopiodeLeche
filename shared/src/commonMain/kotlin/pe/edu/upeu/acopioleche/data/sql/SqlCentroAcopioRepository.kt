package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.CentroAcopioEntity
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository

/**
 * Implementación real con SQLDelight (RNF-03): mismo contrato de [CentroAcopioRepository],
 * ahora respaldado por una tabla que sobrevive al cierre de la app. `FakeCentroAcopioRepository`
 * se conserva para tests — ver docs/modelo-dominio.md, sección "Decisiones de arquitectura".
 */
class SqlCentroAcopioRepository(
    database: AcopioLecheDatabase,
) : CentroAcopioRepository {

    private val queries = database.centroAcopioQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { centro -> insertar(centro) }
        }
    }

    override fun observarCentros(): Flow<List<CentroAcopio>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun guardar(centro: CentroAcopio) {
        insertar(centro)
    }

    override suspend fun actualizar(centro: CentroAcopio) {
        insertar(centro)
    }

    override suspend fun eliminar(id: String) {
        queries.eliminar(id)
    }

    private fun insertar(centro: CentroAcopio) {
        queries.insertar(
            id = centro.id,
            nombre = centro.nombre,
            ubicacion = centro.ubicacion,
            capacidadLitrosDia = centro.capacidadLitrosDia,
            activo = if (centro.activo) 1L else 0L,
            capacidadTanqueLitros = centro.capacidadTanqueLitros,
        )
    }

    private fun CentroAcopioEntity.toDomain(): CentroAcopio =
        CentroAcopio(
            id = id,
            nombre = nombre,
            ubicacion = ubicacion,
            capacidadLitrosDia = capacidadLitrosDia,
            activo = activo != 0L,
            capacidadTanqueLitros = capacidadTanqueLitros,
        )

    private fun seed(): List<CentroAcopio> =
        listOf(
            CentroAcopio(
                id = "CA-001",
                nombre = "Huata Centro",
                ubicacion = "Plaza principal",
                capacidadLitrosDia = 450.0,
                activo = true,
                capacidadTanqueLitros = 600.0,
            ),
            CentroAcopio(
                id = "CA-002",
                nombre = "Coyme",
                ubicacion = "Local comunal",
                capacidadLitrosDia = 320.0,
                activo = true,
                capacidadTanqueLitros = 300.0,
            ),
            CentroAcopio(
                id = "CA-003",
                nombre = "Pallalla",
                ubicacion = "Módulo asociativo",
                capacidadLitrosDia = 250.0,
                activo = true,
                capacidadTanqueLitros = 400.0,
            ),
        )
}
