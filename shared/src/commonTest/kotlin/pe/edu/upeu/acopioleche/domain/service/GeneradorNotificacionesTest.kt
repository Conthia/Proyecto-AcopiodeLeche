package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion

class GeneradorNotificacionesTest {

    private val fecha = LocalDateTime(2026, 9, 5, 18, 0)

    @Test
    fun `resumenEntregaDiaria usa el tipo correcto y menciona los litros`() {
        val notificacion = GeneradorNotificaciones.resumenEntregaDiaria(id = "N-1", proveedorId = "P-014", litrosHoy = 24.0, fecha = fecha)

        assertEquals(expected = TipoNotificacion.RESUMEN_ENTREGA_DIARIA, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("24.0"))
        assertEquals(expected = true, actual = notificacion.sonidoDistintivo)
    }

    @Test
    fun `resumenEntregaSemanal usa el tipo correcto y menciona el monto`() {
        val notificacion = GeneradorNotificaciones.resumenEntregaSemanal(
            id = "N-2", proveedorId = "P-014", litrosSemana = 150.0, montoFinal = 255.0, fecha = fecha,
        )

        assertEquals(expected = TipoNotificacion.RESUMEN_ENTREGA_SEMANAL, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("255.0"))
    }

    @Test
    fun `alertaAdulteracion RN-10 menciona el porcentaje y que se disculpa la falta con reduccion de precio`() {
        val notificacion = GeneradorNotificaciones.alertaAdulteracion(
            id = "N-3", proveedorId = "P-008", porcentajeAgua = 3.0,
            decision = ResultadoSancion.ReducirPrecioSemanal(proveedorId = "P-008"),
            fecha = fecha,
        )

        assertEquals(expected = TipoNotificacion.ALERTA_ADULTERACION, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("3.0"))
        assertTrue(notificacion.mensaje.contains("se reducirá el precio"))
    }

    @Test
    fun `alertaAdulteracion RN-11 menciona el retiro y el monto exacto de la multa`() {
        val notificacion = GeneradorNotificaciones.alertaAdulteracion(
            id = "N-3b", proveedorId = "P-008", porcentajeAgua = 1.0,
            decision = ResultadoSancion.RetirarYMultar(proveedorId = "P-008", montoMulta = 5000.0),
            fecha = fecha,
        )

        assertTrue(notificacion.mensaje.contains("retirado del padrón"))
        assertTrue(notificacion.mensaje.contains("5000.0"))
    }

    @Test
    fun `alertaAdulteracion RN-12 menciona el retiro inmediato sin mencionar multa`() {
        val notificacion = GeneradorNotificaciones.alertaAdulteracion(
            id = "N-3c", proveedorId = "P-008", porcentajeAgua = 6.5,
            decision = ResultadoSancion.RetirarInmediato(proveedorId = "P-008"),
            fecha = fecha,
        )

        assertTrue(notificacion.mensaje.contains("retirado de inmediato"))
        assertTrue(!notificacion.mensaje.contains("multa"))
    }

    @Test
    fun `resultadoDensidad usa el tipo correcto y menciona el valor medido`() {
        val notificacion = GeneradorNotificaciones.resultadoDensidad(id = "N-4", proveedorId = "P-014", densidad = 1.031, fecha = fecha)

        assertEquals(expected = TipoNotificacion.RESULTADO_DENSIDAD, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("1.031"))
    }

    @Test
    fun `citacionReunion usa el tipo correcto y menciona el tema y el lugar`() {
        val notificacion = GeneradorNotificaciones.citacionReunion(
            id = "N-5",
            proveedorId = "P-014",
            tema = "Reunión mensual de productores",
            fechaReunion = LocalDate(2026, 9, 12),
            lugar = "Local comunal Huata Centro",
            fecha = fecha,
        )

        assertEquals(expected = TipoNotificacion.CITACION_REUNION, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("Reunión mensual de productores"))
        assertTrue(notificacion.mensaje.contains("Local comunal Huata Centro"))
    }

    @Test
    fun `avisoCapacitacion usa el tipo correcto y menciona el motivo`() {
        val notificacion = GeneradorNotificaciones.avisoCapacitacion(
            id = "N-6", proveedorId = "P-008", motivo = "Acidez fuera de rango", fecha = fecha,
        )

        assertEquals(expected = TipoNotificacion.AVISO_CAPACITACION, actual = notificacion.tipo)
        assertTrue(notificacion.mensaje.contains("Acidez fuera de rango"))
    }
}
