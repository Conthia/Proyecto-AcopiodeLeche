package pe.edu.upeu.acopioleche.domain.service

import kotlin.time.Instant
import pe.edu.upeu.acopioleche.domain.model.EstadoBloqueoCuenta

/**
 * Regla de bloqueo temporal de RF-01: 3 intentos fallidos consecutivos bloquean la cuenta
 * exactamente 10 minutos (el encargo lo pide así, no 30 como otras políticas típicas).
 */
object PoliticaBloqueoLogin {
    const val MAX_INTENTOS_FALLIDOS: Int = 3
    const val MINUTOS_BLOQUEO: Int = 10

    fun evaluar(intentosFallidos: Int, ultimoIntentoFallidoEn: Instant?, ahora: Instant): EstadoBloqueoCuenta {
        if (intentosFallidos < MAX_INTENTOS_FALLIDOS || ultimoIntentoFallidoEn == null) {
            return EstadoBloqueoCuenta.Habilitado
        }
        val minutosTranscurridos = (ahora - ultimoIntentoFallidoEn).inWholeMinutes
        val minutosRestantes = MINUTOS_BLOQUEO - minutosTranscurridos
        return if (minutosRestantes > 0) {
            EstadoBloqueoCuenta.Bloqueado(minutosRestantes = minutosRestantes.toInt())
        } else {
            EstadoBloqueoCuenta.Habilitado
        }
    }
}
