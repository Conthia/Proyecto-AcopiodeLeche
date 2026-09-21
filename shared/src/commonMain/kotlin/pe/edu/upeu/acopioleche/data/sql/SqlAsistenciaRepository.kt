package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.AsistenciaEntity
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository

class SqlAsistenciaRepository(
    database: AcopioLecheDatabase,
) : AsistenciaRepository {

    private val queries = database.asistenciaQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { marcarSinFlow(it) }
        }
    }

    override fun observarAsistencia(reunionId: String): Flow<List<Asistencia>> =
        queries.selectPorReunion(reunionId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun marcar(asistencia: Asistencia) {
        marcarSinFlow(asistencia)
    }

    private fun marcarSinFlow(asistencia: Asistencia) {
        queries.marcar(
            id = asistencia.id,
            reunionId = asistencia.reunionId,
            actorId = asistencia.actorId,
            tipoActor = asistencia.tipoActor.name,
            presente = if (asistencia.presente) 1L else 0L,
        )
    }

    private fun AsistenciaEntity.toDomain(): Asistencia =
        Asistencia(
            id = id,
            reunionId = reunionId,
            actorId = actorId,
            tipoActor = TipoActor.valueOf(tipoActor),
            presente = presente != 0L,
        )

    private fun seed(): List<Asistencia> =
        CONVOCADOS_R08.map { actorId ->
            Asistencia(id = "AS-R08-$actorId", reunionId = "R-08", actorId = actorId, tipoActor = TipoActor.PROVEEDOR, presente = false)
        } + CONVOCADOS_R06.mapIndexed { index, actorId ->
            Asistencia(
                id = "AS-R06-$actorId",
                reunionId = "R-06",
                actorId = actorId,
                tipoActor = TipoActor.PROVEEDOR,
                presente = index < 4,
            )
        }

    private companion object {
        val CONVOCADOS_R08 = listOf("P-014", "P-027", "P-003", "P-041", "P-052", "P-008")
        val CONVOCADOS_R06 = listOf("P-014", "P-027", "P-003", "P-041", "P-052", "P-008")
    }
}
