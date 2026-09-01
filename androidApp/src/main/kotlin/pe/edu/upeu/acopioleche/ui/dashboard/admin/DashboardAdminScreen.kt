package pe.edu.upeu.acopioleche.ui.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonCerrarSesion
import pe.edu.upeu.acopioleche.ui.common.DashboardHeader
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion
import pe.edu.upeu.acopioleche.ui.common.RolUsuario
import pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.MoradoAdministrador
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue

@Composable
fun DashboardAdminScreen(
    onIrAProveedores: () -> Unit,
    onIrAAcopiadores: () -> Unit,
    onIrAAgenda: () -> Unit,
    onIrAEntregas: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardAdminViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()
    val horaActual = remember {
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.forLanguageTag("es-PE")).format(java.util.Date())
    }

    Column(modifier = modifier.fillMaxSize()) {
        DashboardHeader(
            rol = RolUsuario.ADMINISTRADOR,
            nombreUsuario = "Carlos Mamani",
            estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
            horaActual = horaActual,
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .background(MoradoAdministrador.copy(alpha = 0.15f), RoundedCornerShape(9.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(AcopioIcons.PanelAdmin, contentDescription = null, tint = MoradoAdministrador, modifier = Modifier.size(13.dp))
                Text(
                    "Panel de administración · ${estado.fechaHoy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC4B5FD),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    estado.estadisticas.chunked(2).forEach { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            fila.forEach { stat ->
                                Column(
                                    modifier = Modifier.weight(1f).background(stat.fondo, RoundedCornerShape(14.dp)).padding(13.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(stat.valor, style = MaterialTheme.typography.headlineMedium, color = stat.color)
                                        if (stat.unidad.isNotEmpty()) {
                                            Text(stat.unidad, style = MaterialTheme.typography.titleSmall, color = stat.color, modifier = Modifier.padding(start = 2.dp))
                                        }
                                    }
                                    Text(stat.etiqueta, style = MaterialTheme.typography.labelSmall, color = stat.color.copy(alpha = 0.75f), modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("GESTIÓN", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TarjetaGestion("Proveedores", "${estado.cantidadProveedores} registros", AcopioIcons.Personas, AzulFondoTarjeta, onIrAProveedores, Modifier.weight(1f))
                    TarjetaGestion("Acopiadores", "${estado.cantidadAcopiadores} registros", AcopioIcons.Turbon, pe.edu.upeu.acopioleche.ui.theme.MoradoAdministradorFondo, onIrAAcopiadores, Modifier.weight(1f))
                    TarjetaGestion("Agenda", "${estado.cantidadEventos} registros", AcopioIcons.Calendario, pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaFondo, onIrAAgenda, Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulTextoBoton, RoundedCornerShape(16.dp))
                        .clickable(onClick = onIrAEntregas)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(modifier = Modifier.size(44.dp).background(MoradoAdministrador, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(AcopioIcons.Recibo, contentDescription = null, tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ver todas las entregas", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("Historial completo del sistema", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(top = 1.dp))
                    }
                    Icon(AcopioIcons.ChevronDerecha, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                }
            }

            item {
                Text("ACTIVIDAD RECIENTE", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
            }
            items(estado.actividadReciente) { actividad ->
                FilaActividad(actividad)
            }

            item { BotonCerrarSesion(onClick = onCerrarSesion) }
        }
    }
}

@Composable
private fun TarjetaGestion(
    etiqueta: String,
    conteo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    fondoIcono: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.5.dp, BordeSuave, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(40.dp).background(fondoIcono, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
            Icon(icono, contentDescription = null, tint = AzulTextoBoton)
        }
        Text(etiqueta, style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton, modifier = Modifier.padding(top = 7.dp))
        Text(conteo, style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 1.dp))
    }
}

@Composable
private fun FilaActividad(actividad: ActividadReciente) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(32.dp).background(FondoSecundario, RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
            Icon(iconoParaActividad(actividad.tipo), contentDescription = null, tint = colorParaActividad(actividad.tipo), modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(actividad.descripcion, style = MaterialTheme.typography.bodySmall, color = AzulTextoBoton)
            Text(actividad.tiempoRelativo, style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun iconoParaActividad(tipo: TipoActividad) = when (tipo) {
    TipoActividad.ENTREGA -> AcopioIcons.Gota
    TipoActividad.REGISTRO -> AcopioIcons.Personas
    TipoActividad.EVENTO -> AcopioIcons.Calendario
    TipoActividad.SINCRONIZACION -> AcopioIcons.Sincronizando
}

private fun colorParaActividad(tipo: TipoActividad): Color = when (tipo) {
    TipoActividad.ENTREGA -> pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
    TipoActividad.REGISTRO -> pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro
    TipoActividad.EVENTO -> pe.edu.upeu.acopioleche.ui.theme.MoradoAdministradorOscuro
    TipoActividad.SINCRONIZACION -> pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
}
