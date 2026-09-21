package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CalificacionProveedorTest {

    @Test
    fun `tiene exactamente tres niveles A B y C`() {
        assertEquals(
            expected = listOf(CalificacionProveedor.A, CalificacionProveedor.B, CalificacionProveedor.C),
            actual = CalificacionProveedor.entries,
        )
    }

    @Test
    fun `A y C son calificaciones distintas`() {
        assertNotEquals(illegal = CalificacionProveedor.A, actual = CalificacionProveedor.C)
    }

    @Test
    fun `se puede asociar una calificacion a un proveedor de ejemplo`() {
        val proveedor = proveedorDePrueba(calificacion = CalificacionProveedor.B)

        assertEquals(expected = CalificacionProveedor.B, actual = proveedor.calificacion)
    }

    @Test
    fun `dos proveedores con distinta calificacion no son iguales`() {
        val proveedorA = proveedorDePrueba(calificacion = CalificacionProveedor.A)
        val proveedorC = proveedorDePrueba(calificacion = CalificacionProveedor.C)

        assertTrue(proveedorA != proveedorC)
    }
}

private fun proveedorDePrueba(calificacion: CalificacionProveedor): Proveedor =
    Proveedor(
        id = "P-001",
        nombre = "Rosa Quispe Mamani",
        documento = "12345678",
        telefono = "987654321",
        sector = Sector.NORTE,
        entregaDirectaEnPlanta = false,
        numeroVacas = 9,
        calificacion = calificacion,
    )
