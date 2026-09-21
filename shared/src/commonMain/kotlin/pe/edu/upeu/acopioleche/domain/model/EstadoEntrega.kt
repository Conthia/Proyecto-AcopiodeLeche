package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Ciclo de vida de una [Entrega], desde que el acopiador la registra hasta que se liquida
 * al proveedor. Modelado como jerarquía cerrada porque cada transición exige datos distintos.
 */
sealed interface EstadoEntrega {
    data object Pendiente : EstadoEntrega

    data object Aceptada : EstadoEntrega

    data class Rechazada(
        val motivo: MotivoRechazo,
    ) : EstadoEntrega

    data class NoRecogida(
        val motivo: String,
    ) : EstadoEntrega

    data class EnTransitoAPlanta(
        val transportistaId: String,
        val horaSalida: LocalDateTime,
    ) : EstadoEntrega

    data class Liquidada(
        val liquidacionId: String,
    ) : EstadoEntrega

    data class Cancelada(
        val motivo: String,
        val canceladaPor: String, // ID del usuario autenticado de la sesión
        val fechaHora: LocalDateTime,
    ) : EstadoEntrega
}
