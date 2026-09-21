package pe.edu.upeu.acopioleche.domain.model

/**
 * Identidad de la sesión con la que se inició sesión (RF-01): usuario, rol, centro de acopio
 * y proveedorId opcional (para rol PRODUCTOR y aislamiento de datos RN-29).
 */
data class SesionActiva(
    val usuarioId: String,
    val nombreCompleto: String,
    val rol: RolUsuario,
    val centroAcopioId: String?,
    val proveedorId: String? = null,
)
