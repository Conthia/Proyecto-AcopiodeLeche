package pe.edu.upeu.acopioleche.ui.dashboard.acopiador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.data.RepositorioEntregasEnMemoria

class DashboardAcopiadorViewModel : ViewModel() {

    private val nombreUsuario = MutableStateFlow(DatosDemo.acopiadorActual.nombre)
    private val horaActual = MutableStateFlow("")

    val uiState: StateFlow<DashboardAcopiadorUiState> = combine(
        nombreUsuario,
        horaActual,
        RepositorioEntregasEnMemoria.entregas,
    ) { nombre, hora, entregas ->
        DashboardAcopiadorUiState(
            nombreUsuario = nombre,
            sectoresAsignados = DatosDemo.acopiadorActual.sectoresAsignados,
            entregas = entregas,
            horaActual = hora,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardAcopiadorUiState(nombreUsuario = DatosDemo.acopiadorActual.nombre),
    )

    fun actualizarHora(hora: String) {
        horaActual.value = hora
    }
}
