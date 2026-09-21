package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class EstadoEntregaTest {

    @Test
    fun `Rechazada conserva su motivo`() {
        val estado: EstadoEntrega = EstadoEntrega.Rechazada(motivo = MotivoRechazo.GRASA_FUERA_DE_RANGO)

        val rechazada = assertIs<EstadoEntrega.Rechazada>(estado)
        assertEquals(expected = MotivoRechazo.GRASA_FUERA_DE_RANGO, actual = rechazada.motivo)
    }

    @Test
    fun `EnTransitoAPlanta conserva transportista y hora de salida`() {
        val salida = LocalDateTime(2026, 9, 5, 6, 15)
        val estado: EstadoEntrega = EstadoEntrega.EnTransitoAPlanta(
            transportistaId = "T-01",
            horaSalida = salida,
        )

        val enTransito = assertIs<EstadoEntrega.EnTransitoAPlanta>(estado)
        assertEquals(expected = "T-01", actual = enTransito.transportistaId)
        assertEquals(expected = salida, actual = enTransito.horaSalida)
    }

    @Test
    fun `Liquidada conserva el id de liquidacion`() {
        val estado: EstadoEntrega = EstadoEntrega.Liquidada(liquidacionId = "LIQ-2026-09")

        val liquidada = assertIs<EstadoEntrega.Liquidada>(estado)
        assertEquals(expected = "LIQ-2026-09", actual = liquidada.liquidacionId)
    }

    @Test
    fun `Pendiente y Aceptada son objetos unicos y distintos entre si`() {
        assertIs<EstadoEntrega.Pendiente>(EstadoEntrega.Pendiente)
        assertIs<EstadoEntrega.Aceptada>(EstadoEntrega.Aceptada)
        assertEquals(expected = EstadoEntrega.Pendiente, actual = EstadoEntrega.Pendiente)
    }
}
