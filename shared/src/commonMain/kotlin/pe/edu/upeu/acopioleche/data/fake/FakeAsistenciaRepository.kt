package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository

class FakeAsistenciaRepository : AsistenciaRepository {

    private val _asistencias: MutableStateFlow<List<Asistencia>> = MutableStateFlow(seed())

    override fun observarAsistencia(reunionId: String): Flow<List<Asistencia>> =
        _asistencias.asStateFlow().map { lista -> lista.filter { it.reunionId == reunionId } }

    override suspend fun marcar(asistencia: Asistencia) {
        _asistencias.value = _asistencias.value
            .filterNot { it.reunionId == asistencia.reunionId && it.actorId == asistencia.actorId }
            .plus(asistencia)
    }

    private fun seed(): List<Asistencia> =
        CONVOCADOS_R08.map { actorId ->
            Asistencia(
                id = "AS-R08-$actorId",
                reunionId = "R-08",
                actorId = actorId,
                tipoActor = TipoActor.PROVEEDOR,
                presente = false,
            )
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
