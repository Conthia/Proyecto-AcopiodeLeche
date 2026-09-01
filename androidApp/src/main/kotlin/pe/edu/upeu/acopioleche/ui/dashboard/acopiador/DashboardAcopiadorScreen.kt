package pe.edu.upeu.acopioleche.ui.dashboard.acopiador

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
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
fun DashboardAcopiadorScreen(
    onIrARegistrarEntrega: () -> Unit,
    onIrAAgenda: () -> Unit,
    onIrANuevoProveedor: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardAcopiadorViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        val formato = SimpleDateFormat("HH:mm", Locale.forLanguageTag("es-PE"))
        viewModel.actualizarHora(formato.format(Date()))
    }

    val hayPendientes = estado.entregas.any { it.estadoSincronizacion == EstadoSincronizacion.PENDIENTE }
    val estadoGeneralSync = if (hayPendientes) EstadoSincronizacion.PENDIENTE else EstadoSincronizacion.SINCRONIZADO

    Column(modifier = modifier.fillMaxSize()) {
        DashboardHeader(
            rol = RolUsuario.ACOPIADOR,
            nombreUsuario = "Carlos Mamani",
            estadoSincronizacion = estadoGeneralSync,
            horaActual = estado.horaActual,
        ) {
            if (hayPendientes) {
                val pendientes = estado.entregas.count { it.estadoSincronizacion == EstadoSincronizacion.PENDIENTE }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Dorado.copy(alpha = 0.2f), RoundedCornerShape(9.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(AcopioIcons.Sincronizando, contentDescription = null, tint = Dorado, modifier = Modifier.size(13.dp))
                    Text("$pendientes entregas pendientes de sincronizar", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFCD34D))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    TarjetaEstadistica(
                        etiqueta = "Litros hoy",
                        valor = "%.1f L".format(estado.litrosHoy),
                        color = AzulSecundario,
                        fondo = AzulFondoTarjeta,
                        modifier = Modifier.weight(1f),
                    )
                    TarjetaEstadistica(
                        etiqueta = "Entregas",
                        valor = "${estado.cantidadEntregas}",
                        color = VerdeExitoOscuro,
                        fondo = VerdeExitoFondo,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                TarjetaAccionPrincipal(
                    titulo = "Registrar Entrega",
                    subtitulo = "Registrar litros de leche recolectados",
                    onClick = onIrARegistrarEntrega,
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    TarjetaAccesoSecundario(
                        icono = AcopioIcons.Calendario,
                        etiqueta = "Mi Agenda",
                        subetiqueta = "Eventos del día",
                        onClick = onIrAAgenda,
                        modifier = Modifier.weight(1f),
                    )
                    TarjetaAccesoSecundario(
                        icono = AcopioIcons.Personas,
                        etiqueta = "Proveedores",
                        subetiqueta = "Registrar productor",
                        onClick = onIrANuevoProveedor,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Text("Mis sectores asignados", style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton)
                    Row(modifier = Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        estado.sectoresAsignados.forEach { sector ->
                            Text(
                                sector,
                                style = MaterialTheme.typography.labelSmall,
                                color = AzulSecundario,
                                modifier = Modifier
                                    .background(AzulFondoTarjeta, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "REGISTROS DE HOY",
                    style = MaterialTheme.typography.labelMedium,
                    color = pe.edu.upeu.acopioleche.ui.theme.TextoSecundario,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            items(estado.entregas.take(3)) { entregaUi ->
                FilaEntregaResumen(entregaUi)
            }

            item {
                BotonCerrarSesion(onClick = onCerrarSesion)
            }
        }
    }
}

@Composable
private fun TarjetaEstadistica(etiqueta: String, valor: String, color: Color, fondo: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(fondo, RoundedCornerShape(14.dp)).padding(13.dp)) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, color = color)
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.75f), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun TarjetaAccionPrincipal(titulo: String, subtitulo: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Dorado, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(modifier = Modifier.size(54.dp).background(AzulTextoBoton, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            Icon(AcopioIcons.Gota, contentDescription = null, tint = Dorado, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, color = AzulTextoBoton)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = AzulTextoBoton.copy(alpha = 0.55f), modifier = Modifier.padding(top = 2.dp))
        }
        Icon(AcopioIcons.ChevronDerecha, contentDescription = null, tint = AzulTextoBoton.copy(alpha = 0.5f))
    }
}

@Composable
private fun TarjetaAccesoSecundario(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    subetiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(13.dp),
    ) {
        Box(modifier = Modifier.size(44.dp).background(AzulFondoTarjeta, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icono, contentDescription = null, tint = AzulSecundario)
        }
        Text(etiqueta, style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton, modifier = Modifier.padding(top = 9.dp))
        Text(subetiqueta, style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun FilaEntregaResumen(entregaUi: pe.edu.upeu.acopioleche.ui.entrega.EntregaUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (entregaUi.estadoSincronizacion == EstadoSincronizacion.SINCRONIZADO) VerdeExito else Dorado,
                    androidx.compose.foundation.shape.CircleShape,
                ),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(entregaUi.proveedorNombre, style = MaterialTheme.typography.bodyMedium, color = AzulTextoBoton)
            Text("Turno ${entregaUi.entrega.turno}", style = MaterialTheme.typography.labelSmall, color = TextoTenue)
        }
        Text("${entregaUi.entrega.volumenLitros} L", style = MaterialTheme.typography.titleMedium, color = AzulSecundario)
    }
}
