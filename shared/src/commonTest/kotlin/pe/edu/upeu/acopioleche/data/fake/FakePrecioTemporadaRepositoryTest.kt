package pe.edu.upeu.acopioleche.data.fake

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion

class FakePrecioTemporadaRepositoryTest {

    @Test
    fun `obtenerPrecioVigenteEn devuelve el precio de la temporada que cubre la fecha`() = runBlocking {
        val repo = FakePrecioTemporadaRepository()

        // 15 de marzo de 2026 cae dentro de PT-2026-01 (Ene-Jun, S/1.60).
        val resultado = repo.obtenerPrecioVigenteEn(LocalDate(2026, 3, 15))

        assertEquals(expected = 1.60, actual = resultado.precioPorLitro)
        assertFalse(resultado.esRespaldo)
    }

    @Test
    fun `obtenerPrecioVigenteEn devuelve el respaldo cuando ninguna temporada cubre la fecha`() = runBlocking {
        val repo = FakePrecioTemporadaRepository()

        // 1 de enero de 2027: fuera de las dos temporadas sembradas (ambas de 2026).
        val resultado = repo.obtenerPrecioVigenteEn(LocalDate(2027, 1, 1))

        assertEquals(expected = CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO, actual = resultado.precioPorLitro)
        assertTrue(resultado.esRespaldo)
    }

    @Test
    fun `si dos temporadas se traslapan, gana la de fechaInicio mas reciente`() = runBlocking {
        val repo = FakePrecioTemporadaRepository()
        repo.guardar(
            PrecioTemporada(
                id = "PT-AJUSTE",
                nombreTemporada = "Ajuste temporal",
                fechaInicio = LocalDate(2026, 5, 1),
                fechaFin = LocalDate(2026, 5, 31),
                precioPorLitro = 2.50,
            ),
        )

        // 15 de mayo de 2026 cae tanto en PT-2026-01 (Ene-Jun) como en PT-AJUSTE (Mayo).
        val resultado = repo.obtenerPrecioVigenteEn(LocalDate(2026, 5, 15))

        assertEquals(expected = 2.50, actual = resultado.precioPorLitro)
        assertFalse(resultado.esRespaldo)
    }
}
