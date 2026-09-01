package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton

@Composable
fun NavHeader(
    titulo: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    contenidoDerecha: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AzulTextoBoton)
            .height(56.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = AcopioIcons.Volver,
                    contentDescription = "Volver",
                    tint = Color.White,
                )
            }
        }
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )
        contenidoDerecha?.invoke()
    }
}
