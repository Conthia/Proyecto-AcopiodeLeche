package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue

@Composable
fun FormInput(
    etiqueta: String,
    placeholder: String,
    valor: String,
    onValorCambia: (String) -> Unit,
    modifier: Modifier = Modifier,
    esPassword: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(etiqueta)
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambia,
            placeholder = { Text(placeholder, color = TextoTenue) },
            singleLine = true,
            visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = tipoTeclado),
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AzulTextoBoton,
                unfocusedBorderColor = BordeClaro,
                focusedTextColor = AzulTextoBoton,
                unfocusedTextColor = AzulTextoBoton,
            ),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        )
    }
}
