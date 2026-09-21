package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Fija por defecto los valores que ya corrían como `const val` dispersos antes de este refactor
 * — si alguien cambia un default sin querer, este test lo marca. Los campos NO DEFINIDA deben
 * seguir siendo `null` por defecto: si algún día alguien les pone un número "porque sí", este
 * test también lo marca.
 */
class ReglasNegocioTest {

    @Test
    fun `los valores CONFIRMADOS y SUPUESTOS por defecto coinciden con los que ya corrian en produccion`() {
        val reglas = ReglasNegocio()

        assertEquals(expected = 5.0, actual = reglas.umbralAdulteracionGravePorcentaje)
        assertEquals(expected = 5000.0, actual = reglas.multaSegundaAdulteracion)
        assertEquals(expected = 0.15, actual = reglas.porcentajeReduccionAdulteracionLeveProvisional)
        assertEquals(expected = 3, actual = reglas.maxIntentosFallidos)
        assertEquals(expected = 10, actual = reglas.minutosBloqueo)
        assertEquals(expected = 1.70, actual = reglas.precioReferenciaPorLitro)
        assertEquals(expected = 4, actual = reglas.plazoMaximoTrasladoHoras)
    }

    @Test
    fun `los rangos de calidad por defecto coinciden con los que ya corrian en produccion`() {
        val rangos = ReglasNegocio().rangosCalidad

        assertEquals(expected = 1.028..1.034, actual = rangos.densidad)
        assertEquals(expected = 3.0..6.0, actual = rangos.grasa)
        assertEquals(expected = 2.9..3.8, actual = rangos.proteina)
        assertEquals(expected = 4.0..5.0, actual = rangos.lactosa)
        assertEquals(expected = 0.0..10.0, actual = rangos.temperatura)
        assertEquals(expected = 6.6..6.8, actual = rangos.ph)
    }

    @Test
    fun `bonificacionPorGrasaPorLitro es null por defecto (NO DEFINIDA, RN-14)`() {
        assertNull(ReglasNegocio().bonificacionPorGrasaPorLitro)
    }
}
