package pe.edu.upeu.acopioleche.domain.model

sealed interface ResultadoAnalisis {

    data class Normal(
        val densidad: Double,
        val acidez: Double,
        val grasa: Double,
    ) : ResultadoAnalisis

    data class FueraDeRango(
        val motivo: MotivoRechazo,
        val valorMedido: Double,
        val rangoPermitido: ClosedFloatingPointRange<Double>,
    ) : ResultadoAnalisis

    data class Adulterada(
        val indicio: String,
    ) : ResultadoAnalisis
}
