package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import pe.edu.upeu.acopioleche.domain.model.EstadoBloqueoCuenta

class PoliticaBloqueoLoginTest {

    private val ahora = Instant.parse("2026-09-06T12:00:00Z")

    @Test
    fun `con menos de 3 intentos fallidos la cuenta esta habilitada`() {
        val estado = PoliticaBloqueoLogin.evaluar(intentosFallidos = 2, ultimoIntentoFallidoEn = ahora, ahora = ahora)

        assertIs<EstadoBloqueoCuenta.Habilitado>(estado)
    }

    @Test
    fun `al tercer intento fallido la cuenta queda bloqueada 10 minutos`() {
        val estado = PoliticaBloqueoLogin.evaluar(intentosFallidos = 3, ultimoIntentoFallidoEn = ahora, ahora = ahora)

        assertIs<EstadoBloqueoCuenta.Bloqueado>(estado)
        assertEquals(expected = 10, actual = estado.minutosRestantes)
    }

    @Test
    fun `a los 5 minutos de bloqueo quedan 5 minutos restantes`() {
        val estado = PoliticaBloqueoLogin.evaluar(
            intentosFallidos = 3,
            ultimoIntentoFallidoEn = ahora,
            ahora = ahora + 5.minutes,
        )

        assertIs<EstadoBloqueoCuenta.Bloqueado>(estado)
        assertEquals(expected = 5, actual = estado.minutosRestantes)
    }

    @Test
    fun `a los 10 minutos exactos la cuenta vuelve a estar habilitada`() {
        val estado = PoliticaBloqueoLogin.evaluar(
            intentosFallidos = 3,
            ultimoIntentoFallidoEn = ahora,
            ahora = ahora + 10.minutes,
        )

        assertIs<EstadoBloqueoCuenta.Habilitado>(estado)
    }

    @Test
    fun `sin intentos previos la cuenta esta habilitada`() {
        val estado = PoliticaBloqueoLogin.evaluar(intentosFallidos = 0, ultimoIntentoFallidoEn = null, ahora = ahora)

        assertIs<EstadoBloqueoCuenta.Habilitado>(estado)
    }
}
