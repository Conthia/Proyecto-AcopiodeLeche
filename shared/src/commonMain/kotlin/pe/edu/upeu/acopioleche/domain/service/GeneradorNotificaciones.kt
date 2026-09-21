package pe.edu.upeu.acopioleche.domain.service

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.Notificacion
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion
import pe.edu.upeu.acopioleche.domain.model.TipoNotificacion

/**
 * Construye el texto de cada uno de los 6 [TipoNotificacion] confirmados (RF-11/14/15/16).
 * Función pura: no genera el `id` ni decide cuándo disparar cada notificación (eso lo hacen
 * los ViewModel que ya orquestan el flujo correspondiente — registrar entrega, generar
 * liquidación, registrar análisis), igual que `CalculadoraLiquidacion`/`MotorSanciones`.
 *
 * `sonidoDistintivo` siempre queda en `true`: el interesado pidió explícitamente que estos
 * avisos suenen distinto a otros mensajes del celular para que el proveedor los reconozca. Esta
 * app no tiene acceso a reproducir un sonido real de dispositivo — el campo queda como el dato
 * que una integración real (push/SMS) usaría para elegir el tono, y la bandeja lo muestra como
 * indicador visual.
 */
object GeneradorNotificaciones {

    fun resumenEntregaDiaria(id: String, proveedorId: String, litrosHoy: Double, fecha: LocalDateTime): Notificacion =
        crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.RESUMEN_ENTREGA_DIARIA,
            mensaje = "Hoy entregaste $litrosHoy L en total.",
            fecha = fecha,
        )

    fun resumenEntregaSemanal(id: String, proveedorId: String, litrosSemana: Double, montoFinal: Double, fecha: LocalDateTime): Notificacion =
        crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.RESUMEN_ENTREGA_SEMANAL,
            mensaje = "Liquidación de la semana: $litrosSemana L · S/ $montoFinal.",
            fecha = fecha,
        )

    /**
     * RN-10/RN-11/RN-12 (RF-19): el mensaje indica explícitamente cuál de los 3 desenlaces
     * aplicó — reducción de precio esa semana, retiro + multa, o retiro inmediato — para que el
     * proveedor/administrador no tenga que inferirlo del porcentaje.
     */
    fun alertaAdulteracion(id: String, proveedorId: String, porcentajeAgua: Double, decision: ResultadoSancion, fecha: LocalDateTime): Notificacion {
        val desenlace = when (decision) {
            is ResultadoSancion.ReducirPrecioSemanal ->
                "Se disculpa esta primera falta, pero se reducirá el precio de toda tu leche entregada esta semana."
            is ResultadoSancion.RetirarYMultar ->
                "Por ser la segunda detección, fuiste retirado del padrón de proveedores y se aplicó una multa de S/ ${decision.montoMulta}."
            is ResultadoSancion.RetirarInmediato ->
                "Por la magnitud detectada, fuiste retirado de inmediato del padrón de proveedores."
        }
        return crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.ALERTA_ADULTERACION,
            mensaje = "Se detectó adulteración en tu última entrega: $porcentajeAgua % de agua añadida. $desenlace",
            fecha = fecha,
        )
    }

    fun resultadoDensidad(id: String, proveedorId: String, densidad: Double, fecha: LocalDateTime): Notificacion =
        crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.RESULTADO_DENSIDAD,
            mensaje = "Resultado de densidad de tu última entrega: $densidad.",
            fecha = fecha,
        )

    fun citacionReunion(id: String, proveedorId: String, tema: String, fechaReunion: LocalDate, lugar: String, fecha: LocalDateTime): Notificacion =
        crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.CITACION_REUNION,
            mensaje = "Estás citado a \"$tema\" el $fechaReunion en $lugar.",
            fecha = fecha,
        )

    fun avisoCapacitacion(id: String, proveedorId: String, motivo: String, fecha: LocalDateTime): Notificacion =
        crear(
            id = id,
            destinatarioId = proveedorId,
            tipo = TipoNotificacion.AVISO_CAPACITACION,
            mensaje = "Se te asignó una capacitación correctiva: $motivo.",
            fecha = fecha,
        )

    private fun crear(id: String, destinatarioId: String, tipo: TipoNotificacion, mensaje: String, fecha: LocalDateTime): Notificacion =
        Notificacion(
            id = id,
            destinatarioId = destinatarioId,
            tipo = tipo,
            mensaje = mensaje,
            fechaEnvio = fecha,
            sonidoDistintivo = true,
        )
}
