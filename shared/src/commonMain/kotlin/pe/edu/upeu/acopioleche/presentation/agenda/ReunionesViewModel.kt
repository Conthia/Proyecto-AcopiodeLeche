package pe.edu.upeu.acopioleche.presentation.agenda

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

@OptIn(ExperimentalCoroutinesApi::class)
class ReunionesViewModel(
    scope: CoroutineScope,
    private val reunionRepository: ReunionRepository,
    private val asistenciaRepository: AsistenciaRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<ReunionesUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<ReunionesUiState>> = _uiState.asStateFlow()

    init {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        scope.launch {
            reunionRepository.observarReuniones()
                .flatMapLatest { reuniones ->
                    if (reuniones.isEmpty()) {
                        MutableStateFlow(ReunionesUiState())
                    } else {
                        combine(reuniones.map { asistenciaRepository.observarAsistencia(it.id) }) { listasAsistencia ->
                            ReunionesUiState(
                                reuniones = reuniones.mapIndexed { index, reunion ->
                                    val asistencia = listasAsistencia[index]
                                    ReunionResumen(
                                        id = reunion.id,
                                        tipo = reunion.tipo,
                                        tema = reunion.tema,
                                        fecha = reunion.fecha,
                                        horaInicioMinutos = reunion.horaInicioMinutos,
                                        horaFinMinutos = reunion.horaFinMinutos,
                                        lugar = reunion.lugar,
                                        yaOcurrio = reunion.fecha < hoy,
                                        numeroConvocados = asistencia.size,
                                        numeroAsistentes = asistencia.count { it.presente },
                                    )
                                },
                            )
                        }
                    }
                }
                // .catch va al final de TODA la cadena (después del flatMapLatest) para que
                // también atrape fallos de los flujos internos de asistencia, no solo de
                // observarReuniones().
                .map<ReunionesUiState, UiState<ReunionesUiState>> { estado ->
                    if (estado.reuniones.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar reuniones", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun guardarReunion(reunion: Reunion) {
        scope.launch {
            reunionRepository.guardar(reunion)
        }
    }

    fun eliminarReunion(id: String, alDesestimarConAsistencia: () -> Unit, alEliminarDefinitivo: () -> Unit) {
        scope.launch {
            val asistencia = asistenciaRepository.observarAsistencia(id).first()
            val tieneAsistenciaRegistrada = asistencia.any { it.presente }
            if (tieneAsistenciaRegistrada) {
                alDesestimarConAsistencia()
            } else {
                reunionRepository.eliminar(id)
                alEliminarDefinitivo()
            }
        }
    }

    private companion object {
        const val TAG = "ReunionesViewModel"
    }
}
