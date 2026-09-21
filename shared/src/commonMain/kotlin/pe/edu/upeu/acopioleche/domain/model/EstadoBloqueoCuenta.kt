package pe.edu.upeu.acopioleche.domain.model

/**
 * Resultado de evaluar la política de bloqueo temporal de RF-01
 * ([pe.edu.upeu.acopioleche.domain.service.PoliticaBloqueoLogin]) para una cuenta en un
 * instante dado.
 */
sealed interface EstadoBloqueoCuenta {
    data object Habilitado : EstadoBloqueoCuenta

    data class Bloqueado(
        val minutosRestantes: Int,
    ) : EstadoBloqueoCuenta
}
