package pe.edu.upeu.acopioleche.domain.model

// EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
/**
 * Calificación histórica acumulada de un proveedor (no el resultado de una entrega puntual).
 * A = calidad consistentemente dentro de norma, B = calidad con observaciones recurrentes,
 * C = historial con rechazos o adulteraciones detectadas.
 */
enum class CalificacionProveedor {
    A,
    B,
    C,
}
