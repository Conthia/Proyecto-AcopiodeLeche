package pe.edu.upeu.acopioleche.ui.agenda

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.presentation.agenda.ConvocadoUiModel
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun ConvocadoRow(convocado: ConvocadoUiModel, alTocar: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = alTocar,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (convocado.presente) VerdeSuaveFondo else FondoTarjeta),
        border = BorderStroke(width = 1.dp, color = if (convocado.presente) VerdeOscuro else BordeTarjeta),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(
                        color = if (convocado.presente) VerdeOscuro else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (convocado.presente) {
                    Text(text = "✓", color = Color.White)
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(text = convocado.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(text = convocado.meta, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            }
        }
    }
}
