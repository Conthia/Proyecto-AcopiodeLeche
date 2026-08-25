package pe.edu.upeu.acopioleche.domain.model

sealed interface EstadoEntrega {

    data object Pendiente : EstadoEntrega

    data object Aceptada : EstadoEntrega

    data class Rechazada(
        val motivo: MotivoRechazo,
    ) : EstadoEntrega

    data class EnTransitoAPlanta(
        val transportistaId: String,
        val horaSalida: String,
    ) : EstadoEntrega

    data class Liquidada(
        val liquidacionId: String,
    ) : EstadoEntrega
}
