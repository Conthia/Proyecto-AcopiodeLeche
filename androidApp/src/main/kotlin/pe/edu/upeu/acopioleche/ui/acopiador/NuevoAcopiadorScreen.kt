package pe.edu.upeu.acopioleche.ui.acopiador

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.FormInput
import pe.edu.upeu.acopioleche.ui.common.FormLabel
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.SuccessView
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun NuevoAcopiadorScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevoAcopiadorViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        NavHeader(titulo = "Nuevo Acopiador", onVolver = onVolver)

        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (estado.guardado) {
                SuccessView(
                    titulo = "¡Acopiador guardado!",
                    subtitulo = "${estado.nombre.ifBlank { "El acopiador" }} fue registrado exitosamente con ${estado.sectoresSeleccionados.size} sector(es) asignado(s).",
                    onNuevo = viewModel::nuevoRegistro,
                    onVolver = onVolver,
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
                    Text(
                        "Registre el recolector y asígnele los sectores que recorrerá.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )

                    FormInput("Nombre completo *", "Ej. Carlos Mamani Pari", estado.nombre, viewModel::cambiarNombre)

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Tipo de vehículo *")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TarjetaVehiculo(
                                etiqueta = "Turbón",
                                icono = AcopioIcons.Turbon,
                                seleccionado = estado.vehiculo == "Turbón",
                                onClick = { viewModel.seleccionarVehiculo("Turbón") },
                                modifier = Modifier.weight(1f),
                            )
                            TarjetaVehiculo(
                                etiqueta = "Motocarga",
                                icono = AcopioIcons.Motocarga,
                                seleccionado = estado.vehiculo == "Motocarga",
                                onClick = { viewModel.seleccionarVehiculo("Motocarga") },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        FormLabel("Sectores asignados (puede elegir varios)")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DatosDemo.sectores.forEach { sector ->
                                val seleccionado = sector in estado.sectoresSeleccionados
                                ChipMultiSeleccion(
                                    texto = sector,
                                    seleccionado = seleccionado,
                                    onClick = { viewModel.alternarSector(sector) },
                                )
                            }
                        }
                    }

                    BotonGrande(
                        etiqueta = "Guardar acopiador",
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
private fun TarjetaVehiculo(
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(72.dp)
            .background(if (seleccionado) AzulTextoBoton else Color.White, RoundedCornerShape(13.dp))
            .border(1.5.dp, if (seleccionado) AzulTextoBoton else BordeClaro, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icono, contentDescription = null, tint = if (seleccionado) Color.White else TextoSecundario)
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = if (seleccionado) Color.White else TextoSecundario,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ChipMultiSeleccion(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .background(if (seleccionado) AzulFondoTarjeta else Color.White, RoundedCornerShape(10.dp))
            .border(1.5.dp, if (seleccionado) AzulSecundario else BordeClaro, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (seleccionado) {
            Icon(AcopioIcons.Check, contentDescription = null, tint = AzulSecundario, modifier = Modifier.height(13.dp))
        }
        Text(
            texto,
            style = MaterialTheme.typography.bodySmall,
            color = if (seleccionado) AzulSecundario else TextoSecundario,
        )
    }
}
