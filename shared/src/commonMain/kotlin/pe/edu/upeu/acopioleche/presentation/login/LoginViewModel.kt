package pe.edu.upeu.acopioleche.presentation.login

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.EstadoBloqueoCuenta
import pe.edu.upeu.acopioleche.domain.model.ResultadoLogin
import pe.edu.upeu.acopioleche.domain.model.SesionActiva
import pe.edu.upeu.acopioleche.domain.model.Usuario
import pe.edu.upeu.acopioleche.domain.repository.UsuarioRepository
import pe.edu.upeu.acopioleche.domain.service.PasswordHasher
import pe.edu.upeu.acopioleche.domain.service.PoliticaBloqueoLogin
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class LoginViewModel(
    scope: CoroutineScope,
    private val usuarioRepository: UsuarioRepository,
    private val reglasNegocio: ReglasNegocio,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNombreUsuarioChange(valor: String) {
        _uiState.value = _uiState.value.copy(nombreUsuario = valor, mensajeError = null)
    }

    fun onContrasenaChange(valor: String) {
        _uiState.value = _uiState.value.copy(contrasena = valor, mensajeError = null)
    }

    fun onIngresarClick() {
        val estado = _uiState.value
        if (estado.nombreUsuario.isBlank() || estado.contrasena.isBlank()) {
            _uiState.value = estado.copy(mensajeError = "Ingresa usuario y contraseña")
            return
        }
        scope.launch {
            _uiState.value = _uiState.value.copy(validando = true, mensajeError = null)
            val resultado = autenticar(
                nombreUsuario = estado.nombreUsuario,
                contrasenaPlano = estado.contrasena,
                ahora = Clock.System.now(),
            )
            _uiState.value = when (resultado) {
                is ResultadoLogin.Exitoso -> {
                    SesionActivaHolder.iniciar(sesion = resultado.sesion)
                    _uiState.value.copy(validando = false, sesionIniciada = resultado.sesion)
                }
                is ResultadoLogin.CredencialesInvalidas ->
                    _uiState.value.copy(validando = false, mensajeError = "Usuario o contraseña incorrectos")
                is ResultadoLogin.CuentaBloqueada ->
                    _uiState.value.copy(
                        validando = false,
                        mensajeError = "Cuenta bloqueada por intentos fallidos. Espere ${resultado.minutosRestantes} minutos e inténtelo de nuevo.",
                    )
            }
        }
    }

    private suspend fun autenticar(
        nombreUsuario: String,
        contrasenaPlano: String,
        ahora: Instant,
    ): ResultadoLogin {
        val usuario: Usuario = usuarioRepository.buscarPorNombreUsuario(nombreUsuario = nombreUsuario)
            ?: return ResultadoLogin.CredencialesInvalidas

        when (val bloqueo = PoliticaBloqueoLogin.evaluar(usuario.intentosFallidos, usuario.ultimoIntentoFallidoEn, ahora, reglasNegocio)) {
            is EstadoBloqueoCuenta.Bloqueado -> return ResultadoLogin.CuentaBloqueada(minutosRestantes = bloqueo.minutosRestantes)
            is EstadoBloqueoCuenta.Habilitado -> {
                if (usuario.intentosFallidos >= reglasNegocio.maxIntentosFallidos) {
                    usuarioRepository.reiniciarIntentos(usuarioId = usuario.id)
                }
            }
        }

        val credencialesValidas = PasswordHasher.verificar(contrasenaPlano = contrasenaPlano, contra = usuario.contrasena)
        if (!credencialesValidas) {
            val nuevosIntentos = usuario.intentosFallidos + 1
            usuarioRepository.registrarIntentoFallido(usuarioId = usuario.id, momento = ahora)
            val bloqueoTrasFallo = PoliticaBloqueoLogin.evaluar(nuevosIntentos, ahora, ahora, reglasNegocio)
            if (bloqueoTrasFallo is EstadoBloqueoCuenta.Bloqueado) {
                return ResultadoLogin.CuentaBloqueada(minutosRestantes = bloqueoTrasFallo.minutosRestantes)
            }
            return ResultadoLogin.CredencialesInvalidas
        }

        usuarioRepository.registrarLoginExitoso(usuarioId = usuario.id)
        return ResultadoLogin.Exitoso(
            sesion = SesionActiva(
                usuarioId = usuario.id,
                nombreCompleto = usuario.nombreCompleto,
                rol = usuario.rol,
                centroAcopioId = usuario.centroAcopioId,
                proveedorId = usuario.proveedorId,
            ),
        )
    }
}
