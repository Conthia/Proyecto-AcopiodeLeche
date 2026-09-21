package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import pe.edu.upeu.acopioleche.domain.model.LecturaLactoescan
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis

class EvaluadorCalidadTest {

    private val reglas = ReglasNegocio()

    private val lecturaDentroDeRango = LecturaLactoescan(
        densidad = 1.031,
        grasa = 3.6,
        proteina = 3.2,
        lactosa = 4.6,
        temperatura = 4.0,
        ph = 6.7,
        porcentajeAguaAnadida = 0.0,
    )

    @Test
    fun `todos los parametros dentro de rango y sin agua anadida da un resultado Normal`() {
        val resultado = EvaluadorCalidad.evaluar(lecturaDentroDeRango, reglas)

        val normal = assertIs<ResultadoAnalisis.Normal>(resultado)
        assertEquals(expected = 0.0, actual = normal.acidez)
        assertEquals(expected = lecturaDentroDeRango.grasa, actual = normal.grasa)
        assertEquals(expected = lecturaDentroDeRango.ph, actual = normal.ph)
    }

    @Test
    fun `densidad fuera de rango da FueraDeRango con el motivo correcto`() {
        val resultado = EvaluadorCalidad.evaluar(lecturaDentroDeRango.copy(densidad = 1.020), reglas)

        val fueraDeRango = assertIs<ResultadoAnalisis.FueraDeRango>(resultado)
        assertEquals(expected = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO, actual = fueraDeRango.motivo)
        assertEquals(expected = 1.020, actual = fueraDeRango.valorMedido)
    }

    @Test
    fun `agua anadida mayor a 0 es adulteracion incluso si tambien hay un parametro fuera de rango`() {
        val resultado = EvaluadorCalidad.evaluar(
            lecturaDentroDeRango.copy(densidad = 1.020, porcentajeAguaAnadida = 6.5),
            reglas,
        )

        val adulterada = assertIs<ResultadoAnalisis.Adulterada>(resultado)
        assertEquals(expected = 6.5, actual = adulterada.porcentajeAgua)
    }

    @Test
    fun `con varios parametros fuera de rango se reporta el primero segun el orden definido`() {
        val resultado = EvaluadorCalidad.evaluar(lecturaDentroDeRango.copy(grasa = 0.5, ph = 3.0), reglas)

        val fueraDeRango = assertIs<ResultadoAnalisis.FueraDeRango>(resultado)
        assertEquals(expected = MotivoRechazo.GRASA_FUERA_DE_RANGO, actual = fueraDeRango.motivo)
    }

    @Test
    fun `sin agua anadida y sin parametros fuera de rango la acidez del resultado siempre es 0`() {
        val resultado = EvaluadorCalidad.evaluar(lecturaDentroDeRango, reglas)

        val normal = assertIs<ResultadoAnalisis.Normal>(resultado)
        assertEquals(expected = 0.0, actual = normal.acidez)
    }
}
