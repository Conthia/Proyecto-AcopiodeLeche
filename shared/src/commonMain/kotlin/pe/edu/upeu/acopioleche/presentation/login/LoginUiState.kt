package pe.edu.upeu.acopioleche.presentation.login

import pe.edu.upeu.acopioleche.domain.model.SesionActiva

data class LoginUiState(
    val nombreUsuario: String = "",
    val contrasena: String = "",
    val validando: Boolean = false,
    val mensajeError: String? = null,
    val sesionIniciada: SesionActiva? = null,
)
