package pe.edu.upeu.acopioleche.presentation.agenda

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class AsistenciaViewModel(
    scope: CoroutineScope,
    private val asistenciaRepository: AsistenciaRepository,
    proveedorRepository: ProveedorRepository,
    reunionRepository: ReunionRepository,
    private val reunionId: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(AsistenciaUiState())
    val uiState: StateFlow<AsistenciaUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                reunionRepository.observarReuniones(),
                asistenciaRepository.observarAsistencia(reunionId = reunionId),
                proveedorRepository.observarProveedores(),
            ) { reuniones, asistencias, proveedores ->
                val reunion = reuniones.find { it.id == reunionId }
                AsistenciaUiState(
                    temaReunion = reunion?.tema.orEmpty(),
                    fechaLugar = if (reunion != null) "${reunion.fecha} · ${reunion.lugar}" else "",
                    convocados = asistencias.map { asistencia ->
                        val proveedor = proveedores.find { it.id == asistencia.actorId }
                        ConvocadoUiModel(
                            actorId = asistencia.actorId,
                            nombre = proveedor?.nombre ?: asistencia.actorId,
                            meta = "${asistencia.actorId} · ${proveedor?.sector.orEmpty()}",
                            presente = asistencia.presente,
                        )
                    },
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun onToggleConvocado(actorId: String) {
        scope.launch {
            val actual = _uiState.value.convocados.find { it.actorId == actorId } ?: return@launch
            asistenciaRepository.marcar(
                asistencia = Asistencia(
                    id = "AS-$reunionId-$actorId",
                    reunionId = reunionId,
                    actorId = actorId,
                    tipoActor = TipoActor.PROVEEDOR,
                    presente = !actual.presente,
                ),
            )
        }
    }

    fun onEscanearQr() {
        scope.launch {
            val siguiente = _uiState.value.convocados.firstOrNull { !it.presente } ?: return@launch
            onToggleConvocado(actorId = siguiente.actorId)
        }
    }

    fun onCerrarActa() {
        _uiState.value = _uiState.value.copy(actaCerrada = true)
    }
}
