package pe.edu.upeu.acopioleche.ui.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.AvatarIniciales
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun PerfilScreen(
    alVolver: () -> Unit,
    alCerrarSesion: () -> Unit,
) {
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = sesion

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Mi Perfil",
                subtitulo = sesionActiva?.nombreCompleto ?: "Usuario",
                alVolver = alVolver,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (sesionActiva != null) {
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AvatarIniciales(nombre = sesionActiva.nombreCompleto)
                        Column {
                            Text(
                                text = sesionActiva.nombreCompleto,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = VerdeOscuro,
                            )
                            Text(
                                text = "Usuario ID: ${sesionActiva.usuarioId}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario,
                            )
                        }
                    }
                }

                AppCard {
                    SectionLabel(texto = "Información de cuenta")
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PerfilInfoRow(
                            etiqueta = "Rol asignado",
                            valor = when (sesionActiva.rol) {
                                RolUsuario.ACOPIADOR -> "Acopiador de Campo"
                                RolUsuario.ADMINISTRADOR -> "Administrador"
                                RolUsuario.ENCARGADO_PAGOS -> "Encargado de Pagos"
                                RolUsuario.PRODUCTOR_LACTEOS -> "Productor de Lácteos"
                                RolUsuario.PRODUCTOR -> "Productor de Leche"
                            },
                        )
                        PerfilInfoRow(
                            etiqueta = "Centro de Acopio",
                            valor = when (sesionActiva.centroAcopioId) {
                                "CA-001" -> "Huata Centro"
                                "CA-002" -> "Coyme"
                                "CA-003" -> "Pallalla"
                                null -> "Supervisión General (Todos)"
                                else -> sesionActiva.centroAcopioId ?: "Sin asignar"
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    SesionActivaHolder.cerrar()
                    alCerrarSesion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoAlerta,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PerfilInfoRow(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        Text(text = valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = VerdeOscuro)
    }
}
