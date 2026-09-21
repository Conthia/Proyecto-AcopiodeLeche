package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.CapacitacionCorrectivaEntity
import pe.edu.upeu.acopioleche.domain.model.CapacitacionCorrectiva
import pe.edu.upeu.acopioleche.domain.repository.CapacitacionCorrectivaRepository

class SqlCapacitacionCorrectivaRepository(
    database: AcopioLecheDatabase,
) : CapacitacionCorrectivaRepository {

    private val queries = database.capacitacionCorrectivaQueries

    override fun observarPendientes(): Flow<List<CapacitacionCorrectiva>> =
        queries.selectPendientes().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun registrar(capacitacion: CapacitacionCorrectiva) {
        queries.insertar(
            id = capacitacion.id,
            proveedorId = capacitacion.proveedorId,
            motivo = capacitacion.motivo,
            fecha = capacitacion.fecha.toString(),
            atendida = if (capacitacion.atendida) 1L else 0L,
        )
    }

    private fun CapacitacionCorrectivaEntity.toDomain(): CapacitacionCorrectiva =
        CapacitacionCorrectiva(
            id = id,
            proveedorId = proveedorId,
            motivo = motivo,
            fecha = LocalDate.parse(fecha),
            atendida = atendida != 0L,
        )
}
