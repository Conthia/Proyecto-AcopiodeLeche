package pe.edu.upeu.acopioleche.domain.model

/**
 * Origen del dato capturado del LactoScan en campo (RF-29).
 * Permite distinguir si los parámetros provienen del escaneo/OCR del ticket o del ingreso manual.
 */
enum class OrigenDatoAnalisis {
    OCR_COMPROBANTE,
    INGRESO_MANUAL,
}
