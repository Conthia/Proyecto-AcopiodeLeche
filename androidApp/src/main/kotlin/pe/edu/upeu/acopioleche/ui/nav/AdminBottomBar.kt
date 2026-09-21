package pe.edu.upeu.acopioleche.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AdminBottomBar(navController: NavHostController) {
    val actual = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = actual == Ruta.PanelControl.ruta,
            onClick = { navController.navigate(Ruta.PanelControl.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = null) },
            label = { Text("Panel") },
        )
        NavigationBarItem(
            selected = actual == Ruta.Proveedores.ruta,
            onClick = { navController.navigate(Ruta.Proveedores.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.People, contentDescription = null) },
            label = { Text("Proveedores") },
        )
        NavigationBarItem(
            selected = actual == Ruta.CentrosAcopio.ruta,
            onClick = { navController.navigate(Ruta.CentrosAcopio.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null) },
            label = { Text("Centros") },
        )
        NavigationBarItem(
            selected = actual == Ruta.Reuniones.ruta,
            onClick = { navController.navigate(Ruta.Reuniones.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.DateRange, contentDescription = null) },
            label = { Text("Reuniones") },
        )
        NavigationBarItem(
            selected = actual == Ruta.Reportes.ruta,
            onClick = { navController.navigate(Ruta.Reportes.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.Description, contentDescription = null) },
            label = { Text("Reportes") },
        )
        NavigationBarItem(
            selected = actual == Ruta.AnalisisCalidad.ruta,
            onClick = { navController.navigate(Ruta.AnalisisCalidad.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.Science, contentDescription = null) },
            label = { Text("Calidad") },
        )
    }
}
