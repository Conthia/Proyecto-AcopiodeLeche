package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class CalculadoraLiquidacionTest {

    private val jueves = LocalDate(2026, 9, 3)

    @Test
    fun `el monto bruto es litros aceptados por el precio de referencia S 1_70`() {
        val liquidacion = CalculadoraLiquidacion.calcular(
            id = "LIQ-1",
            proveedorId = "P-014",
            semanaInicio = jueves,
            litrosAceptados = 100.0,
            tieneSancionReduccionPendiente = false,
            generadaAutomaticamente = false,
        )

        assertEquals(expected = 170.0, actual = liquidacion.montoBruto)
    }

    @Test
    fun `sin sancion pendiente el monto final es igual al bruto`() {
        val sinSancion = CalculadoraLiquidacion.calcular(
            id = "LIQ-2",
            proveedorId = "P-014",
            semanaInicio = jueves,
            litrosAceptados = 100.0,
            tieneSancionReduccionPendiente = false,
            generadaAutomaticamente = false,
        )

        assertEquals(expected = sinSancion.montoBruto, actual = sinSancion.montoFinal)
    }

    @Test
    fun `RN-10 con sancion pendiente el monto final descuenta el porcentaje propuesto (TODO, no confirmado)`() {
        val conSancion = CalculadoraLiquidacion.calcular(
            id = "LIQ-2b",
            proveedorId = "P-014",
            semanaInicio = jueves,
            litrosAceptados = 100.0,
            tieneSancionReduccionPendiente = true,
            generadaAutomaticamente = false,
        )

        val esperado = conSancion.montoBruto * (1 - CalculadoraLiquidacion.PORCENTAJE_REDUCCION_POR_ADULTERACION_LEVE)
        assertEquals(expected = esperado, actual = conSancion.montoFinal)
    }

    @Test
    fun `tieneSancionPendienteDeMonto se propaga tal cual se recibe`() {
        val conSancion = CalculadoraLiquidacion.calcular(
            id = "LIQ-3",
            proveedorId = "P-014",
            semanaInicio = jueves,
            litrosAceptados = 50.0,
            tieneSancionReduccionPendiente = true,
            generadaAutomaticamente = false,
        )
        val sinSancion = conSancion.copy(id = "LIQ-4", tieneSancionPendienteDeMonto = false)

        assertEquals(expected = true, actual = conSancion.tieneSancionPendienteDeMonto)
        assertEquals(expected = false, actual = sinSancion.tieneSancionPendienteDeMonto)
    }

    @Test
    fun `la fecha de pago es el viernes siguiente al cierre de la semana`() {
        val liquidacion = CalculadoraLiquidacion.calcular(
            id = "LIQ-5",
            proveedorId = "P-014",
            semanaInicio = jueves,
            litrosAceptados = 10.0,
            tieneSancionReduccionPendiente = false,
            generadaAutomaticamente = false,
        )

        assertEquals(expected = LocalDate(2026, 9, 11), actual = liquidacion.fechaPago)
    }
}
