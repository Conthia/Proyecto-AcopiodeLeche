package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.Asistencia

interface AsistenciaRepository {
    fun observarAsistencia(reunionId: String): Flow<List<Asistencia>>

    suspend fun marcar(asistencia: Asistencia)
}
