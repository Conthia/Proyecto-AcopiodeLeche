package pe.edu.upeu.acopioleche.domain.service

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Ciclo semanal confirmado por el interesado (Fase 5, RF-06/07): la semana va de jueves a
 * miércoles, se paga cada viernes. Vive aquí (no solo en Fase 5) porque la Fase 4 ya necesitaba
 * saber qué semana marcar como afectada por una reducción de precio (RN-10, "TODA la semana"),
 * antes de que la Fase 5 calculara montos.
 */
object CicloSemanal {

    private fun diasDesdeInicioDeCiclo(fecha: LocalDate): Int =
        (fecha.dayOfWeek.ordinal - DayOfWeek.THURSDAY.ordinal + 7) % 7

    /** Jueves que inicia la semana (jueves a miércoles) a la que pertenece [fecha]. */
    fun inicioDeSemana(fecha: LocalDate): LocalDate =
        fecha.minus(diasDesdeInicioDeCiclo(fecha), DateTimeUnit.DAY)

    /**
     * Distancia en días desde el jueves de inicio del ciclo hasta [fecha]: 0 = jueves, 1 =
     * viernes, ..., 6 = miércoles. Para cualquier [fecha], siempre se cumple
     * `inicioDeSemana(fecha) + indiceEnCiclo(fecha) días == fecha`.
     */
    fun indiceEnCiclo(fecha: LocalDate): Int = diasDesdeInicioDeCiclo(fecha)

    /** Viernes siguiente al cierre de la semana que empieza en [semanaInicio] (jueves + 8 días). */
    fun fechaDePago(semanaInicio: LocalDate): LocalDate = semanaInicio.plus(8, DateTimeUnit.DAY)
}
