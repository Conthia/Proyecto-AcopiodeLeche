package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

// CONFIRMADO CON EL INTERESADO (matriz de trazabilidad del proyecto, RN-10/RN-11/RN-12, RF-19 —
// reconfirmado 2026-09-14). El contador de "primera vez" vs. "segunda vez" (RN-11) de este
// proveedor NO es un campo dedicado: es el conteo histórico (de por vida, nunca se reinicia) de
// filas de este tipo para ese `proveedorId` — ver `observarSancionesDe` en `SancionRepository` y
// su uso en `RegistrarAnalisisViewModel.aplicarSancionPorAdulteracion`. Es genérico a cualquier
// causa de adulteración futura, no solo agua añadida: esta sealed interface no registra la
// causa, solo el desenlace de la sanción.
/**
 * Registro histórico de una sanción ya aplicada (RF-19) — a diferencia de [ResultadoSancion],
 * que es solo la decisión en el momento de evaluar, esto persiste para poder contar "primera
 * vez" vs. "segunda vez" (RN-11) en futuras detecciones del mismo proveedor, y para que la
 * Fase 5 (liquidación semanal) sepa qué semanas tienen una reducción de precio pendiente.
 *
 * [ReduccionPrecioSemanal] no lleva su propio porcentaje/monto de descuento: el % es un valor
 * global (no por sanción) — ver [pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion.PORCENTAJE_REDUCCION_POR_ADULTERACION_LEVE],
 * todavía con un TODO porque RN-10 no especifica el monto exacto ("se reduce el precio", sin
 * decir cuánto) — mismo caso pendiente que RN-14 (bonificación por grasa).
 */
sealed interface SancionAplicada {
    val id: String
    val proveedorId: String
    val fecha: LocalDate

    data class ReduccionPrecioSemanal(
        override val id: String,
        override val proveedorId: String,
        override val fecha: LocalDate,
        /** Jueves que inicia la semana afectada (RN-10: "TODA la semana"), no solo esa entrega. */
        val semanaInicio: LocalDate,
    ) : SancionAplicada

    data class RetiroPorAdulteracion(
        override val id: String,
        override val proveedorId: String,
        override val fecha: LocalDate,
        /** 0.0 cuando es RN-12 (retiro inmediato, "sin sanción progresiva" = sin multa). */
        val montoMulta: Double,
    ) : SancionAplicada
}
