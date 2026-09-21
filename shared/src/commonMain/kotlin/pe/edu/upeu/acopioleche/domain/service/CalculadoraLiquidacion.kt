package pe.edu.upeu.acopioleche.domain.service

import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Liquidacion

/**
 * Calcula la liquidación semanal de un proveedor (RF-06/RF-07). Precio de referencia por temporada (RF-28)
 * y ciclo semanal confirmados por el interesado; lo que falta confirmar queda en TODOs explícitos, no
 * inventado.
 */
object CalculadoraLiquidacion {
    const val PRECIO_REFERENCIA_POR_LITRO: Double = 1.70

    // TODO: % de reducción de precio por adulteración leve (RN-10) NO CONFIRMADO con el
    // interesado — la matriz solo dice "se reduce el precio de toda la leche entregada esa
    // semana" sin indicar cuánto (mismo caso pendiente que RN-14, la bonificación por grasa).
    // Valor propuesto mientras se confirma.
    const val PORCENTAJE_REDUCCION_POR_ADULTERACION_LEVE: Double = 0.15

    /**
     * Fecha que decide qué [pe.edu.upeu.acopioleche.domain.model.PrecioTemporada] aplica a una
     * semana de liquidación. **PROVISIONAL, no confirmado con el cliente** (ver PENDIENTES.md):
     * de las tres opciones evaluadas — fecha de cada entrega, inicio del ciclo, fecha de pago —
     * se eligió el inicio del ciclo (`semanaInicio`) porque no exige romper `litrosAceptados` en
     * un único `Double` por semana. Tanto `LiquidacionesViewModel` como `PagosHomeViewModel`
     * pasan por aquí, así que cambiar la opción elegida es modificar esta única línea.
     */
    fun fechaReferenciaPrecio(semanaInicio: LocalDate): LocalDate = semanaInicio

    fun calcular(
        id: String,
        proveedorId: String,
        semanaInicio: LocalDate,
        litrosAceptados: Double,
        precioPorLitroVigente: Double = PRECIO_REFERENCIA_POR_LITRO,
        tieneSancionReduccionPendiente: Boolean = false,
        generadaAutomaticamente: Boolean = true,
    ): Liquidacion {
        val montoBruto = litrosAceptados * precioPorLitroVigente

        // TODO: Bonificación por grasa alta NO CONFIRMADA con el interesado.
        // El precio base por litro solo se ajusta a la baja mediante sanciones aplicadas (RN-10), nunca al alza.
        val montoFinal = if (tieneSancionReduccionPendiente) {
            montoBruto * (1 - PORCENTAJE_REDUCCION_POR_ADULTERACION_LEVE)
        } else {
            montoBruto
        }

        return Liquidacion(
            id = id,
            proveedorId = proveedorId,
            semanaInicio = semanaInicio,
            litrosAceptados = litrosAceptados,
            montoBruto = montoBruto,
            montoFinal = montoFinal,
            tieneSancionPendienteDeMonto = tieneSancionReduccionPendiente,
            fechaPago = CicloSemanal.fechaDePago(semanaInicio),
            generadaAutomaticamente = generadaAutomaticamente,
            precioPorLitroAplicado = precioPorLitroVigente,
        )
    }
}
