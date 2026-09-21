package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.NotificacionEntity
import pe.edu.upeu.acopioleche.domain.model.Notificacion
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.service.GeneradorNotificaciones

class SqlNotificacionRepository(
    database: AcopioLecheDatabase,
) : NotificacionRepository {

    private val queries = database.notificacionQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { insertarSinFlow(it) }
        }
    }

    override fun observarTodas(): Flow<List<Notificacion>> =
        queries.selectTodas().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarDe(destinatarioId: String): Flow<List<Notificacion>> =
        queries.selectDeDestinatario(destinatarioId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun registrar(notificacion: Notificacion) {
        insertarSinFlow(notificacion)
    }

    override suspend fun marcarLeida(notificacionId: String) {
        queries.marcarLeida(notificacionId)
    }

    private fun insertarSinFlow(notificacion: Notificacion) {
        queries.insertar(
            id = notificacion.id,
            destinatarioId = notificacion.destinatarioId,
            tipo = notificacion.tipo.name,
            mensaje = notificacion.mensaje,
            fechaEnvio = notificacion.fechaEnvio.toString(),
            sonidoDistintivo = if (notificacion.sonidoDistintivo) 1L else 0L,
            leida = if (notificacion.leida) 1L else 0L,
        )
    }

    private fun NotificacionEntity.toDomain(): Notificacion =
        Notificacion(
            id = id,
            destinatarioId = destinatarioId,
            tipo = TipoNotificacion.valueOf(tipo),
            mensaje = mensaje,
            fechaEnvio = LocalDateTime.parse(fechaEnvio),
            sonidoDistintivo = sonidoDistintivo != 0L,
            leida = leida != 0L,
        )

    // RF-11/14/15/16: CITACION_REUNION para los proveedores convocados a la próxima reunión
    // (R-08), la misma lista de convocados que usa SqlAsistenciaRepository.
    private fun seed(): List<Notificacion> =
        CONVOCADOS_R08.mapIndexed { index, proveedorId ->
            GeneradorNotificaciones.citacionReunion(
                id = "N-R08-$proveedorId",
                proveedorId = proveedorId,
                tema = "Reunión mensual de productores",
                fechaReunion = LocalDate(2026, 9, 12),
                lugar = "Local comunal Huata Centro",
                fecha = LocalDateTime(2026, 9, 5, 9 + index, 0),
            )
        }

    private companion object {
        val CONVOCADOS_R08 = listOf("P-014", "P-027", "P-003", "P-041", "P-052", "P-008")
    }
}
