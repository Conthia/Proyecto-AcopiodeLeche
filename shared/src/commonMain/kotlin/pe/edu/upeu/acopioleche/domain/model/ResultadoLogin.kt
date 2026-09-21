package pe.edu.upeu.acopioleche.domain.model

/**
 * Resultado de un intento de inicio de sesión (RF-01). `CredencialesInvalidas` cubre tanto
 * usuario/contraseña incorrectos como un rol seleccionado que no corresponde a la cuenta: no se
 * distingue a propósito, para no revelar a un atacante cuál de los tres campos falló.
 */
sealed interface ResultadoLogin {
    data class Exitoso(
        val sesion: SesionActiva,
    ) : ResultadoLogin

    data object CredencialesInvalidas : ResultadoLogin

    data class CuentaBloqueada(
        val minutosRestantes: Int,
    ) : ResultadoLogin
}
