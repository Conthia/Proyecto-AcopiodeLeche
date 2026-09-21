package pe.edu.upeu.acopioleche.ui.entrega

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.presentation.entrega.SeleccionarProveedorViewModel
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.AvatarIniciales
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun SeleccionarProveedorScreen(
    alVolver: () -> Unit,
    alSeleccionar: (proveedorId: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "SeleccionarProveedorScreen requiere una sesión activa" }
    val viewModel = remember(sesionActiva.usuarioId) {
        SeleccionarProveedorViewModel(
            scope = scope,
            proveedorRepository = ServiceLocator.proveedorRepository,
            rutaRepository = ServiceLocator.rutaRepository,
            acopiadorId = sesionActiva.usuarioId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(titulo = "Seleccionar proveedor", subtitulo = "Paso 1 de 2", alVolver = alVolver) },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    value = uiState.textoBusqueda,
                    onValueChange = { viewModel.onTextoBusquedaChange(it) },
                    placeholder = { Text("Buscar por código o nombre…") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            item { SectionLabel(texto = "Proveedores de la ruta · ${uiState.proveedores.size}") }
            items(uiState.proveedoresFiltrados) { proveedor ->
                Card(
                    onClick = { alSeleccionar(proveedor.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                    border = BorderStroke(width = 1.dp, color = BordeTarjeta),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AvatarIniciales(nombre = proveedor.nombre)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = proveedor.nombre, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "${proveedor.id} · ${proveedor.sector}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario,
                            )
                        }
                    }
                }
            }
        }
    }
}
