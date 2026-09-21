package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EstadoConexionTest {

    @Test
    fun `AlDia es un objeto unico sin datos asociados`() {
        val estado: EstadoConexion = EstadoConexion.AlDia

        assertIs<EstadoConexion.AlDia>(estado)
    }

    @Test
    fun `Pendiente conserva los dias sin sincronizar`() {
        val estado: EstadoConexion = EstadoConexion.Pendiente(diasSinSincronizar = 2)

        assertIs<EstadoConexion.Pendiente>(estado)
        assertEquals(expected = 2, actual = estado.diasSinSincronizar)
    }

    @Test
    fun `SinConexion conserva los dias desconectado`() {
        val estado: EstadoConexion = EstadoConexion.SinConexion(dias = 5)

        assertIs<EstadoConexion.SinConexion>(estado)
        assertEquals(expected = 5, actual = estado.dias)
    }

    @Test
    fun `describirEstado resuelve exhaustivamente las tres variantes`() {
        assertEquals(expected = "al día", actual = describirEstado(estado = EstadoConexion.AlDia))
        assertEquals(
            expected = "pendiente hace 3 día(s)",
            actual = describirEstado(estado = EstadoConexion.Pendiente(diasSinSincronizar = 3)),
        )
        assertEquals(
            expected = "sin conexión hace 7 día(s)",
            actual = describirEstado(estado = EstadoConexion.SinConexion(dias = 7)),
        )
    }
}

private fun describirEstado(estado: EstadoConexion): String =
    when (estado) {
        is EstadoConexion.AlDia -> "al día"
        is EstadoConexion.Pendiente -> "pendiente hace ${estado.diasSinSincronizar} día(s)"
        is EstadoConexion.SinConexion -> "sin conexión hace ${estado.dias} día(s)"
    }
