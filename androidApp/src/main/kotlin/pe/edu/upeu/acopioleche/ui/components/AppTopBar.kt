package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

val LocalAbrirMenuAdmin = staticCompositionLocalOf<(() -> Unit)?> { null }

@Composable
fun AppTopBar(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    alVolver: (() -> Unit)? = null,
    alAbrirPerfil: (() -> Unit)? = null,
    alAbrirMenu: (() -> Unit)? = null,
    accesorio: @Composable (RowScope.() -> Unit)? = null,
) {
    val fnMenu = alAbrirMenu ?: LocalAbrirMenuAdmin.current
    Row(
        modifier = modifier
            .background(VerdeOscuro)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (fnMenu != null && alVolver == null) {
            IconButton(onClick = fnMenu) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú principal",
                    tint = Color.White,
                )
            }
        }
        if (alVolver != null) {
            IconButton(onClick = alVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(text = subtitulo, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        }
        accesorio?.invoke(this)
        if (alAbrirPerfil != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = alAbrirPerfil) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Perfil",
                    tint = Color.White,
                )
            }
        }
    }
}
