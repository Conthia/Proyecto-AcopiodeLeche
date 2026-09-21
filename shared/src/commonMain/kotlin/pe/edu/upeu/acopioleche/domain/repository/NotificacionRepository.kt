package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.Notificacion

interface NotificacionRepository {
    fun observarTodas(): Flow<List<Notificacion>>

    fun observarDe(destinatarioId: String): Flow<List<Notificacion>>

    suspend fun registrar(notificacion: Notificacion)

    suspend fun marcarLeida(notificacionId: String)
}
