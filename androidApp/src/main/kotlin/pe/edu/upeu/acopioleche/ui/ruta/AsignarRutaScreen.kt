package pe.edu.upeu.acopioleche.ui.ruta

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.presentation.ruta.AsignarRutaViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun AsignarRutaScreen(alVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        AsignarRutaViewModel(
            scope = scope,
            rutaRepository = ServiceLocator.rutaRepository,
            usuarioRepository = ServiceLocator.usuarioRepository,
            centroAcopioRepository = ServiceLocator.centroAcopioRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    var rutaAEliminar by remember { mutableStateOf<RutaAcopio?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Asignar Ruta de Acopio",
                subtitulo = "Programación diaria para acopiadores",
                alVolver = alVolver,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (uiState.enModoEdicion) {
                item {
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Editando ruta existente",
                                style = MaterialTheme.typography.titleMedium,
                                color = VerdeOscuro,
                            )
                            TextButton(onClick = { viewModel.onCancelarEdicion() }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                                Text("Cancelar edición")
                            }
                        }
                    }
                }
            }

            item {
                AppCard {
                    SectionLabel(texto = "1. Seleccionar Acopiador")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.acopiadores.forEach { acopiador ->
                            FilterChip(
                                selected = uiState.acopiadorSeleccionadoId == acopiador.id,
                                onClick = { viewModel.onAcopiadorChange(acopiador.id) },
                                label = { Text(acopiador.nombreCompleto) },
                            )
                        }
                    }

                    SectionLabel(texto = "2. Seleccionar Centro de Sector (Destino)")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.centrosSector.forEach { centro ->
                            FilterChip(
                                selected = uiState.centroSectorSeleccionadoId == centro.id,
                                onClick = { viewModel.onCentroSectorChange(centro.id) },
                                label = { Text(centro.nombre) },
                            )
                        }
                    }

                    SectionLabel(texto = "3. Nombre de la Ruta (Opcional)")
                    OutlinedTextField(
                        value = uiState.nombreRuta,
                        onValueChange = { viewModel.onNombreRutaChange(it) },
                        label = { Text("Ej: Ruta Sector Coyme / Pallalla") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                AppCard {
                    SectionLabel(texto = "4. Agregar Proveedores a la Ruta")
                    Text(
                        text = "Selecciona productores para incluir en la orden de parada:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        uiState.proveedoresDisponibles.forEach { prov ->
                            val estaAgregado = uiState.paradasOrdenadasProveedorIds.contains(prov.id)
                            FilterChip(
                                selected = estaAgregado,
                                onClick = {
                                    if (estaAgregado) viewModel.onQuitarParada(prov.id) else viewModel.onAgregarParada(prov.id)
                                },
                                label = { Text("${if (estaAgregado) "✓ " else "+ "}${prov.nombre}") },
                            )
                        }
                    }
                }
            }

            if (uiState.paradasOrdenadasProveedorIds.isNotEmpty()) {
                item {
                    SectionLabel(texto = "Paradas de la Ruta (en orden de recorrido)")
                }

                itemsIndexed(uiState.paradasOrdenadasProveedorIds) { index, provId ->
                    val prov = uiState.proveedoresDisponibles.find { it.id == provId }
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${index + 1}. ${prov?.nombre ?: provId} (${prov?.sector ?: "Sector"})",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Row {
                                if (index > 0) {
                                    IconButton(onClick = { viewModel.onMoverParada(index, index - 1) }) {
                                        Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = "Subir")
                                    }
                                }
                                if (index < uiState.paradasOrdenadasProveedorIds.size - 1) {
                                    IconButton(onClick = { viewModel.onMoverParada(index, index + 1) }) {
                                        Icon(imageVector = Icons.Filled.ArrowDownward, contentDescription = "Bajar")
                                    }
                                }
                                IconButton(onClick = { viewModel.onQuitarParada(provId) }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Quitar", tint = RojoAlerta)
                                }
                            }
                        }
                    }
                }
            }

            uiState.mensajeError?.let { err ->
                item { Text(text = err, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium) }
            }

            uiState.mensajeNotificacion?.let { msg ->
                item { Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium) }
            }

            item {
                Button(
                    onClick = { viewModel.onAsignarRutaClick() },
                    enabled = !uiState.guardando,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text(
                        when {
                            uiState.guardando -> "Guardando…"
                            uiState.enModoEdicion -> "Guardar Cambios"
                            else -> "Guardar y Asignar Ruta"
                        },
                    )
                }
            }

            item {
                SectionLabel(texto = "Rutas Asignadas Registradas (${uiState.rutasExistentes.size})")
            }

            items(uiState.rutasExistentes) { ruta ->
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(text = ruta.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Acopiador: ${uiState.acopiadores.find { it.id == ruta.acopiadorId }?.nombreCompleto ?: ruta.acopiadorId} · ${ruta.fecha}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario,
                            )
                            Text(
                                text = "${ruta.paradas.size} paradas ordenadas · Centro: ${ruta.centroSectorId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario,
                            )
                        }
                        if (ruta.estado == EstadoRuta.EN_CURSO) {
                            EstadoBadge(texto = "En Curso", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                        } else {
                            EstadoBadge(texto = "Finalizada", color = AmbarTexto, colorFondo = AmbarFondo)
                        }
                    }
                    if (ruta.volumenDescargadoLitros != null) {
                        HorizontalDivider()
                        Text(
                            text = "Descarga consolidada: ${ruta.volumenDescargadoLitros} L · Cierre: ${ruta.fechaHoraCierre}",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeOscuro,
                        )
                    }
                    HorizontalDivider()
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.onEditarRuta(ruta) }) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                            Text("Editar")
                        }
                        OutlinedButton(
                            onClick = { rutaAEliminar = ruta },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoAlerta),
                        ) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                            Text("Eliminar")
                        }
                    }
                }
            }
        }

        rutaAEliminar?.let { ruta ->
            AlertDialog(
                onDismissRequest = { rutaAEliminar = null },
                title = { Text("¿Eliminar ruta?") },
                text = {
                    Text(
                        "Se eliminará '${ruta.nombre}' y sus paradas. Si el acopiador ya registró " +
                            "avance en ella, no se podrá eliminar y deberás editarla en su lugar.",
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onEliminarRuta(ruta)
                            rutaAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta, contentColor = Color.White),
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { rutaAEliminar = null }) { Text("Cancelar") }
                },
            )
        }
    }
}
