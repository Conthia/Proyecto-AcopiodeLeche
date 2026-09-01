package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue
import androidx.compose.ui.unit.dp

@Composable
fun ProveedorSeleccionadoTarjeta(proveedor: Proveedor, onQuitar: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FondoSecundario, RoundedCornerShape(13.dp))
            .border(2.dp, AzulSecundario, RoundedCornerShape(13.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(proveedor.nombre, style = MaterialTheme.typography.titleMedium, color = AzulTextoBoton)
            Text(proveedor.sector, style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 2.dp))
        }
        Icon(
            AcopioIcons.Cerrar,
            contentDescription = "Quitar proveedor",
            tint = TextoTenue,
            modifier = Modifier.size(18.dp).clickable(onClick = onQuitar),
        )
    }
}

@Composable
fun BuscadorProveedor(
    texto: String,
    mostrarResultados: Boolean,
    resultados: List<Proveedor>,
    onTextoCambia: (String) -> Unit,
    onFoco: () -> Unit,
    onSeleccionar: (Proveedor) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar proveedor por nombre o sector...",
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = texto,
            onValueChange = onTextoCambia,
            placeholder = { Text(placeholder, color = TextoTenue) },
            singleLine = true,
            leadingIcon = { Icon(AcopioIcons.Buscar, contentDescription = null, tint = TextoTenue) },
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AzulTextoBoton,
                unfocusedBorderColor = BordeClaro,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 4.dp)
                .clickable(onClick = onFoco),
        )
        if (mostrarResultados) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(13.dp))
                    .border(1.5.dp, BordeSuave, RoundedCornerShape(13.dp)),
            ) {
                if (resultados.isEmpty()) {
                    Text(
                        "Sin resultados",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoTenue,
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                    )
                } else {
                    resultados.take(5).forEach { proveedor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeleccionar(proveedor) }
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).background(FondoSecundario, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(proveedor.nombre.first().toString(), style = MaterialTheme.typography.titleSmall, color = AzulSecundario)
                            }
                            Column {
                                Text(proveedor.nombre, style = MaterialTheme.typography.bodyMedium, color = AzulTextoBoton)
                                Text(proveedor.sector, style = MaterialTheme.typography.labelSmall, color = TextoTenue)
                            }
                        }
                    }
                }
            }
        }
    }
}
