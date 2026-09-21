package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EntregaTest {

    @Test
    fun `conserva el volumen y el turno registrados`() {
        val entrega = entregaDePrueba(volumenLitros = 24.0, turno = Turno.MANANA)

        assertEquals(expected = 24.0, actual = entrega.volumenLitros)
        assertEquals(expected = Turno.MANANA, actual = entrega.turno)
    }

    @Test
    fun `puede no tener acopiador cuando la entrega es directa en planta`() {
        val entrega = entregaDePrueba(volumenLitros = 10.0, turno = Turno.TARDE).copy(acopiadorId = null)

        assertEquals(expected = null, actual = entrega.acopiadorId)
    }

    @Test
    fun `inicia en estado Pendiente hasta que se procese`() {
        val entrega = entregaDePrueba(volumenLitros = 15.0, turno = Turno.MANANA)

        assertIs<EstadoEntrega.Pendiente>(entrega.estado)
    }

    @Test
    fun `conserva la cantidad de porongos entregados`() {
        val entrega = entregaDePrueba(volumenLitros = 24.0, turno = Turno.MANANA).copy(cantidadPorongos = 2)

        assertEquals(expected = 2, actual = entrega.cantidadPorongos)
    }

    @Test
    fun `sin volumen de planta registrado la diferencia es nula`() {
        val entrega = entregaDePrueba(volumenLitros = 34.0, turno = Turno.MANANA)

        assertEquals(expected = null, actual = entrega.volumenPlantaLitros)
        assertEquals(expected = null, actual = entrega.diferenciaLitros)
    }

    @Test
    fun `la diferencia es negativa cuando se pierden litros entre el campo y la planta`() {
        val entrega = entregaDePrueba(volumenLitros = 34.0, turno = Turno.MANANA).copy(volumenPlantaLitros = 32.0)

        assertEquals(expected = -2.0, actual = entrega.diferenciaLitros)
    }

    @Test
    fun `la diferencia es cero cuando el volumen de campo y de planta coinciden`() {
        val entrega = entregaDePrueba(volumenLitros = 34.0, turno = Turno.MANANA).copy(volumenPlantaLitros = 34.0)

        assertEquals(expected = 0.0, actual = entrega.diferenciaLitros)
    }
}

private fun entregaDePrueba(volumenLitros: Double, turno: Turno): Entrega =
    Entrega(
        id = "E-1042",
        proveedorId = "P-027",
        acopiadorId = "A-01",
        centroAcopioId = "CA-001",
        fecha = LocalDate(2026, 9, 5),
        turno = turno,
        volumenLitros = volumenLitros,
        estado = EstadoEntrega.Pendiente,
        cantidadPorongos = 1,
    )
