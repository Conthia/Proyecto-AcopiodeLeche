package pe.edu.upeu.acopioleche.ui.asistencia

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.ui.data.DatosDemo

class ControlAsistenciaViewModel(reunion: Reunion) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ControlAsistenciaUiState(reunion = reunion, asistentes = DatosDemo.asistentes),
    )
    val uiState: StateFlow<ControlAsistenciaUiState> = _uiState.asStateFlow()

    fun alternarPresente(asistenteId: String) = _uiState.update {
        val actual = it.presentes[asistenteId] ?: false
        it.copy(presentes = it.presentes + (asistenteId to !actual))
    }

    fun construirRegistrosDeAsistencia(): List<Asistencia> {
        val estado = _uiState.value
        return estado.asistentes.map { asistente ->
            Asistencia(
                id = "asist-${estado.reunion.id}-${asistente.id}",
                reunionId = estado.reunion.id,
                actorId = asistente.id,
                tipoActor = asistente.tipoActor,
                presente = estado.presentes[asistente.id] ?: false,
            )
        }
    }
}
