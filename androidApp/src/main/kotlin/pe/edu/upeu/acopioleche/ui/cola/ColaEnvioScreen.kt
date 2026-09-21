package pe.edu.upeu.acopioleche.ui.cola

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.cola.ColaEnvioViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EntregaRow
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun ColaEnvioScreen(alVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        ColaEnvioViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            syncCoordinator = ServiceLocator.syncCoordinator,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Cola de envío",
                subtitulo = "${uiState.numeroPendientes} registros locales",
                alVolver = alVolver,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AppCard {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = if (uiState.enLinea) "En línea" else "Trabajando sin conexión",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(checked = uiState.enLinea, onCheckedChange = { viewModel.onToggleConexion() })
                    }
                    Text(
                        text = if (uiState.enLinea) {
                            "Se envían en orden de captura; cada registro conserva su folio local para trazabilidad."
                        } else {
                            "Las entregas se guardan en el equipo. El envío se hace solo cuando aparece señal."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                    )
                    Button(
                        onClick = { viewModel.onSincronizarClick() },
                        enabled = !uiState.sincronizando,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) {
                        Text(if (uiState.sincronizando) "Enviando…" else "Sincronizar ahora")
                    }
                    uiState.ultimoMensaje?.let { mensaje ->
                        Text(text = mensaje, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    }
                    if (uiState.proveedoresPendientes > 0) {
                        Text(
                            text = "${uiState.proveedoresPendientes} proveedor(es) pendiente(s) de envío",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                        )
                    }
                }
            }

            item { SectionLabel(texto = "Outbox local") }

            items(uiState.outbox) { entrega -> EntregaRow(entrega = entrega, mostrarAvatar = false) }
        }
    }
}
