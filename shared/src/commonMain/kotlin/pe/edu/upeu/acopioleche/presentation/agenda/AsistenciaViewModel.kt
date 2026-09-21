package pe.edu.upeu.acopioleche.presentation.agenda

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

class AsistenciaViewModel(
    scope: CoroutineScope,
    private val asistenciaRepository: AsistenciaRepository,
    proveedorRepository: ProveedorRepository,
    reunionRepository: ReunionRepository,
    private val reunionId: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<AsistenciaUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<AsistenciaUiState>> = _uiState.asStateFlow()

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
            }
                .map<AsistenciaUiState, UiState<AsistenciaUiState>> { estado ->
                    if (estado.convocados.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar asistencia", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun onToggleConvocado(actorId: String) {
        scope.launch {
            val estado = _uiState.value as? UiState.Exito ?: return@launch
            val actual = estado.datos.convocados.find { it.actorId == actorId } ?: return@launch
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
            val estado = _uiState.value as? UiState.Exito ?: return@launch
            val siguiente = estado.datos.convocados.firstOrNull { !it.presente } ?: return@launch
            onToggleConvocado(actorId = siguiente.actorId)
        }
    }

    fun onCerrarActa() {
        val estado = _uiState.value
        if (estado is UiState.Exito) {
            _uiState.value = estado.copy(datos = estado.datos.copy(actaCerrada = true))
        }
    }

    private companion object {
        const val TAG = "AsistenciaViewModel"
    }
}
