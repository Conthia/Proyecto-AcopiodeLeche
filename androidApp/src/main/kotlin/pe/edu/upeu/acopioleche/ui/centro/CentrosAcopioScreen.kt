package pe.edu.upeu.acopioleche.ui.centro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.presentation.centro.CentroConEstadisticas
import pe.edu.upeu.acopioleche.presentation.centro.CentrosAcopioUiState
import pe.edu.upeu.acopioleche.presentation.centro.CentrosAcopioViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun CentrosAcopioScreen() {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        CentrosAcopioViewModel(
            scope = scope,
            centroAcopioRepository = ServiceLocator.centroAcopioRepository,
            entregaRepository = ServiceLocator.entregaRepository,
            equipoCampoRepository = ServiceLocator.equipoCampoRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val exito = uiState as? UiState.Exito<CentrosAcopioUiState>

    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var centroAEditar by remember { mutableStateOf<CentroConEstadisticas?>(null) }
    var centroAEliminar by remember { mutableStateOf<CentroConEstadisticas?>(null) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(titulo = "Centros de acopio", subtitulo = exito?.datos?.numeroActivos?.let { "$it activos" } ?: "") },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = exito?.datos?.centros?.size?.let { "$it centros registrados" } ?: "", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                Button(
                    onClick = { mostrarDialogoNuevo = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) { Text("+ Nuevo centro") }
            }
            mensajeNotificacion?.let { msg ->
                Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            when (val estado = uiState) {
                UiState.Cargando -> EstadoCargando(modifier = Modifier.weight(1f))
                UiState.Vacio -> EstadoVacio(
                    mensaje = "No hay centros de acopio registrados. Usa '+ Nuevo centro' para agregar el primero.",
                    modifier = Modifier.weight(1f),
                )
                is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.weight(1f))
                is UiState.Exito -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(estado.datos.centros) { centro ->
                        CentroCard(
                            centro = centro,
                            alToggleActivo = { viewModel.toggleActivo(centro.id) },
                            alEditar = { centroAEditar = centro },
                            alEliminar = { centroAEliminar = centro },
                        )
                    }
                }
            }
        }

        if (mostrarDialogoNuevo) {
            CentroFormDialog(
                centro = null,
                alDesestimar = { mostrarDialogoNuevo = false },
                alGuardar = { nuevo ->
                    viewModel.guardarCentro(nuevo)
                    mostrarDialogoNuevo = false
                    mensajeNotificacion = "Centro '${nuevo.nombre}' registrado correctamente"
                },
            )
        }

        centroAEditar?.let { c ->
            CentroFormDialog(
                centro = c,
                alDesestimar = { centroAEditar = null },
                alGuardar = { editado ->
                    viewModel.guardarCentro(editado)
                    centroAEditar = null
                    mensajeNotificacion = "Centro '${editado.nombre}' actualizado"
                },
            )
        }

        centroAEliminar?.let { c ->
            AlertDialog(
                onDismissRequest = { centroAEliminar = null },
                title = { Text("¿Eliminar centro de acopio?") },
                text = { Text("Si el centro '${c.nombre}' tiene entregas o usuarios asignados, se desactivará en lugar de eliminarse para no perder datos históricos. ¿Continuar?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarCentro(
                                id = c.id,
                                alDesactivarPorDependencias = {
                                    mensajeNotificacion = "El centro tenía entregas/usuarios asociados. Se marcó como Inactivo."
                                },
                                alEliminarDefinitivo = {
                                    mensajeNotificacion = "Centro de acopio eliminado."
                                },
                            )
                            centroAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta, contentColor = Color.White),
                    ) { Text("Eliminar / Desactivar") }
                },
                dismissButton = {
                    TextButton(onClick = { centroAEliminar = null }) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun CentroFormDialog(
    centro: CentroConEstadisticas?,
    alDesestimar: () -> Unit,
    alGuardar: (CentroAcopio) -> Unit,
) {
    var nombre by remember { mutableStateOf(centro?.nombre ?: "") }
    var ubicacion by remember { mutableStateOf(centro?.ubicacion ?: "") }
    var capacidadDiaTexto by remember { mutableStateOf(centro?.capacidadLitrosDia?.toInt()?.toString() ?: "300") }
    var capacidadTanqueTexto by remember { mutableStateOf(centro?.capacidadTanqueLitros?.toInt()?.toString() ?: "500") }
    var activo by remember { mutableStateOf(centro?.activo ?: true) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = alDesestimar,
        title = { Text(if (centro == null) "Nuevo Centro de Acopio" else "Editar Centro de Acopio") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre del centro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it; error = null },
                    label = { Text("Ubicación / Referencia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = capacidadDiaTexto,
                    onValueChange = { capacidadDiaTexto = it },
                    label = { Text("Capacidad proyectada (L/día)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = capacidadTanqueTexto,
                    onValueChange = { capacidadTanqueTexto = it },
                    label = { Text("Capacidad de tanque (L)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Centro activo")
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
                        error = "Ingresa el nombre del centro"
                        return@Button
                    }
                    val capDia = capacidadDiaTexto.toDoubleOrNull() ?: 300.0
                    val capTanque = capacidadTanqueTexto.toDoubleOrNull() ?: 500.0
                    val idGenerado = centro?.id ?: "CA-00${(4..99).random()}"
                    val objeto = CentroAcopio(
                        id = idGenerado,
                        nombre = nombre.trim(),
                        ubicacion = ubicacion.trim(),
                        capacidadLitrosDia = capDia,
                        activo = activo,
                        capacidadTanqueLitros = capTanque,
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
