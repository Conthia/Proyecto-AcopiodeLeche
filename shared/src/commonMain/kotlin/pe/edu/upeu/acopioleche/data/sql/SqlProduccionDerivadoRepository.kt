package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.ProduccionDerivadoEntity
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository

class SqlProduccionDerivadoRepository(
    database: AcopioLecheDatabase,
) : ProduccionDerivadoRepository {

    private val queries = database.produccionDerivadoQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { guardarSync(it) }
        }
    }

    override fun observarProduccion(): Flow<List<ProduccionDerivado>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun guardar(produccion: ProduccionDerivado) {
        guardarSync(produccion)
    }

    override suspend fun eliminar(id: String) {
        queries.eliminar(id)
    }

    private fun guardarSync(p: ProduccionDerivado) {
        queries.insertar(
            id = p.id,
            tipoProducto = p.tipoProducto,
            codigoLote = p.codigoLote,
            cantidadUnidades = p.cantidadUnidades,
            fechaProduccion = p.fechaProduccion.toString(),
            responsableId = p.responsableId,
        )
    }

    private fun ProduccionDerivadoEntity.toDomain(): ProduccionDerivado =
        ProduccionDerivado(
            id = id,
            tipoProducto = tipoProducto,
            codigoLote = codigoLote,
            cantidadUnidades = cantidadUnidades,
            fechaProduccion = LocalDate.parse(fechaProduccion),
            responsableId = responsableId,
        )

    private fun seed(): List<ProduccionDerivado> =
        listOf(
            ProduccionDerivado(
                id = "PROD-DER-01",
                tipoProducto = "Queso Paria Fresco",
                codigoLote = "LOTE-2026-001",
                cantidadUnidades = 45.0,
                fechaProduccion = LocalDate(2026, 9, 10),
                responsableId = "PROD-LACT-01",
            ),
            ProduccionDerivado(
                id = "PROD-DER-02",
                tipoProducto = "Yogurt Bebible Frutado",
                codigoLote = "LOTE-2026-002",
                cantidadUnidades = 80.0,
                fechaProduccion = LocalDate(2026, 9, 12),
                responsableId = "PROD-LACT-01",
            ),
        )
}
