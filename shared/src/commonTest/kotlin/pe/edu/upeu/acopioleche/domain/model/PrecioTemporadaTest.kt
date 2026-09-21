package pe.edu.upeu.acopioleche.domain.model

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upeu.acopioleche.data.fake.FakePagoRepository
import pe.edu.upeu.acopioleche.data.fake.FakePrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Clock

class PrecioTemporadaTest {

    @Test
    fun obtenerPrecioVigenteEnRetornaTarifaSegunFechaDeEntrega() {
        runBlocking {
            val repository = FakePrecioTemporadaRepository()
            val fechaLluvias = LocalDate(2026, 3, 15) // Caer en Alta produccion
            val fechaEstiaje = LocalDate(2026, 8, 20) // Caer en Baja produccion

            val precioLluvias = repository.obtenerPrecioVigenteEn(fechaLluvias)
            val precioEstiaje = repository.obtenerPrecioVigenteEn(fechaEstiaje)

            assertEquals(1.60, precioLluvias)
            assertEquals(1.90, precioEstiaje)
        }
    }

    @Test
    fun obtenerPrecioVigenteEnUsaPrecioFallbackSiFechaCaeEnHueco() {
        runBlocking {
            val repository = FakePrecioTemporadaRepository()
            val fechaSinTarifa = LocalDate(2028, 1, 1)

            val precioFallback = repository.obtenerPrecioVigenteEn(fechaSinTarifa)
            assertEquals(FakePrecioTemporadaRepository.PRECIO_BASE_FALLBACK, precioFallback)
        }
    }

    @Test
    fun registrarPagoManualEnSemanasLiquidadas() {
        runBlocking {
            val repository = FakePagoRepository()
            val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val pago = PagoEntrega(
                id = "PAGO-100",
                liquidacionId = "LIQ-2026-09",
                proveedorId = "P-014",
                monto = 180.50,
                fechaHora = ahora,
                encargadoId = "PAGOS-01",
                metodoPago = "EFECTIVO",
            )
            repository.registrarPago(pago)

            val pagos = repository.observarPagosDe("P-014").first()
            assertNotNull(pagos.find { it.id == "PAGO-100" })
            assertEquals("PAGOS-01", pagos[0].encargadoId)
        }
    }

    @Test
    fun calculadoraLiquidacionUtilizaPrecioPorLitroPorTemporada() {
        val semanaInicio = LocalDate(2026, 9, 10)
        val liquidacion160 = CalculadoraLiquidacion.calcular(
            id = "LIQ-1",
            proveedorId = "P-014",
            semanaInicio = semanaInicio,
            litrosAceptados = 100.0,
            precioPorLitroVigente = 1.60,
        )

        val liquidacion190 = CalculadoraLiquidacion.calcular(
            id = "LIQ-2",
            proveedorId = "P-014",
            semanaInicio = semanaInicio,
            litrosAceptados = 100.0,
            precioPorLitroVigente = 1.90,
        )

        assertEquals(160.0, liquidacion160.montoFinal)
        assertEquals(190.0, liquidacion190.montoFinal)
    }
}
