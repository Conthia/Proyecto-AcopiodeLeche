package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion

class MotorSancionesTest {

    @Test
    fun `RN-10 adulteracion menor a 5 por ciento primera vez reduce el precio de la semana`() {
        val resultado = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 3.0,
            numeroAdulteracionesPrevias = 0,
        )

        assertIs<ResultadoSancion.ReducirPrecioSemanal>(resultado)
        assertEquals(expected = "P-008", actual = resultado.proveedorId)
    }

    @Test
    fun `RN-10 exactamente 5 por ciento primera vez cuenta como leve, no como grave`() {
        val resultado = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 5.0,
            numeroAdulteracionesPrevias = 0,
        )

        assertIs<ResultadoSancion.ReducirPrecioSemanal>(resultado)
    }

    @Test
    fun `RN-12 adulteracion mayor a 5 por ciento primera vez retira de inmediato sin multa`() {
        val resultado = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 5.1,
            numeroAdulteracionesPrevias = 0,
        )

        assertIs<ResultadoSancion.RetirarInmediato>(resultado)
    }

    @Test
    fun `RN-11 segunda deteccion retira y multa sin importar el porcentaje`() {
        val resultadoMenor = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 1.0,
            numeroAdulteracionesPrevias = 1,
        )
        val resultadoMayor = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 9.0,
            numeroAdulteracionesPrevias = 1,
        )

        val retiroMenor = assertIs<ResultadoSancion.RetirarYMultar>(resultadoMenor)
        val retiroMayor = assertIs<ResultadoSancion.RetirarYMultar>(resultadoMayor)
        assertEquals(expected = MotorSanciones.MULTA_SEGUNDA_ADULTERACION, actual = retiroMenor.montoMulta)
        assertEquals(expected = MotorSanciones.MULTA_SEGUNDA_ADULTERACION, actual = retiroMayor.montoMulta)
    }

    @Test
    fun `RN-11 tiene prioridad sobre RN-12 incluso con porcentaje alto en la segunda vez`() {
        val resultado = MotorSanciones.evaluarAdulteracion(
            proveedorId = "P-008",
            porcentajeAgua = 20.0,
            numeroAdulteracionesPrevias = 3,
        )

        assertIs<ResultadoSancion.RetirarYMultar>(resultado)
    }

    @Test
    fun `la multa de la segunda vez es exactamente S 5000`() {
        assertEquals(expected = 5000.0, actual = MotorSanciones.MULTA_SEGUNDA_ADULTERACION)
    }
}
