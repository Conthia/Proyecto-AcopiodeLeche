package pe.edu.upeu.acopioleche.presentation.proveedor

import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor

/**
 * `litrosHoy` es el volumen real entregado hoy (suma de sus [pe.edu.upeu.acopioleche.domain.model.Entrega]),
 * no un promedio histórico: esta versión aún no guarda entregas de días anteriores
 * (ver docs/modelo-dominio.md, sección "Decisiones de alcance").
 */
data class ProveedorConEntregas(
    val id: String,
    val nombre: String,
    val sector: String,
    val numeroVacas: Int,
    val litrosHoy: Double,
    val calificacion: CalificacionProveedor,
    val activo: Boolean,
    val documento: String = "",
    val telefono: String = "",
    val entregaDirectaEnPlanta: Boolean = false,
    /** true = hay un alta/edición/baja local que todavía no confirmó el backend. */
    val pendienteSync: Boolean = false,
)
