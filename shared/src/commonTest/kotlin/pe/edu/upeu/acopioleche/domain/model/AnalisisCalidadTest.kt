package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AnalisisCalidadTest {

    @Test
    fun `analisis calidad con resultado adulterada expone el indicio`() {
        val analisis = AnalisisCalidad(
            id = "an-1",
            entregaId = "e-1",
            tecnicoId = "tec-1",
            fecha = "2026-08-24",
            resultado = ResultadoAnalisis.Adulterada(indicio = "presencia de agua"),
        )

        val resultado = analisis.resultado
        check(resultado is ResultadoAnalisis.Adulterada)
        assertEquals(expected = "presencia de agua", actual = resultado.indicio)
    }

    @Test
    fun `analisis calidad sin entrega asociada lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            AnalisisCalidad(
                id = "an-1",
                entregaId = "",
                tecnicoId = "tec-1",
                fecha = "2026-08-24",
                resultado = ResultadoAnalisis.Normal(densidad = 1.030, acidez = 15.0, grasa = 3.5),
            )
        }
    }

    @Test
    fun `analisis calidad sin tecnico lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            AnalisisCalidad(
                id = "an-1",
                entregaId = "e-1",
                tecnicoId = "",
                fecha = "2026-08-24",
                resultado = ResultadoAnalisis.Normal(densidad = 1.030, acidez = 15.0, grasa = 3.5),
            )
        }
    }

    @Test
    fun `analisis calidad con resultado fuera de rango conserva el motivo`() {
        val analisis = AnalisisCalidad(
            id = "an-1",
            entregaId = "e-1",
            tecnicoId = "tec-1",
            fecha = "2026-08-24",
            resultado = ResultadoAnalisis.FueraDeRango(
                motivo = MotivoRechazo.GRASA_FUERA_DE_RANGO,
                valorMedido = 1.5,
                rangoPermitido = 3.0..4.5,
            ),
        )

        val resultado = analisis.resultado
        check(resultado is ResultadoAnalisis.FueraDeRango)
        assertEquals(expected = MotivoRechazo.GRASA_FUERA_DE_RANGO, actual = resultado.motivo)
    }
}
