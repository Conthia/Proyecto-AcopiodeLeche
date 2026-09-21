package pe.edu.upeu.acopioleche.presentation.reportes

object ReportesCatalogo {
    val DISPONIBLES: List<ReporteDescriptor> = listOf(
        ReporteDescriptor(
            id = "REP-01",
            titulo = "Acopio diario por proveedor",
            subtitulo = "Volumen, turno y estado de envío",
            formato = "XLSX",
        ),
        ReporteDescriptor(
            id = "REP-02",
            titulo = "Calidad promedio por proveedor",
            subtitulo = "Densidad, acidez y grasa",
            formato = "PDF",
        ),
        ReporteDescriptor(
            id = "REP-03",
            titulo = "Alertas de adulteración",
            subtitulo = "Indicios detectados en el mes",
            formato = "PDF",
        ),
        ReporteDescriptor(
            id = "REP-04",
            titulo = "Asistencia a capacitaciones",
            subtitulo = "Actas firmadas por reunión",
            formato = "PDF",
        ),
        ReporteDescriptor(
            id = "REP-05",
            titulo = "Liquidación estimada por calidad",
            subtitulo = "Requiere validar fórmula de pago",
            formato = "XLSX",
        ),
    )
}
