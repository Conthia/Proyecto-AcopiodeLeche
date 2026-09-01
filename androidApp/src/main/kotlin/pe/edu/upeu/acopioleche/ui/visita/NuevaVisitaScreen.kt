package pe.edu.upeu.acopioleche.ui.visita

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.BuscadorProveedor
import pe.edu.upeu.acopioleche.ui.common.FormLabel
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.ProveedorSeleccionadoTarjeta
import pe.edu.upeu.acopioleche.ui.common.SuccessView
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlerta
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.RojoError
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorFondo
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorTexto
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeExito
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoTexto

private val TIPOS_VISITA = listOf("Calidad", "Sanidad", "Asesoría")
private val RESULTADOS_VISITA = listOf("Óptimo", "Observación", "Crítico")

@Composable
fun NuevaVisitaScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevaVisitaViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        NavHeader(titulo = "Nueva Visita Técnica", onVolver = onVolver)

        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (estado.guardado) {
                SuccessView(
                    titulo = "¡Visita registrada!",
                    subtitulo = "Visita técnica a ${estado.proveedorSeleccionado?.nombre} guardada con resultado: ${estado.resultado}.",
                    etiquetaNuevo = "Nueva visita",
                    onNuevo = viewModel::nuevaVisita,
                    onVolver = onVolver,
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
                    Text(
                        "Registre la visita de campo al productor, incluyendo el tipo de control y el resultado observado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Proveedor visitado *")
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
                                placeholder = "Buscar proveedor...",
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Tipo de visita *")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TIPOS_VISITA.forEach { tipo ->
                                val seleccionado = estado.tipoVisita == tipo
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .background(if (seleccionado) VerdeExitoFondo else Color.White, RoundedCornerShape(12.dp))
                                        .border(1.5.dp, if (seleccionado) VerdeExito else BordeClaro, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.seleccionarTipoVisita(tipo) },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        tipo,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (seleccionado) VerdeExitoTexto else TextoSecundario,
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Resultado observado *")
                        RESULTADOS_VISITA.forEach { resultado ->
                            OpcionResultado(
                                texto = resultado,
                                seleccionado = estado.resultado == resultado,
                                onClick = { viewModel.seleccionarResultado(resultado) },
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        FormLabel("Observaciones")
                        OutlinedTextField(
                            value = estado.observaciones,
                            onValueChange = viewModel::cambiarObservaciones,
                            placeholder = {
                                Text(
                                    "Ej. La leche presentó acidez elevada de 19°D. Se recomienda revisar el proceso de ordeño y limpieza de equipos.",
                                    color = pe.edu.upeu.acopioleche.ui.theme.TextoTenue,
                                )
                            },
                            minLines = 3,
                            shape = RoundedCornerShape(13.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = AzulTextoBoton,
                                unfocusedBorderColor = BordeClaro,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    BotonGrande(
                        etiqueta = "Guardar visita",
                        onClick = viewModel::guardar,
                        variante = VarianteBoton.DORADO,
                        habilitado = estado.puedeGuardar,
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcionResultado(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val (colorBorde, colorFondo, colorTexto) = when (texto) {
        "Óptimo" -> Triple(VerdeExito, VerdeExitoFondo, VerdeExitoTexto)
        "Observación" -> Triple(AmarilloAlerta, AmarilloAlertaFondo, AmbarTexto)
        else -> Triple(RojoError, RojoErrorFondo, RojoErrorTexto)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(if (seleccionado) colorFondo else Color.White, RoundedCornerShape(12.dp))
            .border(if (seleccionado) 2.dp else 1.5.dp, if (seleccionado) colorBorde else BordeClaro, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(20.dp)
                .border(if (seleccionado) 6.dp else 2.dp, if (seleccionado) colorBorde else BordeClaro, CircleShape),
        )
        Text(
            texto,
            style = MaterialTheme.typography.titleSmall,
            color = if (seleccionado) colorTexto else TextoSecundario,
        )
    }
}
