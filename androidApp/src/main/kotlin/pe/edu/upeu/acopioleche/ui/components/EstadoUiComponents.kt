package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

/**
 * Cuerpo de pantalla mientras el `Flow` reactivo del ViewModel todavía no emitió su primer valor.
 */
@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VerdeOscuro)
    }
}

/**
 * Cuerpo de pantalla cuando la fuente de datos respondió correctamente pero no hay elementos.
 */
@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Cuerpo de pantalla cuando la carga de datos falló (excepción en el `Flow`). [mensaje] es texto
 * fijo orientado al usuario, no el detalle técnico de la excepción (ver [AppLogger] para eso).
 */
@Composable
fun EstadoError(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = RojoAlerta,
            textAlign = TextAlign.Center,
        )
    }
}
