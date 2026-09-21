package pe.edu.upeu.acopioleche.ui.liquidacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.presentation.liquidacion.LiquidacionResumen
import pe.edu.upeu.acopioleche.presentation.liquidacion.LiquidacionesViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun LiquidacionesScreen(
    alVolver: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        LiquidacionesViewModel(
            scope = scope,
            proveedorRepository = ServiceLocator.proveedorRepository,
            entregaRepository = ServiceLocator.entregaRepository,
            sancionRepository = ServiceLocator.sancionRepository,
            liquidacionRepository = ServiceLocator.liquidacionRepository,
            notificacionRepository = ServiceLocator.notificacionRepository,
            precioTemporadaRepository = ServiceLocator.precioTemporadaRepository,
            reglasNegocio = ServiceLocator.reglasNegocio,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val generando by viewModel.generando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Liquidaciones",
                subtitulo = "Semana del ${viewModel.semanaInicio} · se paga el ${viewModel.fechaPago}",
                alVolver = alVolver,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(
                onClick = { viewModel.onGenerarClick() },
                enabled = !generando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
            ) {
                Text(if (generando) "Generando…" else "Generar liquidaciones de esta semana")
            }
            mensaje?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            when (val estado = uiState) {
                UiState.Cargando -> EstadoCargando(modifier = Modifier.weight(1f))
                UiState.Vacio -> EstadoVacio(
                    mensaje = "No hay proveedores activos para liquidar esta semana.",
                    modifier = Modifier.weight(1f),
                )
                is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.weight(1f))
                is UiState.Exito -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item { SectionLabel(texto = "Proveedores activos · ${estado.datos.resumenes.size}") }
                    items(estado.datos.resumenes) { resumen -> LiquidacionRow(resumen = resumen) }
                }
            }
        }
    }
}

@Composable
private fun LiquidacionRow(resumen: LiquidacionResumen) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(width = 1.dp, color = BordeTarjeta),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = resumen.nombreProveedor, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${resumen.litrosAceptados} L aceptados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
                if (resumen.tieneSancionPendienteDeMonto) {
                    Text(
                        text = "Sanción por adulteración: monto de descuento pendiente de confirmar",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbarTexto,
                    )
                }
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(text = "S/ ${resumen.montoFinal}", style = MaterialTheme.typography.titleMedium, color = VerdeOscuro)
                if (resumen.generada) {
                    EstadoBadge(texto = "Generada", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                } else {
                    EstadoBadge(texto = "Pendiente", color = AmbarTexto, colorFondo = AmbarFondo)
                }
            }
        }
    }
}
