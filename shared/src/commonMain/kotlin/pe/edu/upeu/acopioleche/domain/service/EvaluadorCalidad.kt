package pe.edu.upeu.acopioleche.domain.service

import pe.edu.upeu.acopioleche.domain.model.LecturaLactoescan
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis

/**
 * Clasifica una [LecturaLactoescan] en la variante de [ResultadoAnalisis] correspondiente, para
 * el rechazo automático de RF-04/RF-05 ("sin aprobación humana") y el registro de adulteración
 * de RF-13.
 *
 * Reglas de clasificación (orden de evaluación, importante):
 * 1. Si `porcentajeAguaAnadida > 0`, es adulteración (RF-13) — tiene prioridad sobre cualquier
 *    otro parámetro fuera de rango, porque el lactoescan mide agua añadida específicamente para
 *    detectar esto, a diferencia de los demás parámetros que solo indican calidad.
 * 2. Si no hay adulteración, se revisan densidad/grasa/proteína/lactosa/temperatura/pH en ese
 *    orden fijo; el primero fuera de rango determina el [MotivoRechazo] (una `Entrega` solo
 *    guarda un motivo a la vez, igual que en la Parte A — si varios parámetros fallan a la vez,
 *    se reporta el primero de la lista, no todos).
 * 3. Si ninguno falla, es normal (`acidez` se guarda en 0.0: no la mide el dispositivo real).
 *
 * TODO: los rangos de [RangosPermitidos] son valores de ejemplo (típicos de leche cruda de
 * vaca), pendientes de confirmar con el interesado o el laboratorio de la planta antes de un
 * uso real. `porcentajeAguaAnadida > 0.0` como umbral de adulteración es una decisión técnica
 * razonable (el dispositivo debería reportar 0 % en leche pura), no un valor pedido; el 5% de
 * las reglas de sanción (Fase 4, RN-10/11/12) decide la severidad, no si hay o no adulteración.
 */
object EvaluadorCalidad {

    object RangosPermitidos {
        val DENSIDAD: ClosedRange<Double> = 1.028..1.034
        val GRASA: ClosedRange<Double> = 3.0..6.0
        val PROTEINA: ClosedRange<Double> = 2.9..3.8
        val LACTOSA: ClosedRange<Double> = 4.0..5.0
        val TEMPERATURA: ClosedRange<Double> = 0.0..10.0
        val PH: ClosedRange<Double> = 6.6..6.8
    }

    fun evaluar(lectura: LecturaLactoescan): ResultadoAnalisis {
        if (lectura.porcentajeAguaAnadida > 0.0) {
            return ResultadoAnalisis.Adulterada(
                indicio = "Agua añadida detectada: ${lectura.porcentajeAguaAnadida} %",
                porcentajeAgua = lectura.porcentajeAguaAnadida,
            )
        }

        val parametros = listOf(
            Triple(lectura.densidad, RangosPermitidos.DENSIDAD, MotivoRechazo.DENSIDAD_FUERA_DE_RANGO),
            Triple(lectura.grasa, RangosPermitidos.GRASA, MotivoRechazo.GRASA_FUERA_DE_RANGO),
            Triple(lectura.proteina, RangosPermitidos.PROTEINA, MotivoRechazo.PROTEINA_FUERA_DE_RANGO),
            Triple(lectura.lactosa, RangosPermitidos.LACTOSA, MotivoRechazo.LACTOSA_FUERA_DE_RANGO),
            Triple(lectura.temperatura, RangosPermitidos.TEMPERATURA, MotivoRechazo.TEMPERATURA_FUERA_DE_RANGO),
            Triple(lectura.ph, RangosPermitidos.PH, MotivoRechazo.PH_FUERA_DE_RANGO),
        )

        val primeroFueraDeRango = parametros.firstOrNull { (valor, rango, _) -> valor !in rango }
        if (primeroFueraDeRango != null) {
            val (valor, rango, motivo) = primeroFueraDeRango
            return ResultadoAnalisis.FueraDeRango(motivo = motivo, valorMedido = valor, rangoPermitido = rango)
        }

        return ResultadoAnalisis.Normal(
            densidad = lectura.densidad,
            acidez = 0.0,
            grasa = lectura.grasa,
            proteina = lectura.proteina,
            lactosa = lectura.lactosa,
            temperatura = lectura.temperatura,
            ph = lectura.ph,
        )
    }
}
