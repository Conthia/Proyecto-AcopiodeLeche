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

    fun evaluarAdulteracion(
        proveedorId: String,
        porcentajeAgua: Double,
        numeroAdulteracionesPrevias: Int,
        reglas: ReglasNegocio,
    ): ResultadoSancion =
        when {
            numeroAdulteracionesPrevias >= 1 ->
                ResultadoSancion.RetirarYMultar(proveedorId = proveedorId, montoMulta = reglas.multaSegundaAdulteracion)
            // Estrictamente mayor: exactamente el umbral cuenta como RN-10 (leve), confirmado 2026-09-14.
            porcentajeAgua > reglas.umbralAdulteracionGravePorcentaje ->
                ResultadoSancion.RetirarInmediato(proveedorId = proveedorId)
            else ->
                ResultadoSancion.ReducirPrecioSemanal(proveedorId = proveedorId)
        }
}
