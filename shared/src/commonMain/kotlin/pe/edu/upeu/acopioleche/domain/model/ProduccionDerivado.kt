package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

data class ProduccionDerivado(
    val id: String,
    val tipoProducto: String,
    val codigoLote: String,
    val cantidadUnidades: Double,
    val fechaProduccion: LocalDate,
    val responsableId: String,
)
