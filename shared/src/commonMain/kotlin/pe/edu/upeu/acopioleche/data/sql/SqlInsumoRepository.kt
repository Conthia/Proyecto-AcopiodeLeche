package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.InsumoLacteoEntity
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.repository.InsumoRepository

class SqlInsumoRepository(
    database: AcopioLecheDatabase,
) : InsumoRepository {

    private val queries = database.insumoLacteoQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { guardarSync(it) }
        }
    }

    override fun observarInsumos(): Flow<List<InsumoLacteo>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun guardar(insumo: InsumoLacteo) {
        guardarSync(insumo)
    }

    override suspend fun eliminar(id: String) {
        queries.eliminar(id)
    }

    private fun guardarSync(insumo: InsumoLacteo) {
        queries.insertar(
            id = insumo.id,
            nombreInsumo = insumo.nombreInsumo,
            cantidad = insumo.cantidad,
            unidadMedida = insumo.unidadMedida,
            fechaIngreso = insumo.fechaIngreso.toString(),
        )
    }

    private fun InsumoLacteoEntity.toDomain(): InsumoLacteo =
        InsumoLacteo(
            id = id,
            nombreInsumo = nombreInsumo,
            cantidad = cantidad,
            unidadMedida = unidadMedida,
            fechaIngreso = LocalDate.parse(fechaIngreso),
        )

    private fun seed(): List<InsumoLacteo> =
        listOf(
            InsumoLacteo(
                id = "INS-01",
                nombreInsumo = "Cuajo Líquido Grado A",
                cantidad = 5.0,
                unidadMedida = "Litros",
                fechaIngreso = LocalDate(2026, 9, 1),
            ),
            InsumoLacteo(
                id = "INS-02",
                nombreInsumo = "Sal de Mar Granulada",
                cantidad = 50.0,
                unidadMedida = "Kg",
                fechaIngreso = LocalDate(2026, 9, 5),
            ),
            InsumoLacteo(
                id = "INS-03",
                nombreInsumo = "Cultivo Láctico para Yogurt",
                cantidad = 20.0,
                unidadMedida = "Sobres",
                fechaIngreso = LocalDate(2026, 9, 8),
            ),
        )
}
