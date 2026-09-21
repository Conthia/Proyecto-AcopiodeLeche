package pe.edu.upeu.acopioleche.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun ConfirmacionEntregaContent(
    entrega: EntregaResumen,
    alRegistrarOtra: () -> Unit,
    alVolverAlInicio: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppCard {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color = VerdeSuaveFondo, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✓", color = VerdeOscuro, style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = "Entrega registrada",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Guardada en el dispositivo con folio local. Se enviará al servidor cuando haya señal.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            listOf(
                "Folio local" to entrega.id,
                "Proveedor" to entrega.codigoProveedor,
                "Volumen" to "${entrega.volumenLitros} L",
                "Porongos" to "${entrega.cantidadPorongos}",
            ).forEach { (etiqueta, valor) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = etiqueta, color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                    Text(text = valor, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Button(
            onClick = alRegistrarOtra,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
        ) { Text("Registrar otra entrega") }

        OutlinedButton(onClick = alVolverAlInicio, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text("Volver al inicio")
        }
    }
}
