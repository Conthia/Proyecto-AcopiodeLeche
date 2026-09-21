package pe.edu.upeu.acopioleche.ui.dashboard.lacteos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.presentation.dashboard.lacteos.LacteosHomeUiState
import pe.edu.upeu.acopioleche.presentation.dashboard.lacteos.LacteosHomeViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo
import kotlin.time.Clock

@Composable
fun LacteosHomeScreen(
    alAbrirPerfil: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "LacteosHomeScreen requiere una sesión activa" }

    val viewModel = remember(sesionActiva.usuarioId) {
        LacteosHomeViewModel(
            scope = scope,
            produccionDerivadoRepository = ServiceLocator.produccionDerivadoRepository,
            insumoRepository = ServiceLocator.insumoRepository,
            nombreResponsable = sesionActiva.nombreCompleto,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val exito = uiState as? UiState.Exito<LacteosHomeUiState>
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    var mostrarDialogoNuevoDerivado by remember { mutableStateOf(false) }
    var derivadoAEditar by remember { mutableStateOf<ProduccionDerivado?>(null) }
    var mostrarDialogoNuevoInsumo by remember { mutableStateOf(false) }
    var insumoAEditar by remember { mutableStateOf<InsumoLacteo?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Procesamiento de Lácteos",
                subtitulo = "Planta · ${sesionActiva.nombreCompleto}",
                alAbrirPerfil = alAbrirPerfil,
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
            item {
                AppCard {
                    SectionLabel(texto = "Resumen de Planta y Derivados")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatTile(valor = "${exito?.datos?.producciones?.size ?: 0}", etiqueta = "lotes producidos", modifier = Modifier.weight(1f))
                        StatTile(valor = "${exito?.datos?.insumos?.size ?: 0}", etiqueta = "insumos registrados", modifier = Modifier.weight(1f))
                    }
                }
            }

            mensajeNotificacion?.let { msg ->
                item { Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium) }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel(
                        texto = "Producción de Derivados Lácteos (RF-30)",
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { mostrarDialogoNuevoDerivado = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("+ Registrar Lote") }
                }
            }

            when (val estado = uiState) {
                UiState.Cargando -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VerdeOscuro)
                    }
                }
                UiState.Vacio -> item {
                    Text(
                        text = "No hay lotes de producción ni insumos registrados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                    )
                }
                is UiState.Error -> item {
                    Text(text = estado.mensaje, style = MaterialTheme.typography.bodyMedium, color = RojoAlerta)
                }
                is UiState.Exito -> {
                    items(estado.datos.producciones) { p ->
                        AppCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(text = p.tipoProducto, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "Lote: ${p.codigoLote} · ${p.fechaProduccion}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario,
                                    )
                                }
                                EstadoBadge(texto = "${p.cantidadUnidades.toInt()} unidades", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                            }
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                IconButton(onClick = { derivadoAEditar = p }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar", tint = VerdeOscuro)
                                }
                                IconButton(onClick = { viewModel.eliminarProduccion(p.id); mensajeNotificacion = "Lote de producción eliminado." }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = RojoAlerta)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel(
                        texto = "Registro de Insumos Lácteos (RF-31)",
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { mostrarDialogoNuevoInsumo = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("+ Registrar Insumo") }
                }
            }

            exito?.datos?.insumos?.let { insumos ->
                items(insumos) { insumo ->
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(text = insumo.nombreInsumo, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "Ingreso: ${insumo.fechaIngreso}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario,
                                )
                            }
                            EstadoBadge(texto = "${insumo.cantidad} ${insumo.unidadMedida}", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            IconButton(onClick = { insumoAEditar = insumo }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar", tint = VerdeOscuro)
                            }
                            IconButton(onClick = { viewModel.eliminarInsumo(insumo.id); mensajeNotificacion = "Registro de insumo eliminado." }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = RojoAlerta)
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoNuevoDerivado || derivadoAEditar != null) {
            val p = derivadoAEditar
            var tipo by remember { mutableStateOf(p?.tipoProducto ?: "Queso Paria Fresco") }
            var lote by remember { mutableStateOf(p?.codigoLote ?: "LOTE-2026-00${(3..99).random()}") }
            var cantidadTexto by remember { mutableStateOf(p?.cantidadUnidades?.toInt()?.toString() ?: "50") }
            var fTexto by remember { mutableStateOf(p?.fechaProduccion?.toString() ?: "2026-09-15") }
            var err by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { mostrarDialogoNuevoDerivado = false; derivadoAEditar = null },
                title = { Text(if (p == null) "Registrar Lote de Producción (RF-30)" else "Editar Lote") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = tipo,
                            onValueChange = { tipo = it },
                            label = { Text("Tipo de Producto (Queso / Yogurt / Mantequilla)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = lote,
                            onValueChange = { lote = it },
                            label = { Text("Código de Lote") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = cantidadTexto,
                            onValueChange = { cantidadTexto = it },
                            label = { Text("Cantidad de unidades producidas") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = fTexto,
                            onValueChange = { fTexto = it },
                            label = { Text("Fecha Producción (AAAA-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        err?.let { Text(text = it, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cant = cantidadTexto.toDoubleOrNull()
                            val fProd = try { LocalDate.parse(fTexto.trim()) } catch (_: Exception) { null }

                            if (cant == null || cant <= 0.0 || fProd == null || tipo.isBlank()) {
                                err = "Ingresa datos válidos"
                                return@Button
                            }

                            val idObj = p?.id ?: "PROD-DER-${Clock.System.now().toEpochMilliseconds()}"
                            val objeto = ProduccionDerivado(
                                id = idObj,
                                tipoProducto = tipo.trim(),
                                codigoLote = lote.trim(),
                                cantidadUnidades = cant,
                                fechaProduccion = fProd,
                                responsableId = sesionActiva.usuarioId,
                            )
                            viewModel.registrarProduccion(objeto)
                            mensajeNotificacion = "Lote '${objeto.codigoLote}' (${objeto.tipoProducto}) registrado correctamente."
                            mostrarDialogoNuevoDerivado = false
                            derivadoAEditar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Guardar Lote") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoNuevoDerivado = false; derivadoAEditar = null }) { Text("Cancelar") }
                },
            )
        }

        if (mostrarDialogoNuevoInsumo || insumoAEditar != null) {
            val ins = insumoAEditar
            var nombre by remember { mutableStateOf(ins?.nombreInsumo ?: "Cuajo Líquido Grado A") }
            var cantidadTexto by remember { mutableStateOf(ins?.cantidad?.toString() ?: "10.0") }
            var unidad by remember { mutableStateOf(ins?.unidadMedida ?: "Litros") }
            var fTexto by remember { mutableStateOf(ins?.fechaIngreso?.toString() ?: "2026-09-15") }
            var err by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { mostrarDialogoNuevoInsumo = false; insumoAEditar = null },
                title = { Text(if (ins == null) "Registrar Insumo (RF-31)" else "Editar Insumo") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del insumo (ej: Cuajo, Sal, Cultivo)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = cantidadTexto,
                            onValueChange = { cantidadTexto = it },
                            label = { Text("Cantidad") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = unidad,
                            onValueChange = { unidad = it },
                            label = { Text("Unidad de Medida (Litros / Kg / Sobres)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = fTexto,
                            onValueChange = { fTexto = it },
                            label = { Text("Fecha Ingreso (AAAA-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        err?.let { Text(text = it, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cant = cantidadTexto.toDoubleOrNull()
                            val fIng = try { LocalDate.parse(fTexto.trim()) } catch (_: Exception) { null }

                            if (cant == null || cant <= 0.0 || fIng == null || nombre.isBlank()) {
                                err = "Ingresa datos válidos"
                                return@Button
                            }

                            val idObj = ins?.id ?: "INS-${Clock.System.now().toEpochMilliseconds()}"
                            val objeto = InsumoLacteo(
                                id = idObj,
                                nombreInsumo = nombre.trim(),
                                cantidad = cant,
                                unidadMedida = unidad.trim(),
                                fechaIngreso = fIng,
                            )
                            viewModel.registrarInsumo(objeto)
                            mensajeNotificacion = "Insumo '${objeto.nombreInsumo}' (${objeto.cantidad} ${objeto.unidadMedida}) registrado."
                            mostrarDialogoNuevoInsumo = false
                            insumoAEditar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Guardar Insumo") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoNuevoInsumo = false; insumoAEditar = null }) { Text("Cancelar") }
                },
            )
        }
    }
}
