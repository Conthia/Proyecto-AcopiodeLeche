package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ResultadoAnalisisTest {

    @Test
    fun `Normal conserva densidad acidez grasa y los 4 parametros del lactoescan`() {
        val resultado: ResultadoAnalisis = ResultadoAnalisis.Normal(
            densidad = 1.030,
            acidez = 0.0,
            grasa = 3.5,
            proteina = 3.2,
            lactosa = 4.6,
            temperatura = 4.0,
            ph = 6.7,
        )

        val normal = assertIs<ResultadoAnalisis.Normal>(resultado)
        assertEquals(expected = 1.030, actual = normal.densidad)
        assertEquals(expected = 0.0, actual = normal.acidez)
        assertEquals(expected = 3.5, actual = normal.grasa)
        assertEquals(expected = 3.2, actual = normal.proteina)
        assertEquals(expected = 4.6, actual = normal.lactosa)
        assertEquals(expected = 4.0, actual = normal.temperatura)
        assertEquals(expected = 6.7, actual = normal.ph)
    }

    @Test
    fun `FueraDeRango conserva el motivo y el rango permitido`() {
        val resultado: ResultadoAnalisis = ResultadoAnalisis.FueraDeRango(
            motivo = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO,
            valorMedido = 1.020,
            rangoPermitido = 1.028..1.034,
        )

        val fueraDeRango = assertIs<ResultadoAnalisis.FueraDeRango>(resultado)
        assertEquals(expected = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO, actual = fueraDeRango.motivo)
        assertEquals(expected = false, actual = fueraDeRango.valorMedido in fueraDeRango.rangoPermitido)
    }

    @Test
    fun `Adulterada conserva el indicio y el porcentaje de agua detectados`() {
        val resultado: ResultadoAnalisis = ResultadoAnalisis.Adulterada(indicio = "Agua añadida 6.5 %", porcentajeAgua = 6.5)

        val adulterada = assertIs<ResultadoAnalisis.Adulterada>(resultado)
        assertEquals(expected = "Agua añadida 6.5 %", actual = adulterada.indicio)
        assertEquals(expected = 6.5, actual = adulterada.porcentajeAgua)
    }

    @Test
    fun `clasificar resuelve exhaustivamente las tres variantes`() {
        assertEquals(
            expected = "normal",
            actual = clasificar(
                resultado = ResultadoAnalisis.Normal(
                    densidad = 1.030,
                    acidez = 0.0,
                    grasa = 3.5,
                    proteina = 3.2,
                    lactosa = 4.6,
                    temperatura = 4.0,
                    ph = 6.7,
                ),
            ),
        )
        assertEquals(
            expected = "fuera de rango",
            actual = clasificar(
                resultado = ResultadoAnalisis.FueraDeRango(
                    motivo = MotivoRechazo.ACIDEZ_FUERA_DE_RANGO,
                    valorMedido = 0.3,
                    rangoPermitido = 0.13..0.17,
                ),
            ),
        )
        assertEquals(
            expected = "adulterada",
            actual = clasificar(resultado = ResultadoAnalisis.Adulterada(indicio = "prueba de alcohol positiva", porcentajeAgua = 3.0)),
        )
    }
}

private fun clasificar(resultado: ResultadoAnalisis): String =
    when (resultado) {
        is ResultadoAnalisis.Normal -> "normal"
        is ResultadoAnalisis.FueraDeRango -> "fuera de rango"
        is ResultadoAnalisis.Adulterada -> "adulterada"
    }
