package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CentroAcopioTest {

    @Test
    fun `capacidadLitrosDia y capacidadTanqueLitros son campos independientes`() {
        val centro = CentroAcopio(
            id = "CA-001",
            nombre = "Huata Centro",
            ubicacion = "Plaza principal",
            capacidadLitrosDia = 412.0,
            activo = true,
            capacidadTanqueLitros = 600.0,
        )

        assertNotEquals(illegal = centro.capacidadLitrosDia, actual = centro.capacidadTanqueLitros)
        assertEquals(expected = 412.0, actual = centro.capacidadLitrosDia)
        assertEquals(expected = 600.0, actual = centro.capacidadTanqueLitros)
    }

    @Test
    fun `un centro inactivo conserva sus capacidades`() {
        val centro = CentroAcopio(
            id = "CA-003",
            nombre = "Pallalla",
            ubicacion = "Módulo asociativo",
            capacidadLitrosDia = 196.0,
            activo = false,
            capacidadTanqueLitros = 400.0,
        )

        assertEquals(expected = false, actual = centro.activo)
        assertEquals(expected = 400.0, actual = centro.capacidadTanqueLitros)
    }

    @Test
    fun `dos centros con distinta capacidad de tanque no son iguales`() {
        val base = CentroAcopio(
            id = "CA-002",
            nombre = "Coyme",
            ubicacion = "Local comunal",
            capacidadLitrosDia = 268.0,
            activo = true,
            capacidadTanqueLitros = 300.0,
        )
        val otroTanque = base.copy(capacidadTanqueLitros = 500.0)

        assertNotEquals(illegal = base, actual = otroTanque)
    }

    @Test
    fun `cambiar la capacidad diaria no afecta la capacidad de tanque`() {
        val centro = CentroAcopio(
            id = "CA-001",
            nombre = "Huata Centro",
            ubicacion = "Plaza principal",
            capacidadLitrosDia = 412.0,
            activo = true,
            capacidadTanqueLitros = 600.0,
        )
        val conMasThroughput = centro.copy(capacidadLitrosDia = 500.0)

        assertEquals(expected = 600.0, actual = conMasThroughput.capacidadTanqueLitros)
    }
}
