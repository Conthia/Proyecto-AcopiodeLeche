package pe.edu.upeu.acopioleche.presentation.reportes

/**
 * Metadato de un reporte descargable. No es una entidad de dominio (Parte A/B no define
 * "Reporte"): esta versión aún no genera archivos, solo enumera qué reportes se ofrecerán.
 * Ver docs/modelo-dominio.md, sección "Decisiones de alcance".
 */
data class ReporteDescriptor(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val formato: String,
)
