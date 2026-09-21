package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime

data class AnalisisCalidad(
    val id: String,
    val entregaId: String,
    val tecnicoId: String,
    val fecha: LocalDateTime,
    val resultado: ResultadoAnalisis,
    val criterioSeleccion: CriterioAnalisis = CriterioAnalisis.ALEATORIO,
    val origenDato: OrigenDatoAnalisis = OrigenDatoAnalisis.INGRESO_MANUAL,
    val firmaProductorPresente: Boolean = true,
)
