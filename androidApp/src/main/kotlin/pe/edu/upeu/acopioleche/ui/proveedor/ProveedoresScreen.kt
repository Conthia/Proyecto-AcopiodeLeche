package pe.edu.upeu.acopioleche.ui.proveedor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Sector
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedoresViewModel
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import kotlin.time.Clock

@Composable
fun ProveedoresScreen() {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        ProveedoresViewModel(
            scope = scope,
            proveedorRepository = ServiceLocator.proveedorRepository,
            entregaRepository = ServiceLocator.entregaRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    var proveedorAEditar by remember { mutableStateOf<ProveedorConEntregas?>(null) }
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var proveedorAEliminar by remember { mutableStateOf<ProveedorConEntregas?>(null) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(titulo = "Proveedores", subtitulo = "Padrón distrital") },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${uiState.proveedores.size} registrados", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    Button(
                        onClick = { mostrarDialogoNuevo = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("+ Nuevo proveedor") }
                }
            }
            mensajeNotificacion?.let { msg ->
                item {
                    Text(
                        text = msg,
                        color = VerdeOscuro,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
            items(uiState.proveedores) { proveedor ->
                ProveedorRow(
                    proveedor = proveedor,
                    alEditar = { proveedorAEditar = proveedor },
                    alEliminar = { proveedorAEliminar = proveedor },
                )
            }
        }

        if (mostrarDialogoNuevo) {
            ProveedorFormDialog(
                proveedor = null,
                alDesestimar = { mostrarDialogoNuevo = false },
                alGuardar = { nuevo ->
                    viewModel.guardarProveedor(nuevo)
                    mostrarDialogoNuevo = false
                    mensajeNotificacion = "Proveedor '${nuevo.nombre}' registrado correctamente"
                },
            )
        }

        proveedorAEditar?.let { p ->
            ProveedorFormDialog(
                proveedor = p,
                alDesestimar = { proveedorAEditar = null },
                alGuardar = { editado ->
                    viewModel.guardarProveedor(editado)
                    proveedorAEditar = null
                    mensajeNotificacion = "Proveedor '${editado.nombre}' actualizado"
                },
            )
        }

        proveedorAEliminar?.let { p ->
            AlertDialog(
                onDismissRequest = { proveedorAEliminar = null },
                title = { Text("¿Eliminar proveedor?") },
                text = { Text("Si el proveedor '${p.nombre}' tiene entregas registradas, pasará a estado Inactivo/Retirado para preservar la trazabilidad. ¿Deseas continuar?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarProveedor(
                                id = p.id,
                                alDesactivarPorDependencias = {
                                    mensajeNotificacion = "El proveedor tenía entregas asociadas. Se marcó como Retirado/Inactivo."
                                },
                                alEliminarDefinitivo = {
                                    mensajeNotificacion = "Proveedor eliminado del padrón."
                                },
                            )
                            proveedorAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta, contentColor = Color.White),
                    ) { Text("Eliminar / Desactivar") }
                },
                dismissButton = {
                    TextButton(onClick = { proveedorAEliminar = null }) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun ProveedorFormDialog(
    proveedor: ProveedorConEntregas?,
    alDesestimar: () -> Unit,
    alGuardar: (Proveedor) -> Unit,
) {
    var nombre by remember { mutableStateOf(proveedor?.nombre ?: "") }
    var documento by remember { mutableStateOf(proveedor?.documento ?: "") }
    var telefono by remember { mutableStateOf(proveedor?.telefono ?: "") }
    var sector by remember { mutableStateOf(proveedor?.sector ?: Sector.NORTE) }
    var numeroVacasTexto by remember { mutableStateOf(proveedor?.numeroVacas?.toString() ?: "5") }
    var entregaDirectaEnPlanta by remember { mutableStateOf(proveedor?.entregaDirectaEnPlanta ?: false) }
    var activo by remember { mutableStateOf(proveedor?.activo ?: true) }
    var error by remember { mutableStateOf<String?>(null) }

    val sectoresDisponibles = listOf(Sector.NORTE, Sector.SUR, Sector.ESTE, Sector.OESTE)

    AlertDialog(
        onDismissRequest = alDesestimar,
        title = { Text(if (proveedor == null) "Nuevo Proveedor" else "Editar Proveedor") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = documento,
                    onValueChange = { documento = it; error = null },
                    label = { Text("Documento (DNI/RUC)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it; error = null },
                    label = { Text("Teléfono / Celular") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(text = "Sector", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sectoresDisponibles.forEach { s ->
                        FilterChip(
                            selected = sector == s,
                            onClick = { sector = s },
                            label = { Text(s) },
                        )
                    }
                }

                OutlinedTextField(
                    value = numeroVacasTexto,
                    onValueChange = { numeroVacasTexto = it },
                    label = { Text("Número de vacas") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Entrega directa en planta")
                    Switch(checked = entregaDirectaEnPlanta, onCheckedChange = { entregaDirectaEnPlanta = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Estado activo")
                    Switch(checked = activo, onCheckedChange = { activo = it })
                }

                error?.let { err ->
                    Text(text = err, color = RojoAlerta, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "Ingresa el nombre del proveedor"
                        return@Button
                    }
                    val vacas = numeroVacasTexto.toIntOrNull() ?: 1
                    val idGenerado = proveedor?.id ?: "P-${Clock.System.now().toEpochMilliseconds().toString().takeLast(4)}"
                    val objeto = Proveedor(
                        id = idGenerado,
                        nombre = nombre.trim(),
                        documento = documento.trim(),
                        telefono = telefono.trim(),
                        sector = sector,
                        entregaDirectaEnPlanta = entregaDirectaEnPlanta,
                        numeroVacas = vacas,
                        calificacion = proveedor?.calificacion ?: CalificacionProveedor.A,
                        activo = activo,
                    )
                    alGuardar(objeto)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
            ) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = alDesestimar) { Text("Cancelar") }
        },
    )
}
