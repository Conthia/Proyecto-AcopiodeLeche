package pe.edu.upeu.acopioleche.domain.service

import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion

/**
 * Decide la sanción escalonada por adulteración (RF-19), según RN-10/RN-11/RN-12 — reconfirmadas
 * por el interesado el 2026-09-14 vía la matriz de trazabilidad del proyecto, que reemplaza
 * cualquier supuesto previo de "2 o 3 oportunidades" o de un % de descuento fijo. El orden de
 * evaluación importa: "segunda vez" (RN-11) tiene prioridad sobre el porcentaje — se aplica sin
 * importar si esta detección es menor o mayor al umbral.
 *
 * `numeroAdulteracionesPrevias` es el conteo HISTÓRICO de por vida (nunca se reinicia) de
 * detecciones de adulteración de este proveedor, de cualquier causa — no solo agua añadida. El
 * llamador lo obtiene contando los [pe.edu.upeu.acopioleche.domain.model.SancionAplicada] ya
 * registrados para ese proveedor, no de un contador dedicado.
 */
object MotorSanciones {
    const val UMBRAL_ADULTERACION_GRAVE_PORCENTAJE: Double = 5.0
    const val MULTA_SEGUNDA_ADULTERACION: Double = 5000.0

    fun evaluarAdulteracion(
        proveedorId: String,
        porcentajeAgua: Double,
        numeroAdulteracionesPrevias: Int,
    ): ResultadoSancion =
        when {
            numeroAdulteracionesPrevias >= 1 ->
                ResultadoSancion.RetirarYMultar(proveedorId = proveedorId, montoMulta = MULTA_SEGUNDA_ADULTERACION)
            // Estrictamente mayor: exactamente 5% cuenta como RN-10 (leve), confirmado 2026-09-14.
            porcentajeAgua > UMBRAL_ADULTERACION_GRAVE_PORCENTAJE ->
                ResultadoSancion.RetirarInmediato(proveedorId = proveedorId)
            else ->
                ResultadoSancion.ReducirPrecioSemanal(proveedorId = proveedorId)
        }
}
