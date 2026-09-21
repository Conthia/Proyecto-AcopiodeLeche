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
 * Los rangos de calidad (RF-04/RF-05, SUPUESTOS — del equipo, no del cliente ni del laboratorio)
 * vienen de [pe.edu.upeu.acopioleche.domain.service.ReglasNegocio.rangosCalidad], no de un
 * `object` interno — ver ese archivo para la clasificación completa. `porcentajeAguaAnadida >
 * 0.0` como umbral de adulteración es una decisión técnica razonable (el dispositivo debería
 * reportar 0 % en leche pura), no un valor pedido; el umbral de
 * [ReglasNegocio.umbralAdulteracionGravePorcentaje] decide la severidad, no si hay o no
 * adulteración.
 */
object EvaluadorCalidad {

    fun evaluar(lectura: LecturaLactoescan, reglas: ReglasNegocio): ResultadoAnalisis {
        if (lectura.porcentajeAguaAnadida > 0.0) {
            return ResultadoAnalisis.Adulterada(
                indicio = "Agua añadida detectada: ${lectura.porcentajeAguaAnadida} %",
                porcentajeAgua = lectura.porcentajeAguaAnadida,
            )
        }

        val rangos = reglas.rangosCalidad
        val parametros = listOf(
            Triple(lectura.densidad, rangos.densidad, MotivoRechazo.DENSIDAD_FUERA_DE_RANGO),
            Triple(lectura.grasa, rangos.grasa, MotivoRechazo.GRASA_FUERA_DE_RANGO),
            Triple(lectura.proteina, rangos.proteina, MotivoRechazo.PROTEINA_FUERA_DE_RANGO),
            Triple(lectura.lactosa, rangos.lactosa, MotivoRechazo.LACTOSA_FUERA_DE_RANGO),
            Triple(lectura.temperatura, rangos.temperatura, MotivoRechazo.TEMPERATURA_FUERA_DE_RANGO),
            Triple(lectura.ph, rangos.ph, MotivoRechazo.PH_FUERA_DE_RANGO),
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
