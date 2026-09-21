package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDate

/**
 * horaInicioMinutos / horaFinMinutos: minutos desde medianoche (0..1439), para no acoplar el
 * dominio a un tipo de hora con zona horaria cuando solo se necesita comparar duración dentro
 * del mismo día.
 */
data class Reunion(
    val id: String,
    val tipo: TipoEvento,
    val tema: String,
    val fecha: LocalDate,
    val horaInicioMinutos: Int,
    val horaFinMinutos: Int,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
    val lugar: String,
)
