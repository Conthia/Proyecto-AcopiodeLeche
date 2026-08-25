package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ProveedorAcopiadorTest {

    @Test
    fun `proveedor sin documento lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Proveedor(
                id = "p-1",
                nombre = "Juan Perez",
                documento = "",
                telefono = "987654321",
                sector = "Huata",
                entregaDirectaEnPlanta = false,
            )
        }
    }

    @Test
    fun `proveedor con datos validos se crea correctamente`() {
        val proveedor = Proveedor(
            id = "p-1",
            nombre = "Juan Perez",
            documento = "12345678",
            telefono = "987654321",
            sector = "Huata",
            entregaDirectaEnPlanta = false,
        )

        assertTrue(proveedor.documento.isNotBlank())
    }

    @Test
    fun `acopiador sin sectores asignados lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Acopiador(
                id = "a-1",
                nombre = "Maria Lopez",
                vehiculo = "Moto",
                sectoresAsignados = emptyList(),
            )
        }
    }

    @Test
    fun `acopiador con sectores asignados se crea correctamente`() {
        val acopiador = Acopiador(
            id = "a-1",
            nombre = "Maria Lopez",
            vehiculo = "Moto",
            sectoresAsignados = listOf("Huata", "Chucuito"),
        )

        assertTrue(acopiador.sectoresAsignados.isNotEmpty())
    }
}
