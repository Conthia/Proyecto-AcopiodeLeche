package pe.edu.upeu.acopioleche.ui.reportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.presentation.reportes.VolumenComunidad
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun VolumenComunidadRow(volumen: VolumenComunidad, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = volumen.nombreCentro, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${volumen.litros.toInt()} L", style = MaterialTheme.typography.bodyMedium)
        }
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().height(7.dp).background(color = BordeTarjeta, shape = RoundedCornerShape(4.dp)),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(volumen.proporcion.coerceIn(0f, 1f))
                    .height(7.dp)
                    .background(color = VerdeOscuro, shape = RoundedCornerShape(4.dp)),
            )
        }
    }
}
