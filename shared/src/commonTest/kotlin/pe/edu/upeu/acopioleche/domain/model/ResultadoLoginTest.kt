package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResultadoLoginTest {

    private val sesion = SesionActiva(
        usuarioId = "A-01",
        nombreCompleto = "Juan Mamani",
        rol = RolUsuario.ACOPIADOR,
        centroAcopioId = "CA-002",
    )

    @Test
    fun `Exitoso conserva la sesion iniciada`() {
        val resultado: ResultadoLogin = ResultadoLogin.Exitoso(sesion = sesion)

        assertIs<ResultadoLogin.Exitoso>(resultado)
        assertEquals(expected = sesion, actual = resultado.sesion)
    }

    @Test
    fun `CredencialesInvalidas es un objeto unico sin datos asociados`() {
        val resultado: ResultadoLogin = ResultadoLogin.CredencialesInvalidas

        assertIs<ResultadoLogin.CredencialesInvalidas>(resultado)
    }

    @Test
    fun `CuentaBloqueada conserva los minutos restantes`() {
        val resultado: ResultadoLogin = ResultadoLogin.CuentaBloqueada(minutosRestantes = 10)

        assertIs<ResultadoLogin.CuentaBloqueada>(resultado)
        assertEquals(expected = 10, actual = resultado.minutosRestantes)
    }

    @Test
    fun `describirResultado resuelve exhaustivamente las tres variantes`() {
        assertEquals(expected = "ok", actual = describirResultado(resultado = ResultadoLogin.Exitoso(sesion = sesion)))
        assertEquals(expected = "credenciales invalidas", actual = describirResultado(resultado = ResultadoLogin.CredencialesInvalidas))
        assertEquals(
            expected = "bloqueada por 7 min",
            actual = describirResultado(resultado = ResultadoLogin.CuentaBloqueada(minutosRestantes = 7)),
        )
    }
}

private fun describirResultado(resultado: ResultadoLogin): String =
    when (resultado) {
        is ResultadoLogin.Exitoso -> "ok"
        is ResultadoLogin.CredencialesInvalidas -> "credenciales invalidas"
        is ResultadoLogin.CuentaBloqueada -> "bloqueada por ${resultado.minutosRestantes} min"
    }
