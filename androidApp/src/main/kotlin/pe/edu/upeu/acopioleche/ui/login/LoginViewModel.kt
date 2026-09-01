package pe.edu.upeu.acopioleche.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.ui.common.RolUsuario

private const val INTENTOS_MAXIMOS = 3

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun seleccionarRol(rol: RolUsuario) {
        _uiState.update { it.copy(rolSeleccionado = rol) }
    }

    fun cambiarUsuario(usuario: String) {
        _uiState.update { it.copy(usuario = usuario) }
    }

    fun cambiarPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun ingresar() {
        val estado = _uiState.value
        if (estado.bloqueado) return

        if (estado.usuario.isNotBlank() && estado.password.isNotBlank()) {
            _uiState.update { it.copy(loginExitoso = true, intentos = 0) }
        } else {
            val siguientesIntentos = estado.intentos + 1
            _uiState.update {
                it.copy(
                    intentos = siguientesIntentos,
                    bloqueado = siguientesIntentos >= INTENTOS_MAXIMOS,
                )
            }
        }
    }

    fun reiniciar() {
        _uiState.value = LoginUiState()
    }
}
