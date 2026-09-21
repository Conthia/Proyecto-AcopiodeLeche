package pe.edu.upeu.acopioleche.domain.model

/**
 * ACIDEZ_FUERA_DE_RANGO es de la Parte A y se conserva tal cual, pero en la práctica queda en
 * desuso: el "lactoescan" real (confirmado en reunión del 30/08/2026) no mide acidez, así que
 * [pe.edu.upeu.acopioleche.domain.service.EvaluadorCalidad] nunca la evalúa. Los 4 valores
 * siguientes sí se usan, y son extensión — ver [ResultadoAnalisis].
 */
enum class MotivoRechazo {
    DENSIDAD_FUERA_DE_RANGO,
    ACIDEZ_FUERA_DE_RANGO,
    GRASA_FUERA_DE_RANGO,
    ADULTERACION_DETECTADA,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
    PROTEINA_FUERA_DE_RANGO,
    LACTOSA_FUERA_DE_RANGO,
    TEMPERATURA_FUERA_DE_RANGO,
    PH_FUERA_DE_RANGO,
}
