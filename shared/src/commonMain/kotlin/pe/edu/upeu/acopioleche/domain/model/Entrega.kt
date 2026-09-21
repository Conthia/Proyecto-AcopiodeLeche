package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

/**
 * Entidad principal del dominio: registra el volumen de leche que un proveedor entrega en un
 * centro de acopio durante un turno determinado.
 */
data class Entrega(
    val id: String,
    val proveedorId: String,
    val acopiadorId: String?,
    val centroAcopioId: String,
    val fecha: LocalDate,
    val turno: Turno,
    val volumenLitros: Double,
    val estado: EstadoEntrega,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
    val cantidadPorongos: Int,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos.
    // RF-21 (conciliación campo-planta): la medición en campo (`volumenLitros`) es apurada y se
    // pierden litros en el camino (ejemplo real del interesado: tachos de 34 L llegan con 32 L).
    // Este es el segundo volumen, medido al descargar en planta; nulo hasta que se registre.
    val volumenPlantaLitros: Double? = null,
) {
    /** Nulo hasta que se registre el volumen de planta (RF-21). Negativo = se perdieron litros en el camino. */
    val diferenciaLitros: Double? get() = volumenPlantaLitros?.let { it - volumenLitros }
}
