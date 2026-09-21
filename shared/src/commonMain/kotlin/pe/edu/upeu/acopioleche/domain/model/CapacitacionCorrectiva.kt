package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

// EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
/**
 * RN-13/RF-20: acidez fuera de rango NO es sanción económica, genera esto en su lugar. Hoy es
 * inalcanzable desde el flujo real de análisis: el lactoescan (Fase 3) no mide acidez, así que
 * `EvaluadorCalidad` nunca produce `FueraDeRango(motivo = ACIDEZ_FUERA_DE_RANGO)` — se conserva
 * y se prueba igual porque es una regla confirmada de la Parte A, por si acidez vuelve a
 * medirse de otra forma. Ver docs/modelo-dominio.md, sección 3, contradicción 2.
 */
data class CapacitacionCorrectiva(
    val id: String,
    val proveedorId: String,
    val motivo: String,
    val fecha: LocalDate,
    val atendida: Boolean = false,
)
