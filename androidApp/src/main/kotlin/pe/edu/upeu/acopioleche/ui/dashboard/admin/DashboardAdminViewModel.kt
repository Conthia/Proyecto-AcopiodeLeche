package pe.edu.upeu.acopioleche.ui.dashboard.admin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.MoradoAdministradorFondo
import pe.edu.upeu.acopioleche.ui.theme.MoradoAdministradorOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro

private val ESTADISTICAS_DEMO = listOf(
    EstadisticaAdmin("Litros hoy", "342.5", "L", AzulSecundario, AzulFondoTarjeta),
    EstadisticaAdmin("Proveedores", "47", "", VerdeExitoOscuro, VerdeExitoFondo),
    EstadisticaAdmin("Acopiadores", "8", "", MoradoAdministradorOscuro, MoradoAdministradorFondo),
    EstadisticaAdmin("Promedio/prov.", "7.3", "L", AmbarTexto, AmarilloAlertaFondo),
)

private val ACTIVIDAD_DEMO = listOf(
    ActividadReciente("Juan Quispe entregó 28.5L", "Hace 12 min", TipoActividad.ENTREGA),
    ActividadReciente("Rosa Flores registrada como nueva proveedora", "Hace 34 min", TipoActividad.REGISTRO),
    ActividadReciente("Capacitación Higiene confirmada", "Hace 1h", TipoActividad.EVENTO),
    ActividadReciente("Carlos Mamani sincronizó 5 registros", "Hace 2h", TipoActividad.SINCRONIZACION),
)

class DashboardAdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardAdminUiState(
            fechaHoy = "31 de agosto de 2026",
            estadisticas = ESTADISTICAS_DEMO,
            actividadReciente = ACTIVIDAD_DEMO,
            cantidadProveedores = DatosDemo.proveedores.size,
            cantidadAcopiadores = 8,
            cantidadEventos = DatosDemo.eventosIniciales.size,
        ),
    )
    val uiState: StateFlow<DashboardAdminUiState> = _uiState.asStateFlow()
}
