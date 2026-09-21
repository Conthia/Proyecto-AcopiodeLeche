package pe.edu.upeu.acopioleche.domain.service

import kotlin.time.Instant
import pe.edu.upeu.acopioleche.domain.model.EstadoBloqueoCuenta

/**
 * Regla de bloqueo temporal de RF-01: 3 intentos fallidos consecutivos bloquean la cuenta
 * exactamente 10 minutos (el encargo lo pide así, no 30 como otras políticas típicas).
 */
object PoliticaBloqueoLogin {

    fun evaluar(intentosFallidos: Int, ultimoIntentoFallidoEn: Instant?, ahora: Instant, reglas: ReglasNegocio): EstadoBloqueoCuenta {
        if (intentosFallidos < reglas.maxIntentosFallidos || ultimoIntentoFallidoEn == null) {
            return EstadoBloqueoCuenta.Habilitado
        }
        val minutosTranscurridos = (ahora - ultimoIntentoFallidoEn).inWholeMinutes
        val minutosRestantes = reglas.minutosBloqueo - minutosTranscurridos
        return if (minutosRestantes > 0) {
            EstadoBloqueoCuenta.Bloqueado(minutosRestantes = minutosRestantes.toInt())
        } else {
            EstadoBloqueoCuenta.Habilitado
        }
    }
}
