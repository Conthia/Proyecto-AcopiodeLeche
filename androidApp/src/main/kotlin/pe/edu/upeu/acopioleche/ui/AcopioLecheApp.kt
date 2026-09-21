package pe.edu.upeu.acopioleche.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.R
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.ui.components.LocalAbrirMenuAdmin
import pe.edu.upeu.acopioleche.ui.nav.AcopiadorBottomBar
import pe.edu.upeu.acopioleche.ui.nav.AcopioLecheNavHost
import pe.edu.upeu.acopioleche.ui.nav.Ruta
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun AcopioLecheApp() {
    val navController = rememberNavController()
    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route
    val enLogin = rutaActual == null || rutaActual == Ruta.Login.ruta
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val esAdmin = sesion?.rol == RolUsuario.ADMINISTRADOR

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val contenido = @Composable {
        Scaffold(
            bottomBar = {
                val s = sesion
                if (!enLogin && s != null) {
                    when (s.rol) {
                        RolUsuario.ACOPIADOR -> {
                            AcopiadorBottomBar(navController = navController)
                        }
                        RolUsuario.PRODUCTOR -> {}
                        RolUsuario.ENCARGADO_PAGOS -> {}
                        RolUsuario.PRODUCTOR_LACTEOS -> {}
                        RolUsuario.ADMINISTRADOR -> {}
                    }
                }
            },
            // Este Scaffold solo reserva espacio para la barra de navegación propia
            // (AcopiadorBottomBar, que ya respeta el inset del sistema por su cuenta).
            // Los insets reales de status bar / navigation bar del sistema se manejan
            // en cada pantalla (AppTopBar para el superior, el Scaffold propio de cada
            // pantalla para el inferior) para evitar que se consuman aquí y nunca
            // lleguen a aplicarse como espacio visible.
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            Column(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
                AcopioLecheNavHost(navController = navController, sesionActual = sesion)
            }
        }
    }

    if (!enLogin && esAdmin) {
        CompositionLocalProvider(LocalAbrirMenuAdmin provides { scope.launch { drawerState.open() } }) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = true,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(280.dp).fillMaxHeight(),
                        drawerContainerColor = Color.White,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VerdeOscuro)
                                .padding(20.dp),
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(R.drawable.logo_ecolacteos),
                                    contentDescription = "Logo de EcoLácteos Huata",
                                    modifier = Modifier.size(60.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "AcopioLeche Rural",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = sesion?.nombreCompleto ?: "Administrador",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val items = listOf(
                            Triple("Panel de control", Ruta.PanelControl.ruta, Icons.Filled.Home),
                            Triple("Proveedores", Ruta.Proveedores.ruta, Icons.Filled.People),
                            Triple("Centros de acopio", Ruta.CentrosAcopio.ruta, Icons.Filled.LocationOn),
                            Triple("Asignar Rutas", Ruta.AsignarRuta.ruta, Icons.Filled.LocationOn),
                            Triple("Reuniones", Ruta.Reuniones.ruta, Icons.Filled.DateRange),
                            Triple("Reportes", Ruta.Reportes.ruta, Icons.Filled.Description),
                            Triple("Calidad", Ruta.AnalisisCalidad.ruta, Icons.Filled.Science),
                            Triple("Mi Perfil", Ruta.Perfil.ruta, Icons.Filled.Person),
                        )

                        items.forEach { (label, ruta, icon) ->
                            val seleccionado = rutaActual == ruta
                            NavigationDrawerItem(
                                label = { Text(label) },
                                selected = seleccionado,
                                icon = { Icon(imageVector = icon, contentDescription = label) },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    if (rutaActual != ruta) {
                                        navController.navigate(ruta) {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = VerdeOscuro.copy(alpha = 0.15f),
                                    selectedIconColor = VerdeOscuro,
                                    selectedTextColor = VerdeOscuro,
                                ),
                            )
                        }
                    }
                },
                content = contenido,
            )
        }
    } else {
        contenido()
    }
}
