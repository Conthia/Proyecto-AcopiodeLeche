package pe.edu.upeu.acopioleche.ui.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.agenda.AsistenciaUiState
import pe.edu.upeu.acopioleche.presentation.agenda.AsistenciaViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun AsistenciaScreen(reunionId: String, alVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        AsistenciaViewModel(
            scope = scope,
            asistenciaRepository = ServiceLocator.asistenciaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            reunionRepository = ServiceLocator.reunionRepository,
            reunionId = reunionId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val exito = uiState as? UiState.Exito<AsistenciaUiState>

    Scaffold(
        topBar = { AppTopBar(titulo = "Asistencia", subtitulo = exito?.datos?.temaReunion ?: "", alVolver = alVolver) },
        containerColor = FondoPantalla,
    ) { padding ->
        when (val estado = uiState) {
            UiState.Cargando -> EstadoCargando(modifier = Modifier.fillMaxSize().padding(padding))
            UiState.Vacio -> EstadoVacio(
                mensaje = "No hay convocados registrados para esta reunión.",
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.fillMaxSize().padding(padding))
            is UiState.Exito -> {
                val datos = estado.datos
                val hayConvocados = datos.convocados.isNotEmpty()
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        AppCard {
                            Text(text = datos.temaReunion, style = MaterialTheme.typography.titleMedium)
                            Text(text = datos.fechaLugar, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item {
                        AppCard {
                            Text(text = "Registro por credencial", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "Escanear el QR del proveedor marca su asistencia.",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            OutlinedButton(onClick = { viewModel.onEscanearQr() }, enabled = hayConvocados) { Text("Escanear QR") }
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SectionLabel(texto = "Lista de convocados")
                            Text(
                                text = "${datos.numeroPresentes} / ${datos.convocados.size}",
                                color = VerdeOscuro,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    if (hayConvocados) {
                        items(datos.convocados) { convocado ->
                            ConvocadoRow(convocado = convocado, alTocar = { viewModel.onToggleConvocado(convocado.actorId) })
                        }
                    } else {
                        item {
                            Text(
                                text = "Aún no hay convocados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario,
                            )
                        }
                    }
                    item {
                        Button(
                            onClick = { alVolver() },
                            enabled = hayConvocados,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                        ) { Text("Cerrar acta de asistencia") }
                    }
                }
            }
        }
    }
}
