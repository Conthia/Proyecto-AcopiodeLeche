package pe.edu.upeu.acopioleche.ui.dashboard.tecnico

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonCerrarSesion
import pe.edu.upeu.acopioleche.ui.common.DashboardHeader
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion
import pe.edu.upeu.acopioleche.ui.common.RolUsuario
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue
import pe.edu.upeu.acopioleche.ui.theme.VerdeExito
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoTexto
import pe.edu.upeu.acopioleche.ui.theme.VerdeTecnico

@Composable
fun DashboardTecnicoScreen(
    onIrAAgenda: () -> Unit,
    onIrANuevaVisita: () -> Unit,
    onIrAAsistenciaRapida: () -> Unit,
    onIrAProveedores: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardTecnicoViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()
    val horaActual = remember {
        java.text.SimpleDateFormat("HH:mm", java.util.Locale.forLanguageTag("es-PE")).format(java.util.Date())
    }

    Column(modifier = modifier.fillMaxSize()) {
        DashboardHeader(
            rol = RolUsuario.TECNICO,
            nombreUsuario = "Carlos Mamani",
            estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
            horaActual = horaActual,
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .background(VerdeTecnico.copy(alpha = 0.15f), RoundedCornerShape(9.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(AcopioIcons.Check, contentDescription = null, tint = VerdeTecnico, modifier = Modifier.size(13.dp))
                Text(
                    "${estado.visitasProgramadasHoy} visitas programadas para hoy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF86EFAC),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(VerdeExitoOscuro, VerdeExito)), RoundedCornerShape(18.dp))
                        .clickable(onClick = onIrAAgenda)
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier.size(54.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(AcopioIcons.Calendario, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Agenda de Capacitaciones", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Text(
                            "Próximo evento: 05 Sep · 09:00",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Icon(AcopioIcons.ChevronDerecha, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                }
            }

            item {
                Text(
                    "INDICADORES DE CALIDAD",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario,
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    estado.indicadores.chunked(2).forEach { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            fila.forEach { indicador ->
                                TarjetaIndicador(indicador, modifier = Modifier.weight(1f))
                            }
                            if (fila.size == 1) Box(modifier = Modifier.weight(1f))
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
                    Text("VISITAS TÉCNICAS", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                    Text(
                        "Nueva visita",
                        style = MaterialTheme.typography.titleSmall,
                        color = AzulSecundario,
                        modifier = Modifier.clickable(onClick = onIrANuevaVisita),
                    )
                }
            }
            items(estado.visitasRecientes) { visita ->
                FilaVisita(visita)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    TarjetaAcceso(
                        icono = AcopioIcons.Personas,
                        etiqueta = "Asistencia",
                        subetiqueta = "Control de asistentes",
                        onClick = onIrAAsistenciaRapida,
                        modifier = Modifier.weight(1f),
                    )
                    TarjetaAcceso(
                        icono = AcopioIcons.Personas,
                        etiqueta = "Proveedores",
                        subetiqueta = "Ver productores",
                        onClick = onIrAProveedores,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item { BotonCerrarSesion(onClick = onCerrarSesion) }
        }
    }
}

@Composable
private fun TarjetaIndicador(indicador: IndicadorCalidad, modifier: Modifier = Modifier) {
    val esOk = indicador.estado == EstadoIndicador.OK
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.5.dp, if (esOk) VerdeExitoFondo.copy(alpha = 1f) else AmarilloAlertaFondo, RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                indicador.etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = TextoTenue,
                modifier = Modifier.weight(1f),
            )
            Box(modifier = Modifier.size(8.dp).background(if (esOk) VerdeExito else pe.edu.upeu.acopioleche.ui.theme.Dorado, CircleShape))
        }
        Text(indicador.valor, style = MaterialTheme.typography.headlineSmall, color = AzulTextoBoton, modifier = Modifier.padding(top = 4.dp))
        Text("Meta: ${indicador.meta}", style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun FilaVisita(visita: VisitaResumen) {
    val esOptimo = visita.resultado == "Óptimo"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(if (esOptimo) VerdeExitoFondo else AmarilloAlertaFondo, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (esOptimo) AcopioIcons.Check else AcopioIcons.Alerta,
                contentDescription = null,
                tint = if (esOptimo) VerdeExitoOscuro else AmbarTexto,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(visita.proveedorNombre, style = MaterialTheme.typography.bodyMedium, color = AzulTextoBoton)
            Text("${visita.tipo} · ${visita.fecha}", style = MaterialTheme.typography.labelSmall, color = TextoTenue, modifier = Modifier.padding(top = 1.dp))
        }
        Text(
            visita.resultado,
            style = MaterialTheme.typography.labelSmall,
            color = if (esOptimo) VerdeExitoTexto else AmbarTexto,
            modifier = Modifier
                .background(if (esOptimo) VerdeExitoFondo else AmarilloAlertaFondo, RoundedCornerShape(7.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun TarjetaAcceso(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    etiqueta: String,
    subetiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.5.dp, BordeSuave, RoundedCornerShape(16.dp))
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
