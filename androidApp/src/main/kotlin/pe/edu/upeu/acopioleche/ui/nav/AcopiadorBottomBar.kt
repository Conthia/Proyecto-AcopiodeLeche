package pe.edu.upeu.acopioleche.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AcopiadorBottomBar(navController: NavHostController) {
    val actual = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = actual == Ruta.AcopiadorHome.ruta,
            onClick = { navController.navigate(Ruta.AcopiadorHome.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = null) },
            label = { androidx.compose.material3.Text("Inicio") },
        )
        NavigationBarItem(
            selected = actual == Ruta.EntregasDelDia.ruta,
            onClick = { navController.navigate(Ruta.EntregasDelDia.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.List, contentDescription = null) },
            label = { androidx.compose.material3.Text("Entregas") },
        )
        NavigationBarItem(
            selected = actual == Ruta.ColaEnvio.ruta,
            onClick = { navController.navigate(Ruta.ColaEnvio.ruta) { launchSingleTop = true } },
            icon = { Icon(imageVector = Icons.Filled.Sync, contentDescription = null) },
            label = { androidx.compose.material3.Text("Cola") },
        )
    }
}
