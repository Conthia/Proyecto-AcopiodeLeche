package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

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

    @Test
    fun `indiceEnCiclo de un jueves es 0`() {
        val jueves = LocalDate(2026, 9, 3)

        assertEquals(expected = 0, actual = CicloSemanal.indiceEnCiclo(jueves))
    }

    @Test
    fun `indiceEnCiclo de un miercoles es 6`() {
        val miercoles = LocalDate(2026, 9, 9)

        assertEquals(expected = 6, actual = CicloSemanal.indiceEnCiclo(miercoles))
    }

    @Test
    fun `inicioDeSemana mas indiceEnCiclo reconstruye la fecha original, para las 7 fechas de un ciclo`() {
        val juevesDeInicio = LocalDate(2026, 9, 3)

        for (offset in 0..6) {
            val fecha = juevesDeInicio.plus(offset, DateTimeUnit.DAY)
            val reconstruida = CicloSemanal.inicioDeSemana(fecha).plus(CicloSemanal.indiceEnCiclo(fecha), DateTimeUnit.DAY)
            assertEquals(expected = fecha, actual = reconstruida, message = "offset $offset")
        }
    }
}
