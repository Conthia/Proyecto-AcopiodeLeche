package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TurnoTest {

    @Test
    fun `deduce turno manana a medianoche`() {
        val turno = Turno.deducirDeHora(0)
        assertEquals(Turno.MANANA, turno)
    }

    @Test
    fun `deduce turno manana a las 11 AM`() {
        val turno = Turno.deducirDeHora(11)
        assertEquals(Turno.MANANA, turno)
    }

    @Test
    fun `deduce turno tarde a mediodia 12 PM`() {
        val turno = Turno.deducirDeHora(12)
        assertEquals(Turno.TARDE, turno)
    }

    @Test
    fun `deduce turno tarde en la noche`() {
        val turno = Turno.deducirDeHora(20)
        assertEquals(Turno.TARDE, turno)
    }
}
