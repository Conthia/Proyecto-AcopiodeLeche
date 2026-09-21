package pe.edu.upeu.acopioleche.domain.model

import kotlin.time.Instant

/**
 * Cuenta de acceso (RF-01).
 * Invariantes:
 * - [RolUsuario.ACOPIADOR] requiere [centroAcopioId].
 * - [RolUsuario.ADMINISTRADOR] no está atado a un solo centro.
 * - [RolUsuario.PRODUCTOR] requiere [proveedorId] para aislamiento estricto de datos (RN-29).
 */
data class Usuario(
    val id: String,
    val nombreUsuario: String,
    val contrasena: ContrasenaHash,
    val rol: RolUsuario,
    val nombreCompleto: String,
    val centroAcopioId: String?,
    val proveedorId: String? = null,
    val intentosFallidos: Int = 0,
    val ultimoIntentoFallidoEn: Instant? = null,
) {
    init {
        require(nombreUsuario.isNotBlank()) { "El nombre de usuario no puede estar vacío" }
        require(contrasena.hash.isNotBlank()) { "La contraseña hash no puede estar vacía" }
        when (rol) {
            RolUsuario.ACOPIADOR ->
                require(centroAcopioId != null) { "Un usuario ACOPIADOR debe tener un centro de acopio asignado" }
            RolUsuario.ADMINISTRADOR ->
                require(centroAcopioId == null) { "Un usuario ADMINISTRADOR no debe estar atado a un único centro de acopio" }
            RolUsuario.PRODUCTOR ->
                require(proveedorId != null) { "Un usuario PRODUCTOR debe estar vinculado a un proveedorId para aislamiento de datos (RN-29)" }
            RolUsuario.ENCARGADO_PAGOS,
            RolUsuario.PRODUCTOR_LACTEOS -> Unit
        }
    }
}
