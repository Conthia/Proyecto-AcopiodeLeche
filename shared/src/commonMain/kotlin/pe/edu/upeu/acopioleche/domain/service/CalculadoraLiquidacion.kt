package pe.edu.upeu.acopioleche.domain.service

import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Liquidacion

/**
 * Calcula la liquidación semanal de un proveedor (RF-06/RF-07). Precio de referencia por temporada (RF-28)
 * y ciclo semanal confirmados por el interesado; lo que falta confirmar queda en TODOs explícitos, no
 * inventado.
 */
object CalculadoraLiquidacion {

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
        reglas: ReglasNegocio,
        precioPorLitroVigente: Double = reglas.precioReferenciaPorLitro,
        tieneSancionReduccionPendiente: Boolean = false,
        generadaAutomaticamente: Boolean = true,
    ): Liquidacion {
        val montoBruto = litrosAceptados * precioPorLitroVigente

        // RN-14: bonificación por grasa alta NO DEFINIDA (reglas.bonificacionPorGrasaPorLitro),
        // no se aplica aquí todavía — ver PENDIENTES.md. El precio base por litro solo se ajusta
        // a la baja mediante sanciones aplicadas (RN-10), nunca al alza.
        val montoFinal = if (tieneSancionReduccionPendiente) {
            montoBruto * (1 - reglas.porcentajeReduccionAdulteracionLeveProvisional)
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
