package pe.edu.upeu.acopioleche.domain.model

/**
 * Turno de acopio dentro del día. Tipo de apoyo para el campo `turno` de [Entrega],
 * cuyo nombre y presencia sí están definidos en la Parte A del dominio confirmado;
 * el tipo Kotlin concreto es una decisión de implementación (ver docs/modelo-dominio.md).
 */
enum class Turno {
    MANANA,
    TARDE;

    companion object {
        fun deducirDeHora(hora: Int): Turno = if (hora < 12) MANANA else TARDE
    }
}
