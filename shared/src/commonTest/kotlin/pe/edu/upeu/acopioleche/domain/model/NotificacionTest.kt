package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDateTime

class NotificacionTest {

    private fun notificacionDePrueba(): Notificacion =
        Notificacion(
            id = "N-01",
            destinatarioId = "P-014",
            tipo = TipoNotificacion.RESUMEN_ENTREGA_DIARIA,
            mensaje = "Hoy entregaste 24.0 L en total.",
            fechaEnvio = LocalDateTime(2026, 9, 5, 18, 0),
        )

    @Test
    fun `sonidoDistintivo es true por defecto`() {
        assertEquals(expected = true, actual = notificacionDePrueba().sonidoDistintivo)
    }

    @Test
    fun `leida es false por defecto`() {
        assertEquals(expected = false, actual = notificacionDePrueba().leida)
    }

    @Test
    fun `se puede marcar como leida sin alterar el resto de los campos`() {
        val original = notificacionDePrueba()

        val leida = original.copy(leida = true)

        assertEquals(expected = true, actual = leida.leida)
        assertEquals(expected = original.mensaje, actual = leida.mensaje)
        assertEquals(expected = original.tipo, actual = leida.tipo)
    }

    @Test
    fun `conserva el tipo y el destinatario`() {
        val notificacion = notificacionDePrueba()

        assertEquals(expected = TipoNotificacion.RESUMEN_ENTREGA_DIARIA, actual = notificacion.tipo)
        assertEquals(expected = "P-014", actual = notificacion.destinatarioId)
    }
}
