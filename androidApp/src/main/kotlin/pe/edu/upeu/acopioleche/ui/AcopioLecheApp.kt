package pe.edu.upeu.acopioleche.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pe.edu.upeu.acopioleche.ui.acopiador.NuevoAcopiadorScreen
import pe.edu.upeu.acopioleche.ui.agenda.AgendaScreen
import pe.edu.upeu.acopioleche.ui.asistencia.ControlAsistenciaScreen
import pe.edu.upeu.acopioleche.ui.common.RolUsuario
import pe.edu.upeu.acopioleche.ui.dashboard.acopiador.DashboardAcopiadorScreen
import pe.edu.upeu.acopioleche.ui.dashboard.admin.DashboardAdminScreen
import pe.edu.upeu.acopioleche.ui.dashboard.tecnico.DashboardTecnicoScreen
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.entrega.RegistrarEntregaScreen
import pe.edu.upeu.acopioleche.ui.login.LoginScreen
import pe.edu.upeu.acopioleche.ui.nav.Pantalla
import pe.edu.upeu.acopioleche.ui.proveedor.NuevoProveedorScreen
import pe.edu.upeu.acopioleche.ui.theme.AcopioLecheTheme
import pe.edu.upeu.acopioleche.ui.theme.FondoGeneral
import pe.edu.upeu.acopioleche.ui.visita.NuevaVisitaScreen

@Composable
fun AcopioLecheApp() {
    AcopioLecheTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = FondoGeneral) {
            var pantalla by remember { mutableStateOf<Pantalla>(Pantalla.Login) }
            var rolActivo by remember { mutableStateOf<RolUsuario?>(null) }

            fun irAlMenu() { pantalla = Pantalla.Menu }
            fun cerrarSesion() { rolActivo = null; pantalla = Pantalla.Login }

            when (val destino = pantalla) {
                Pantalla.Login -> LoginScreen(
                    onLoginExitoso = { rol ->
                        rolActivo = rol
                        pantalla = Pantalla.Menu
                    },
                )

                Pantalla.Menu -> when (rolActivo) {
                    RolUsuario.TECNICO -> DashboardTecnicoScreen(
                        onIrAAgenda = { pantalla = Pantalla.Agenda },
                        onIrANuevaVisita = { pantalla = Pantalla.NuevaVisita },
                        onIrAAsistenciaRapida = { pantalla = Pantalla.ControlAsistencia(DatosDemo.eventosIniciales.first()) },
                        onIrAProveedores = { pantalla = Pantalla.NuevoProveedor },
                        onCerrarSesion = ::cerrarSesion,
                    )
                    RolUsuario.ADMINISTRADOR -> DashboardAdminScreen(
                        onIrAProveedores = { pantalla = Pantalla.NuevoProveedor },
                        onIrAAcopiadores = { pantalla = Pantalla.NuevoAcopiador },
                        onIrAAgenda = { pantalla = Pantalla.Agenda },
                        onIrAEntregas = { pantalla = Pantalla.RegistrarEntrega },
                        onCerrarSesion = ::cerrarSesion,
                    )
                    RolUsuario.ACOPIADOR, null -> DashboardAcopiadorScreen(
                        onIrARegistrarEntrega = { pantalla = Pantalla.RegistrarEntrega },
                        onIrAAgenda = { pantalla = Pantalla.Agenda },
                        onIrANuevoProveedor = { pantalla = Pantalla.NuevoProveedor },
                        onCerrarSesion = ::cerrarSesion,
                    )
                }

                Pantalla.NuevoProveedor -> NuevoProveedorScreen(onVolver = ::irAlMenu)
                Pantalla.NuevoAcopiador -> NuevoAcopiadorScreen(onVolver = ::irAlMenu)
                Pantalla.RegistrarEntrega -> RegistrarEntregaScreen(onVolver = ::irAlMenu)
                Pantalla.Agenda -> AgendaScreen(
                    onVolver = ::irAlMenu,
                    onIrAAsistencia = { reunion -> pantalla = Pantalla.ControlAsistencia(reunion) },
                )
                is Pantalla.ControlAsistencia -> ControlAsistenciaScreen(
                    reunion = destino.reunion,
                    onVolver = { pantalla = Pantalla.Agenda },
                )
                Pantalla.NuevaVisita -> NuevaVisitaScreen(onVolver = ::irAlMenu)
            }
        }
    }
}
