package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EntregaTest {

    private fun entregaValida(estado: EstadoEntrega = EstadoEntrega.Pendiente) = Entrega(
        id = "e-1",
        proveedorId = "p-1",
        acopiadorId = "a-1",
        centroAcopioId = "c-1",
        fecha = "2026-08-24",
        turno = "MANIANA",
        volumenLitros = 50.0,
        estado = estado,
    )

    @Test
    fun `entrega con volumen positivo se crea correctamente`() {
        val entrega = entregaValida()

        assertEquals(expected = 50.0, actual = entrega.volumenLitros)
    }

    @Test
    fun `entrega con volumen cero lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Entrega(
                id = "e-1",
                proveedorId = "p-1",
                acopiadorId = "a-1",
                centroAcopioId = "c-1",
                fecha = "2026-08-24",
                turno = "MANIANA",
                volumenLitros = 0.0,
                estado = EstadoEntrega.Pendiente,
            )
        }
    }

    @Test
    fun `entrega con volumen negativo lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Entrega(
                id = "e-1",
                proveedorId = "p-1",
                acopiadorId = "a-1",
                centroAcopioId = "c-1",
                fecha = "2026-08-24",
                turno = "MANIANA",
                volumenLitros = -10.0,
                estado = EstadoEntrega.Pendiente,
            )
        }
    }

    @Test
    fun `entrega sin proveedor asociado lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Entrega(
                id = "e-1",
                proveedorId = "",
                acopiadorId = "a-1",
                centroAcopioId = "c-1",
                fecha = "2026-08-24",
                turno = "MANIANA",
                volumenLitros = 50.0,
                estado = EstadoEntrega.Pendiente,
            )
        }
    }

    @Test
    fun `estado rechazada conserva el motivo del rechazo`() {
        val entrega = entregaValida(
            estado = EstadoEntrega.Rechazada(motivo = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO),
        )

        val estado = entrega.estado
        check(estado is EstadoEntrega.Rechazada)
        assertEquals(expected = MotivoRechazo.DENSIDAD_FUERA_DE_RANGO, actual = estado.motivo)
    }

    @Test
    fun `when exhaustivo sobre estado entrega cubre todos los casos`() {
        val estados = listOf(
            EstadoEntrega.Pendiente,
            EstadoEntrega.Aceptada,
            EstadoEntrega.Rechazada(motivo = MotivoRechazo.ACIDEZ_FUERA_DE_RANGO),
            EstadoEntrega.EnTransitoAPlanta(transportistaId = "t-1", horaSalida = "07:30"),
            EstadoEntrega.Liquidada(liquidacionId = "liq-1"),
        )

        val descripciones = estados.map { estado ->
            when (estado) {
                is EstadoEntrega.Pendiente -> "pendiente"
                is EstadoEntrega.Aceptada -> "aceptada"
                is EstadoEntrega.Rechazada -> "rechazada"
                is EstadoEntrega.EnTransitoAPlanta -> "en_transito"
                is EstadoEntrega.Liquidada -> "liquidada"
            }
        }

        assertEquals(
            expected = listOf("pendiente", "aceptada", "rechazada", "en_transito", "liquidada"),
            actual = descripciones,
        )
    }
}
