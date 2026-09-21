package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.Notificacion
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.service.GeneradorNotificaciones

class FakeNotificacionRepository : NotificacionRepository {

    private val _notificaciones: MutableStateFlow<List<Notificacion>> = MutableStateFlow(seed())

    private val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    override fun observarTodas(): Flow<List<Notificacion>> = notificaciones

    override fun observarDe(destinatarioId: String): Flow<List<Notificacion>> =
        notificaciones.map { lista -> lista.filter { it.destinatarioId == destinatarioId } }

    override suspend fun registrar(notificacion: Notificacion) {
        _notificaciones.value = listOf(notificacion) + _notificaciones.value
    }

    override suspend fun marcarLeida(notificacionId: String) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == notificacionId) it.copy(leida = true) else it
        }
    }

    // RF-11/14/15/16: CITACION_REUNION para los proveedores convocados a la próxima reunión
    // (R-08), la misma lista de convocados que usa FakeAsistenciaRepository.
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
