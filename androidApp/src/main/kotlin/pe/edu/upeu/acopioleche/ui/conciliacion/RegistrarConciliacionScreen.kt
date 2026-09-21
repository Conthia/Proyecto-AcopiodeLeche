package pe.edu.upeu.acopioleche.ui.conciliacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import pe.edu.upeu.acopioleche.presentation.conciliacion.RegistrarConciliacionViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun RegistrarConciliacionScreen(
    entregaId: String,
    alVolver: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember(entregaId) {
        RegistrarConciliacionViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            entregaId = entregaId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val diferencia = uiState.diferenciaLitros

    Scaffold(
        topBar = { AppTopBar(titulo = "Conciliar volumen", subtitulo = uiState.nombreProveedor, alVolver = alVolver) },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (diferencia != null) {
                val coincide = diferencia == 0.0
                AppCard {
                    SectionLabel(texto = "Resultado")
                    Text(
                        text = if (coincide) "El volumen coincide" else "Diferencia detectada: $diferencia L",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (coincide) VerdeOscuro else RojoAlerta,
                    )
                    Text(
                        text = "Campo: ${uiState.volumenCampoLitros} L · Planta: ${uiState.volumenPlanta} L",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                    )
                }
                Button(
                    onClick = alVolver,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) { Text("Volver") }
            } else {
                AppCard {
                    SectionLabel(texto = "Volumen registrado en campo")
                    Text(text = "${uiState.volumenCampoLitros} L", style = MaterialTheme.typography.headlineSmall)
                }
                AppCard {
                    SectionLabel(texto = "Volumen medido al descargar en planta")
                    OutlinedTextField(
                        value = uiState.volumenPlantaTexto,
                        onValueChange = { viewModel.onVolumenPlantaChange(it) },
                        label = { Text("Litros") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                uiState.mensajeError?.let { error ->
                    Text(text = error, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium)
                }
                Button(
                    onClick = { viewModel.onGuardarClick() },
                    enabled = !uiState.guardando,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text(if (uiState.guardando) "Guardando…" else "Guardar y comparar")
                }
            }
        }
    }
}
