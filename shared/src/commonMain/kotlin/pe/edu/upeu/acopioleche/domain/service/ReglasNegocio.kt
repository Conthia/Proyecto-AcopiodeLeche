package pe.edu.upeu.acopioleche.domain.service

/**
 * Reglas de negocio parametrizables del sistema, centralizadas en un solo lugar en vez de
 * dispersas como `const val` en cada servicio de dominio (`MotorSanciones`,
 * `CalculadoraLiquidacion`, `PoliticaBloqueoLogin`, `EvaluadorCalidad`). Cada campo cita la RN-xx
 * del Excel del proyecto (hoja "Reglas de Negocio") cuando existe.
 *
 * Los valores CONFIRMADOS conservan el mismo número que ya corría en producción como `const val`
 * — este refactor centraliza de dónde sale el número, no lo cambia. Los SUPUESTOS (propuestos por
 * el equipo, no por el cliente) también se mantienen no-nulos porque ya se aplican en runtime hoy;
 * quitarles el valor cambiaría comportamiento sin que nadie lo haya pedido. Solo lo genuinamente
 * NO DEFINIDO (sin ningún efecto en runtime hoy porque el código que lo consumiría no existe) se
 * representa como `null` — nunca con un número inventado.
 */
data class ReglasNegocio(
    /**
     * RN-10/RN-11/RN-12 (CONFIRMADO, Excel "Reglas de Negocio"): a partir de qué % de agua
     * añadida una primera adulteración es grave (RN-12, retiro inmediato) en vez de leve (RN-10,
     * reducción de precio semanal). Exactamente este valor cuenta como leve, no como grave.
     */
    val umbralAdulteracionGravePorcentaje: Double = 5.0,

    /**
     * RN-11 (CONFIRMADO, Excel): multa en soles al detectar una segunda adulteración del mismo
     * proveedor, sin importar el porcentaje de esa segunda detección.
     */
    val multaSegundaAdulteracion: Double = 5000.0,

    /**
     * RN-10 (SUPUESTO, propuesto por el equipo — el Excel no especifica el monto exacto, solo
     * dice "se reduce el precio de toda la leche entregada esa semana"): % de reducción del
     * precio de la semana completa cuando hay una adulteración leve (primera vez, porcentaje ≤
     * [umbralAdulteracionGravePorcentaje]).
     */
    val porcentajeReduccionAdulteracionLeveProvisional: Double = 0.15,

    /** RF-01 (CONFIRMADO): intentos fallidos consecutivos que bloquean la cuenta. */
    val maxIntentosFallidos: Int = 3,

    /** RF-01 (CONFIRMADO): minutos que dura el bloqueo tras [maxIntentosFallidos]. */
    val minutosBloqueo: Int = 10,

    /**
     * RN-14 (CONFIRMADO, Excel: "sujeto a variación"): precio de referencia por litro (S/), usado
     * como respaldo cuando no hay ningún `PrecioTemporada` vigente para la fecha consultada.
     */
    val precioReferenciaPorLitro: Double = 1.70,

    /**
     * RF-04/RF-05 (SUPUESTO, del equipo — no viene del cliente ni de un laboratorio): rangos
     * fisicoquímicos que determinan el rechazo automático de una entrega.
     */
    val rangosCalidad: RangosCalidad = RangosCalidad(),

    /**
     * RN-14 (NO DEFINIDA — el propio Excel indica que este valor no está confirmado): bonificación
     * por litro (S/) cuando la grasa medida supera cierto umbral. `null` mientras no se confirme;
     * ningún cálculo debe inventar un valor ni aplicar un ajuste al alza en su ausencia.
     */
    val bonificacionPorGrasaPorLitro: Double? = null,

    /**
     * RF-10 (CONFIRMADO): horas máximas entre la recepción de la leche y el registro del
     * traslado antes de que el sistema deba advertir que se pasó el plazo. Sin ningún control
     * implementado todavía — ver PENDIENTES.md.
     */
    val plazoMaximoTrasladoHoras: Int = 4,
)

/** RF-04/RF-05 (SUPUESTO, del equipo): rangos fisicoquímicos de [ReglasNegocio.rangosCalidad]. */
data class RangosCalidad(
    val densidad: ClosedRange<Double> = 1.028..1.034,
    val grasa: ClosedRange<Double> = 3.0..6.0,
    val proteina: ClosedRange<Double> = 2.9..3.8,
    val lactosa: ClosedRange<Double> = 4.0..5.0,
    val temperatura: ClosedRange<Double> = 0.0..10.0,
    val ph: ClosedRange<Double> = 6.6..6.8,
)
