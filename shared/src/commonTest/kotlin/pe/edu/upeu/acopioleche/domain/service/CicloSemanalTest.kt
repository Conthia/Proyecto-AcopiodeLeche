package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class CicloSemanalTest {

    @Test
    fun `un jueves es el inicio de su propia semana`() {
        val jueves = LocalDate(2026, 9, 3)

        assertEquals(expected = jueves, actual = CicloSemanal.inicioDeSemana(jueves))
    }

    @Test
    fun `un miercoles pertenece a la semana que empezo el jueves anterior`() {
        val miercoles = LocalDate(2026, 9, 9)
        val juevesAnterior = LocalDate(2026, 9, 3)

        assertEquals(expected = juevesAnterior, actual = CicloSemanal.inicioDeSemana(miercoles))
    }

    @Test
    fun `un lunes pertenece a la semana que empezo el jueves de la semana calendario anterior`() {
        val lunes = LocalDate(2026, 9, 7)
        val juevesAnterior = LocalDate(2026, 9, 3)

        assertEquals(expected = juevesAnterior, actual = CicloSemanal.inicioDeSemana(lunes))
    }

    @Test
    fun `un viernes pertenece a la semana que empezo el dia anterior`() {
        val viernes = LocalDate(2026, 9, 4)
        val jueves = LocalDate(2026, 9, 3)

        assertEquals(expected = jueves, actual = CicloSemanal.inicioDeSemana(viernes))
    }

    @Test
    fun `fechaDePago es el viernes de la semana siguiente al cierre, 8 dias despues del jueves de inicio`() {
        val jueves = LocalDate(2026, 9, 3)
        val viernesDePago = LocalDate(2026, 9, 11)

        assertEquals(expected = viernesDePago, actual = CicloSemanal.fechaDePago(jueves))
    }
}
