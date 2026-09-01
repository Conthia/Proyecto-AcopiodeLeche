package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AzulMarinoOscuro
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton

@Composable
fun DashboardHeader(
    rol: RolUsuario,
    nombreUsuario: String,
    estadoSincronizacion: EstadoSincronizacion,
    horaActual: String,
    modifier: Modifier = Modifier,
    contenidoExtra: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(AzulMarinoOscuro, AzulSecundario)))
            .padding(top = 16.dp, start = 18.dp, end = 18.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).background(rol.color, RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(rol.inicial, style = MaterialTheme.typography.titleMedium, color = AzulTextoBoton)
                }
                Column {
                    Text(nombreUsuario, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(
                        rol.etiqueta.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = rol.color,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                SyncBadge(estadoSincronizacion)
                Text(
                    horaActual,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                )
            }
        }
        contenidoExtra?.invoke()
    }
}
