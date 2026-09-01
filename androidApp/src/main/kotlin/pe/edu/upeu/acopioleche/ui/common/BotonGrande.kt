package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.Dorado
import pe.edu.upeu.acopioleche.ui.theme.DoradoDeshabilitado
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue

@Composable
fun BotonGrande(
    etiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variante: VarianteBoton = VarianteBoton.PRIMARIO,
    habilitado: Boolean = true,
    icono: (@Composable () -> Unit)? = null,
) {
    val (fondo, contenido, borde) = when (variante) {
        VarianteBoton.PRIMARIO -> Triple(if (habilitado) AzulTextoBoton else TextoTenue, Color.White, null)
        VarianteBoton.SECUNDARIO -> Triple(FondoSecundario, AzulTextoBoton, BordeClaro)
        VarianteBoton.DORADO -> Triple(if (habilitado) Dorado else DoradoDeshabilitado, AzulTextoBoton, null)
    }
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = contenido,
            disabledContainerColor = fondo,
            disabledContentColor = contenido,
        ),
        border = borde?.let { androidx.compose.foundation.BorderStroke(1.5.dp, it) },
        contentPadding = PaddingValues(horizontal = 18.dp),
        modifier = modifier.fillMaxWidth().height(56.dp),
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, androidx.compose.ui.Alignment.CenterHorizontally),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            icono?.invoke()
            Text(text = etiqueta.uppercase(), style = MaterialTheme.typography.labelLarge, color = contenido)
        }
    }
}
