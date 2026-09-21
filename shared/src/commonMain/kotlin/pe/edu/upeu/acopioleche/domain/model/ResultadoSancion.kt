package pe.edu.upeu.acopioleche.domain.model

// CONFIRMADO CON EL INTERESADO (matriz de trazabilidad del proyecto, RN-10/RN-11/RN-12, RF-19 —
// reconfirmado 2026-09-14, reemplaza cualquier supuesto previo de "2 o 3 oportunidades" o de un
// % de descuento fijo).
/**
 * Decisión de [pe.edu.upeu.acopioleche.domain.service.MotorSanciones] al evaluar una
 * adulteración detectada (RF-19). Las reglas RN-10/RN-11/RN-12 confirmadas por el interesado:
 *
 * - RN-10: adulteración ≤ 5%, primera vez → [ReducirPrecioSemanal] (la entrega se acepta).
 * - RN-11: segunda detección, cualquier porcentaje → [RetirarYMultar] (tiene prioridad sobre
 *   RN-10/RN-12: no importa el porcentaje si ya es la segunda vez).
 * - RN-12: adulteración > 5%, primera vez → [RetirarInmediato] (sin multa, "sin sanción
 *   progresiva" — la multa de S/ 5000 es específica de RN-11/segunda detección).
 */
sealed interface ResultadoSancion {
    val proveedorId: String

    data class ReducirPrecioSemanal(
        override val proveedorId: String,
    ) : ResultadoSancion

    data class RetirarYMultar(
        override val proveedorId: String,
        val montoMulta: Double,
    ) : ResultadoSancion

    data class RetirarInmediato(
        override val proveedorId: String,
    ) : ResultadoSancion
}
