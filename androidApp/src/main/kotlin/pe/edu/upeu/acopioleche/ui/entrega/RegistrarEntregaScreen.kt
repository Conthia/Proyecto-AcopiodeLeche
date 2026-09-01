package pe.edu.upeu.acopioleche.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.BuscadorProveedor
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion
import pe.edu.upeu.acopioleche.ui.common.FormLabel
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.ProveedorSeleccionadoTarjeta
import pe.edu.upeu.acopioleche.ui.common.SyncBadge
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.Dorado
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue
import pe.edu.upeu.acopioleche.ui.theme.VerdeExito
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegistrarEntregaScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrarEntregaViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        val formato = SimpleDateFormat("HH:mm", Locale.forLanguageTag("es-PE"))
        viewModel.actualizarHora(formato.format(Date()))
    }

    Column(modifier = modifier.fillMaxSize()) {
        NavHeader(
            titulo = "Registrar Entrega",
            onVolver = onVolver,
            contenidoDerecha = { SyncBadge(EstadoSincronizacion.PENDIENTE) },
        )

        Row(
            modifier = Modifier.fillMaxWidth().background(AzulSecundario).padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("31 de agosto", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f))
            Text(estado.horaActual, style = MaterialTheme.typography.titleMedium, color = Dorado)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta)
                .padding(horizontal = 18.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(AcopioIcons.Gota, contentDescription = null, tint = AzulSecundario, modifier = Modifier.size(15.dp))
            Text(DatosDemo.centroAcopioActual.nombre, style = MaterialTheme.typography.labelSmall, color = AzulSecundario)
        }

        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (estado.guardado) {
                VistaEntregaGuardada(estado, onRegistrarOtra = viewModel::registrarOtra)
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Turno")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("Mañana", "Tarde").forEach { turno ->
                                SelectorTurno(
                                    texto = turno,
                                    seleccionado = estado.turno == turno,
                                    onClick = { viewModel.seleccionarTurno(turno) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Proveedor *")
                        val proveedor = estado.proveedorSeleccionado
                        if (proveedor != null) {
                            ProveedorSeleccionadoTarjeta(proveedor, onQuitar = viewModel::quitarProveedor)
                        } else {
                            BuscadorProveedor(
                                texto = estado.textoBusqueda,
                                mostrarResultados = estado.mostrarBusqueda,
                                resultados = viewModel.proveedoresFiltrados,
                                onTextoCambia = viewModel::cambiarBusqueda,
                                onFoco = { viewModel.mostrarBusqueda(true) },
                                onSeleccionar = viewModel::seleccionarProveedor,
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        FormLabel("Volumen en litros *")
                        Box {
                            OutlinedTextField(
                                value = estado.litros,
                                onValueChange = viewModel::cambiarLitros,
                                placeholder = { Text("0.0", style = MaterialTheme.typography.headlineLarge, color = TextoTenue) },
                                textStyle = MaterialTheme.typography.headlineLarge,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = AzulTextoBoton,
                                    unfocusedBorderColor = BordeClaro,
                                    focusedTextColor = AzulTextoBoton,
                                    unfocusedTextColor = AzulTextoBoton,
                                ),
                                modifier = Modifier.fillMaxWidth().height(76.dp),
                            )
                            Text(
                                "L",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoTenue,
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp),
                            )
                        }
                    }

                    BotonGrande(
                        etiqueta = "Registrar entrega",
                        onClick = viewModel::guardar,
                        variante = VarianteBoton.DORADO,
                        habilitado = estado.puedeGuardar,
                        icono = { Icon(AcopioIcons.Gota, contentDescription = null, tint = AzulTextoBoton, modifier = Modifier.size(19.dp)) },
                    )

                    if (estado.entregasRecientes.isNotEmpty()) {
                        Column(modifier = Modifier.padding(top = 22.dp)) {
                            FormLabel("Registros de hoy")
                            estado.entregasRecientes.take(4).forEach { entregaUi ->
                                FilaEntregaReciente(entregaUi)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorTurno(texto: String, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(50.dp)
            .background(if (seleccionado) AzulTextoBoton else Color.White, RoundedCornerShape(13.dp))
            .border(1.5.dp, if (seleccionado) AzulTextoBoton else BordeClaro, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.titleMedium,
            color = if (seleccionado) Color.White else TextoTenue,
        )
    }
}

@Composable
private fun FilaEntregaReciente(entregaUi: EntregaUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (entregaUi.estadoSincronizacion == EstadoSincronizacion.SINCRONIZADO) VerdeExito else Dorado,
                    CircleShape,
                ),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(entregaUi.proveedorNombre, style = MaterialTheme.typography.bodyMedium, color = AzulTextoBoton)
            Text("Turno ${entregaUi.entrega.turno}", style = MaterialTheme.typography.labelSmall, color = TextoTenue)
        }
        Text("${entregaUi.entrega.volumenLitros} L", style = MaterialTheme.typography.titleMedium, color = AzulSecundario)
    }
}

@Composable
private fun VistaEntregaGuardada(estado: RegistrarEntregaUiState, onRegistrarOtra: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier.size(76.dp).background(VerdeExitoFondo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(AcopioIcons.Check, contentDescription = null, tint = VerdeExitoOscuro, modifier = Modifier.size(38.dp))
        }
        Text("Entrega registrada", style = MaterialTheme.typography.headlineMedium, color = AzulTextoBoton)
        Text(estado.proveedorSeleccionado?.nombre.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = TextoTenue)
        Text("${estado.litros} L", style = MaterialTheme.typography.headlineLarge, color = AzulTextoBoton)
        Text("Turno ${estado.turno} · ${estado.horaActual}", style = MaterialTheme.typography.bodySmall, color = TextoTenue)
        SyncBadge(EstadoSincronizacion.PENDIENTE)
        BotonGrande(
            etiqueta = "Registrar otra entrega",
            onClick = onRegistrarOtra,
            variante = VarianteBoton.DORADO,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
