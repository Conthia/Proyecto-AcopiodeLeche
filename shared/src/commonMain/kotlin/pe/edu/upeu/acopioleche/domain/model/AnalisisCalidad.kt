package pe.edu.upeu.acopioleche.domain.model

data class AnalisisCalidad(
    val id: String,
    val entregaId: String,
    val tecnicoId: String,
    val fecha: String,
    val resultado: ResultadoAnalisis,
) {
    init {
        require(entregaId.isNotBlank()) { "El analisis de calidad debe estar asociado a una entrega" }
        require(tecnicoId.isNotBlank()) { "El analisis de calidad debe registrar un tecnico" }
        require(fecha.isNotBlank()) { "El analisis de calidad debe registrar una fecha" }
    }
}
