package pe.edu.upeu.acopioleche.domain.model

// EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
/**
 * Lectura cruda de los 7 parámetros que mide el "lactoescan" de planta (confirmado en reunión
 * con el interesado, 30/08/2026). No es una variante de [ResultadoAnalisis]: es la entrada que
 * [pe.edu.upeu.acopioleche.domain.service.EvaluadorCalidad] clasifica para decidir cuál de las
 * tres variantes aplica. No incluye `acidez` porque el dispositivo real no la mide.
 */
data class LecturaLactoescan(
    val densidad: Double,
    val grasa: Double,
    val proteina: Double,
    val lactosa: Double,
    val temperatura: Double,
    val ph: Double,
    val porcentajeAguaAnadida: Double,
)
