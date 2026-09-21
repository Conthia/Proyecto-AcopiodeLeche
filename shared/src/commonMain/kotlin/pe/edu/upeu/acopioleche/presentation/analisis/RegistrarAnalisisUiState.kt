package pe.edu.upeu.acopioleche.presentation.analisis

import pe.edu.upeu.acopioleche.domain.model.CriterioAnalisis
import pe.edu.upeu.acopioleche.domain.model.OrigenDatoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion

/**
 * `acidez` no aparece aquí a propósito: el "lactoescan" real no la mide (ver
 * docs/modelo-dominio.md, sección 3), así que el formulario no la pide — se guarda en 0.0.
 */
data class RegistrarAnalisisUiState(
    val entregaId: String = "",
    val nombreProveedor: String = "",
    val densidadTexto: String = "",
    val grasaTexto: String = "",
    val proteinaTexto: String = "",
    val lactosaTexto: String = "",
    val temperaturaTexto: String = "",
    val phTexto: String = "",
    val porcentajeAguaTexto: String = "",
    val criterioSeleccion: CriterioAnalisis = CriterioAnalisis.ALEATORIO,
    val origenDato: OrigenDatoAnalisis = OrigenDatoAnalisis.INGRESO_MANUAL,
    val firmaProductorPresente: Boolean = true,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val resultadoGuardado: ResultadoAnalisis? = null,
    val sancionAplicada: ResultadoSancion? = null,
    /**
     * RN-20: se llena cuando el análisis determinó un cambio de estado (Rechazada/Aceptada) pero
     * la entrega ya estaba en un estado que no admite modificación (EnTransitoAPlanta/Liquidada).
     * El análisis igual queda registrado como evidencia; esto solo avisa que el estado de la
     * entrega no se tocó y requiere revisión manual.
     */
    val advertenciaEstado: String? = null,
) {
    val densidad: Double? get() = densidadTexto.toDoubleOrNull()
    val grasa: Double? get() = grasaTexto.toDoubleOrNull()
    val proteina: Double? get() = proteinaTexto.toDoubleOrNull()
    val lactosa: Double? get() = lactosaTexto.toDoubleOrNull()
    val temperatura: Double? get() = temperaturaTexto.toDoubleOrNull()
    val ph: Double? get() = phTexto.toDoubleOrNull()
    val porcentajeAgua: Double? get() = porcentajeAguaTexto.toDoubleOrNull()
}
