package pe.edu.upeu.acopioleche.ui.proveedor

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
import androidx.compose.foundation.verticalScroll
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
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.FormInput
import pe.edu.upeu.acopioleche.ui.common.FormLabel
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.SuccessView
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue

@Composable
fun NuevoProveedorScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NuevoProveedorViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        NavHeader(titulo = "Nuevo Proveedor", onVolver = onVolver)

        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (estado.guardado) {
                SuccessView(
                    titulo = "¡Proveedor guardado!",
                    subtitulo = "${estado.nombre.ifBlank { "El proveedor" }} fue registrado exitosamente en el sistema.",
                    onNuevo = viewModel::nuevoRegistro,
                    onVolver = onVolver,
                )
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                ) {
                    Text(
                        "Complete los datos del productor lechero. Los campos marcados son obligatorios para el registro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )

                    FormInput("Nombre completo *", "Ej. Juan Quispe Mamani", estado.nombre, viewModel::cambiarNombre)
                    FormInput("DNI / Carnet de extranjería *", "Ej. 42356789", estado.documento, viewModel::cambiarDocumento)
                    FormInput("Teléfono / Celular", "Ej. 951 234 567", estado.telefono, viewModel::cambiarTelefono)

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        FormLabel("Sector / Zona *")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DatosDemo.sectores.forEach { sector ->
                                ChipSeleccionable(
                                    texto = sector,
                                    seleccionado = estado.sector == sector,
                                    onClick = { viewModel.seleccionarSector(sector) },
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        FormLabel("Modalidad de entrega *")
                        OpcionModalidad(
                            titulo = "Entrega directa en planta",
                            descripcion = "El proveedor lleva su leche al centro de acopio",
                            seleccionado = estado.entregaDirectaEnPlanta,
                            onClick = { viewModel.seleccionarModalidad(true) },
                        )
                        OpcionModalidad(
                            titulo = "Mediante un acopiador",
                            descripcion = "Un recolector pasa a recoger la leche en campo",
                            seleccionado = !estado.entregaDirectaEnPlanta,
                            onClick = { viewModel.seleccionarModalidad(false) },
                        )
                    }

                    BotonGrande(
                        etiqueta = "Guardar proveedor",
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
private fun ChipSeleccionable(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .height(40.dp)
            .background(if (seleccionado) AzulTextoBoton else Color.White, RoundedCornerShape(10.dp))
            .border(1.5.dp, if (seleccionado) AzulTextoBoton else BordeClaro, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = if (seleccionado) Color.White else TextoSecundario,
        )
    }
}

@Composable
private fun OpcionModalidad(titulo: String, descripcion: String, seleccionado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(if (seleccionado) FondoSecundario else Color.White, RoundedCornerShape(13.dp))
            .border(if (seleccionado) 2.dp else 1.5.dp, if (seleccionado) AzulTextoBoton else BordeClaro, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RadioPunto(seleccionado)
        Column {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton)
            Text(descripcion, style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun RadioPunto(seleccionado: Boolean) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(22.dp)
            .border(if (seleccionado) 6.dp else 2.dp, if (seleccionado) AzulTextoBoton else BordeClaro, CircleShape),
    )
}
