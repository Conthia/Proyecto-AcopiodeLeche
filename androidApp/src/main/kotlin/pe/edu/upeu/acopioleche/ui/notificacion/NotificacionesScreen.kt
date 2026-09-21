package pe.edu.upeu.acopioleche.ui.notificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion
import pe.edu.upeu.acopioleche.presentation.notificacion.NotificacionResumen
import pe.edu.upeu.acopioleche.presentation.notificacion.NotificacionesViewModel
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.TopBarChip
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun NotificacionesScreen(
    alVolver: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        NotificacionesViewModel(
            scope = scope,
            notificacionRepository = ServiceLocator.notificacionRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Notificaciones",
                subtitulo = "Avisos enviados a proveedores",
                alVolver = alVolver,
                accesorio = { TopBarChip(texto = "${uiState.numeroNoLeidas} sin leer") },
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(uiState.notificaciones) { notificacion ->
                NotificacionRow(notificacion = notificacion, alTocar = { viewModel.onMarcarLeidaClick(notificacion.id) })
            }
        }
    }
}

@Composable
private fun NotificacionRow(notificacion: NotificacionResumen, alTocar: () -> Unit) {
    Card(
        onClick = alTocar,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(width = 1.dp, color = if (notificacion.leida) BordeTarjeta else AmbarTexto),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EstadoBadge(texto = etiquetaTipo(notificacion.tipo), color = AmbarTexto, colorFondo = AmbarFondo)
                if (notificacion.sonidoDistintivo) {
                    EstadoBadge(texto = "🔔 sonido distintivo", color = TextoSecundario, colorFondo = VerdeSuaveFondo)
                }
            }
            Text(
                text = notificacion.mensaje,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.SemiBold,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = "${notificacion.nombreDestinatario} · ${notificacion.fechaEnvio}${if (notificacion.leida) "" else " · sin leer"}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private fun etiquetaTipo(tipo: TipoNotificacion): String =
    when (tipo) {
        TipoNotificacion.RESUMEN_ENTREGA_DIARIA -> "Resumen diario"
        TipoNotificacion.RESUMEN_ENTREGA_SEMANAL -> "Resumen semanal"
        TipoNotificacion.ALERTA_ADULTERACION -> "Alerta de adulteración"
        TipoNotificacion.RESULTADO_DENSIDAD -> "Resultado de densidad"
        TipoNotificacion.CITACION_REUNION -> "Citación a reunión"
        TipoNotificacion.AVISO_CAPACITACION -> "Aviso de capacitación"
    }
