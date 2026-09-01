package pe.edu.upeu.acopioleche.ui.login

import pe.edu.upeu.acopioleche.ui.common.RolUsuario

data class LoginUiState(
    val rolSeleccionado: RolUsuario = RolUsuario.ACOPIADOR,
    val usuario: String = "",
    val password: String = "",
    val intentos: Int = 0,
    val bloqueado: Boolean = false,
    val loginExitoso: Boolean = false,
)
