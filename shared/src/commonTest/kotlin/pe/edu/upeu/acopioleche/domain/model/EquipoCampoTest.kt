package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class EquipoCampoTest {

    @Test
    fun `conserva sus datos basicos`() {
        val equipo = equipoDePrueba(estadoConexion = EstadoConexion.AlDia)

        assertEquals(expected = "EQ-001", actual = equipo.id)
        assertEquals(expected = "Tablet acopio Huata Centro", actual = equipo.nombre)
        assertEquals(expected = "Tablet", actual = equipo.tipo)
        assertEquals(expected = "CA-001", actual = equipo.centroAcopioId)
    }

    @Test
    fun `puede estar al dia`() {
        val equipo = equipoDePrueba(estadoConexion = EstadoConexion.AlDia)

        assertIs<EstadoConexion.AlDia>(equipo.estadoConexion)
    }

    @Test
    fun `puede estar pendiente de sincronizar con dias acumulados`() {
        val equipo = equipoDePrueba(estadoConexion = EstadoConexion.Pendiente(diasSinSincronizar = 1))

        val estado = assertIs<EstadoConexion.Pendiente>(equipo.estadoConexion)
        assertEquals(expected = 1, actual = estado.diasSinSincronizar)
    }

    @Test
    fun `puede estar sin conexion con dias acumulados`() {
        val equipo = equipoDePrueba(estadoConexion = EstadoConexion.SinConexion(dias = 2))

        val estado = assertIs<EstadoConexion.SinConexion>(equipo.estadoConexion)
        assertEquals(expected = 2, actual = estado.dias)
    }

    @Test
    fun `dos equipos con distinto estado de conexion no son iguales`() {
        val alDia = equipoDePrueba(estadoConexion = EstadoConexion.AlDia)
        val sinConexion = equipoDePrueba(estadoConexion = EstadoConexion.SinConexion(dias = 2))

        assertNotEquals(illegal = alDia, actual = sinConexion)
    }
}

private fun equipoDePrueba(estadoConexion: EstadoConexion): EquipoCampo =
    EquipoCampo(
        id = "EQ-001",
        nombre = "Tablet acopio Huata Centro",
        tipo = "Tablet",
        centroAcopioId = "CA-001",
        estadoConexion = estadoConexion,
    )
