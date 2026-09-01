package pe.edu.upeu.acopioleche.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.EcoLogo
import pe.edu.upeu.acopioleche.ui.common.FormInput
import pe.edu.upeu.acopioleche.ui.common.FormLabel
import pe.edu.upeu.acopioleche.ui.common.RolUsuario
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaFondo
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlertaTexto
import pe.edu.upeu.acopioleche.ui.theme.AzulMarinoOscuro
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.Dorado
import pe.edu.upeu.acopioleche.ui.theme.FondoGeneral
import pe.edu.upeu.acopioleche.ui.theme.RojoError
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorFondo
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorTexto
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun LoginScreen(
    onLoginExitoso: (RolUsuario) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.reiniciar()
    }

    LaunchedEffect(estado.loginExitoso) {
        if (estado.loginExitoso) {
            onLoginExitoso(estado.rolSeleccionado)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(FondoGeneral)) {
        // Hero
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AzulMarinoOscuro, AzulSecundario)))
                .padding(top = 36.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            EcoLogo(tamano = 96.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("EcoLácteos Huata", style = MaterialTheme.typography.headlineMedium, color = Dorado)
                Text(
                    "SISTEMA DE ACOPIO DE LECHE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            Text(
                "Municipalidad de Huata · Puno",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            )
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 22.dp, start = 20.dp, end = 20.dp, bottom = 24.dp),
        ) {
            Column(modifier = Modifier.padding(bottom = 18.dp)) {
                FormLabel("Perfil de acceso")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RolUsuario.entries.forEach { rol ->
                        val seleccionado = estado.rolSeleccionado == rol
                        Button(
                            onClick = { viewModel.seleccionarRol(rol) },
                            shape = RoundedCornerShape(11.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (seleccionado) AzulTextoBoton else Color.White,
                                contentColor = if (seleccionado) Color.White else TextoSecundario,
                            ),
                            border = if (!seleccionado) androidx.compose.foundation.BorderStroke(1.5.dp, BordeClaro) else null,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.weight(1f).height(44.dp),
                        ) {
                            Text(rol.etiqueta, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                        }
                    }
                }
            }

            FormInput(
                etiqueta = "Usuario",
                placeholder = "Ingrese su usuario",
                valor = estado.usuario,
                onValorCambia = viewModel::cambiarUsuario,
            )
            FormInput(
                etiqueta = "Contraseña",
                placeholder = "••••••••",
                valor = estado.password,
                onValorCambia = viewModel::cambiarPassword,
                esPassword = true,
                tipoTeclado = KeyboardType.Password,
            )

            if (estado.intentos > 0 && !estado.bloqueado) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AmarilloAlertaFondo, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(AcopioIcons.Alerta, contentDescription = null, tint = Dorado, modifier = Modifier.height(18.dp))
                    Text(
                        "Credenciales incorrectas. Intento ${estado.intentos} de 3.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmarilloAlertaTexto,
                    )
                }
            }

            if (estado.bloqueado) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RojoErrorFondo, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(AcopioIcons.Bloqueado, contentDescription = null, tint = RojoError, modifier = Modifier.height(20.dp))
                    Column {
                        Text(
                            "Cuenta bloqueada temporalmente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RojoErrorTexto,
                        )
                        Text(
                            "Demasiados intentos fallidos. Contacte al administrador o espere 10 minutos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RojoErrorTexto,
                        )
                    }
                }
            }

            BotonGrande(
                etiqueta = if (estado.bloqueado) "Cuenta bloqueada" else "Ingresar",
                onClick = viewModel::ingresar,
                habilitado = !estado.bloqueado,
                variante = VarianteBoton.DORADO,
            )
        }
    }
}
