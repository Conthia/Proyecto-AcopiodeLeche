package pe.edu.upeu.acopioleche

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pe.edu.upeu.acopioleche.data.sqldelight.DesktopDatabaseDriverFactory
import pe.edu.upeu.acopioleche.data.sync.DesktopConnectivityObserver
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.dashboard.acopiador.AcopiadorHomeViewModel

/**
 * Ventana mínima de Desktop/JVM: demuestra que el segundo target de KMP compila y reutiliza el
 * mismo dominio y ViewModels de `shared` que Android, sin duplicar las 8 pantallas completas
 * (fuera del alcance pedido para este target). Ver docs/modelo-dominio.md, sección
 * "Decisiones de alcance".
 */
fun main() = application {
    ServiceLocator.init(
        driverFactory = DesktopDatabaseDriverFactory(),
        connectivityObserver = DesktopConnectivityObserver(),
    )
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    Window(onCloseRequest = ::exitApplication, title = "AcopioLeche — Desktop (exploratorio)") {
        MaterialTheme {
            Surface {
                val viewModel = remember {
                    AcopiadorHomeViewModel(
                        scope = scope,
                        entregaRepository = ServiceLocator.entregaRepository,
                        proveedorRepository = ServiceLocator.proveedorRepository,
                        rutaRepository = ServiceLocator.rutaRepository,
                        nombreAcopiador = "Juan Mamani",
                    )
                }
                val uiState by viewModel.uiState.collectAsState()

                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "AcopioLeche · Acopio de hoy", style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Acopiador · ${uiState.nombreAcopiador}")
                    Text(text = "${uiState.litrosHoy} litros hoy · ${uiState.numeroEntregas} entregas · ${uiState.numeroPendientes} sin enviar")
                    Text(text = "Este target valida que el dominio y los ViewModels de shared/ son reutilizables fuera de Android.")
                }
            }
        }
    }
}
