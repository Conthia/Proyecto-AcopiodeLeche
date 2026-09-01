package pe.edu.upeu.acopioleche.ui.agenda

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.ui.common.horaAMinutosDelDia
import pe.edu.upeu.acopioleche.ui.data.DatosDemo

class AgendaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        AgendaUiState(
            eventos = DatosDemo.eventosIniciales,
            convocadosPorEvento = DatosDemo.convocadosPorEvento,
        ),
    )
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    fun mostrarFormularioNuevo(mostrar: Boolean) = _uiState.update { it.copy(mostrarFormularioNuevo = mostrar) }
    fun cambiarTema(valor: String) = _uiState.update { it.copy(nuevoTema = valor) }
    fun cambiarFecha(valor: String) = _uiState.update { it.copy(nuevaFecha = valor) }
    fun cambiarHoraInicio(valor: String) = _uiState.update { it.copy(nuevaHoraInicio = valor) }
    fun cambiarHoraFin(valor: String) = _uiState.update { it.copy(nuevaHoraFin = valor) }

    fun crearEvento() {
        val estado = _uiState.value
        if (!estado.puedeCrearEvento) return

        val nuevo = Reunion(
            id = "reunion-${System.currentTimeMillis()}",
            tipo = TipoEvento.REUNION,
            tema = estado.nuevoTema,
            fecha = estado.nuevaFecha.ifBlank { "Por definir" },
            horaInicioMinutos = horaAMinutosDelDia(estado.nuevaHoraInicio),
            horaFinMinutos = horaAMinutosDelDia(estado.nuevaHoraFin),
        )
        _uiState.update {
            it.copy(
                eventos = it.eventos + nuevo,
                convocadosPorEvento = it.convocadosPorEvento + (nuevo.id to 0),
                mostrarFormularioNuevo = false,
                nuevoTema = "",
                nuevaFecha = "",
                nuevaHoraInicio = "",
                nuevaHoraFin = "",
            )
        }
    }
}
