package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro

@Composable
fun SuccessView(
    titulo: String,
    subtitulo: String,
    onNuevo: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    etiquetaNuevo: String = "Nuevo registro",
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier.size(76.dp).background(VerdeExitoFondo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AcopioIcons.Check,
                contentDescription = null,
                tint = VerdeExitoOscuro,
                modifier = Modifier.size(38.dp),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = AzulTextoBoton,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BotonGrande(
                etiqueta = etiquetaNuevo,
                onClick = onNuevo,
                variante = VarianteBoton.SECUNDARIO,
                modifier = Modifier.weight(1f),
            )
            BotonGrande(
                etiqueta = "Volver",
                onClick = onVolver,
                variante = VarianteBoton.PRIMARIO,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
