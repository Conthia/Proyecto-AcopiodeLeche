package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion

/**
 * Liquidación semanal de pago a un proveedor (RF-06/RF-07). La Parte A ya referenciaba este
 * concepto (`EstadoEntrega.Liquidada.liquidacionId`) sin tipar su forma; estos campos son la
 * decisión de implementación de esta fase para completarlo — no es una extensión de negocio,
 * es un detalle técnico (ver docs/modelo-dominio.md, sección 1, mismo tratamiento que `Turno`).
 *
 * `montoFinal` es igual a `montoBruto` salvo que `tieneSancionPendienteDeMonto` sea `true`
 * (RN-10), en cuyo caso se le resta el % propuesto en
 * [pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion.PORCENTAJE_REDUCCION_POR_ADULTERACION_LEVE]
 * — nunca se le suma nada (ver el TODO de la bonificación por grasa, RN-14, ahí mismo).
 */
data class Liquidacion(
    val id: String,
    val proveedorId: String,
    /** Jueves que inicia la semana liquidada (jueves a miércoles, confirmado). */
    val semanaInicio: LocalDate,
    val litrosAceptados: Double,
    val montoBruto: Double,
    val montoFinal: Double,
    val tieneSancionPendienteDeMonto: Boolean,
    /** Viernes siguiente al cierre de semana (confirmado: "se paga cada viernes"). */
    val fechaPago: LocalDate,
    val generadaAutomaticamente: Boolean,
    /**
     * Precio por litro (S/) realmente usado para calcular [montoBruto] — se guarda para que una
     * liquidación ya generada no cambie de monto retroactivamente si después se agrega o edita un
     * [PrecioTemporada] que hubiera aplicado a esa semana. Por defecto
     * [CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO], el valor con el que se calcularon
     * todas las liquidaciones antes de que este campo existiera (ver migración `3.sqm`).
     */
    val precioPorLitroAplicado: Double = CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO,
)
