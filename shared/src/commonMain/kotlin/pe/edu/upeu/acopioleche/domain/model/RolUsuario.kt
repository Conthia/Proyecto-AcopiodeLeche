package pe.edu.upeu.acopioleche.domain.model

/**
 * Rol con el que un usuario inicia sesión (RF-01).
 * Cinco roles de usuario según la matriz de requerimientos actualizada.
 */
enum class RolUsuario {
    ACOPIADOR,
    ADMINISTRADOR,
    ENCARGADO_PAGOS,
    PRODUCTOR_LACTEOS,
    PRODUCTOR,
}
