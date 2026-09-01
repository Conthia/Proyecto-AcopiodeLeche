package pe.edu.upeu.acopioleche.ui.nav

import pe.edu.upeu.acopioleche.domain.model.Reunion

sealed interface Pantalla {
    data object Login : Pantalla
    data object Menu : Pantalla
    data object NuevoProveedor : Pantalla
    data object NuevoAcopiador : Pantalla
    data object RegistrarEntrega : Pantalla
    data object Agenda : Pantalla
    data class ControlAsistencia(val reunion: Reunion) : Pantalla
    data object NuevaVisita : Pantalla
}
