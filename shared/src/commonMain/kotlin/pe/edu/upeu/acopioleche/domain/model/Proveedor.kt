package pe.edu.upeu.acopioleche.domain.model

data class Proveedor(
    val id: String,
    val nombre: String,
    val documento: String,
    val telefono: String,
    val sector: String,
    val entregaDirectaEnPlanta: Boolean,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
    val numeroVacas: Int,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
    val calificacion: CalificacionProveedor,
    // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos.
    // Necesario para RN-11/RN-12 (Fase 4, RF-19): "retirar del padrón" necesita un estado que
    // impida nuevas entregas de ese proveedor. Mismo patrón que `CentroAcopio.activo`.
    val activo: Boolean = true,
)
