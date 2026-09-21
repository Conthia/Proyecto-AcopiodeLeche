package pe.edu.upeu.acopioleche.domain.model

/**
 * Criterio de selección para análisis de calidad en campo (RN-24).
 * El análisis no es obligatorio en el 100% de las entregas: puede responder a una rutina
 * programada, una muestra aleatoria, o priorización por historial de sanciones/alertas.
 */
enum class CriterioAnalisis {
    PROGRAMADO,
    ALEATORIO,
    HISTORIAL_ALERTA,
    MANUAL,
}
