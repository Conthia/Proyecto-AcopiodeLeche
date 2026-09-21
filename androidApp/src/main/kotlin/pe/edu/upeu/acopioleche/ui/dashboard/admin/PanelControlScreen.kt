package pe.edu.upeu.acopioleche.ui.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.presentation.dashboard.admin.PanelControlViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun PanelControlScreen(
    alVerLiquidaciones: () -> Unit,
    alVerConciliacion: () -> Unit,
    alVerNotificaciones: () -> Unit,
    alAbrirPerfil: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PanelControlViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            analisisCalidadRepository = ServiceLocator.analisisCalidadRepository,
            equipoCampoRepository = ServiceLocator.equipoCampoRepository,
            centroAcopioRepository = ServiceLocator.centroAcopioRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "PanelControlScreen requiere una sesión activa" }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Panel de control",
                subtitulo = "Administrador · ${sesionActiva.nombreCompleto}",
                alAbrirPerfil = alAbrirPerfil,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        AppCard(modifier = Modifier.padding(end = 0.dp)) {
                            StatTile(valor = "${uiState.litrosSemana.toInt()}", etiqueta = "litros esta semana")
                        }
                    }
                    item {
                        AppCard {
                            StatTile(valor = "${uiState.proveedoresActivos}", etiqueta = "proveedores activos")
                        }
                    }
                    item {
                        AppCard {
                            StatTile(
                                valor = "${"%.1f".format(uiState.grasaPromedioPorcentaje)} %",
                                etiqueta = "grasa promedio",
                                color = VerdeOscuro,
                            )
                        }
                    }
                    item {
                        AppCard {
                            StatTile(
                                valor = "${uiState.numeroAlertasAgua}",
                                etiqueta = "alertas de agua añadida",
                                color = RojoAlerta,
                            )
                        }
                    }
                }
            }

            if (uiState.volumenPorSectorHoy.isNotEmpty()) {
                item {
                    AppCard {
                        SectionLabel(texto = "Recepción de hoy por Sector (L)")
                        // LazyRow con ancho mínimo fijo por tarjeta: si hay muchos sectores, la fila se
                        // desplaza horizontalmente en vez de comprimir cada StatTile hasta que el texto
                        // se corte letra por letra.
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(uiState.volumenPorSectorHoy) { itemSector ->
                                StatTile(
                                    valor = "${itemSector.litros.toInt()} L",
                                    etiqueta = itemSector.sector.replace("Sector ", ""),
                                    modifier = Modifier.width(96.dp),
                                )
                            }
                        }
                    }
                }
            }

            item {
                AppCard {
                    SectionLabel(texto = "Volumen semanal · litros/día")
                    VolumenSemanalChart(valores = uiState.volumenPorDia)
                }
            }

            item {
                Button(
                    onClick = alVerLiquidaciones,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text("Ver liquidaciones semanales")
                }
            }

            item {
                OutlinedButton(
                    onClick = alVerConciliacion,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Conciliación campo-planta")
                }
            }

            item {
                OutlinedButton(
                    onClick = alVerNotificaciones,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Bandeja de notificaciones")
                }
            }

            if (uiState.alertas.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = RojoFondo, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "ALERTAS DE CALIDAD REGISTRADAS HOY",
                            color = RojoAlerta,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        uiState.alertas.forEach { alerta -> AlertaCalidadRow(alerta = alerta) }
                    }
                }
            }

            item {
                AppCard {
                    SectionLabel(texto = "Equipos en campo")
                    uiState.equipos.forEach { equipo -> EquipoCampoRow(equipo = equipo) }
                }
            }
        }
    }
}
