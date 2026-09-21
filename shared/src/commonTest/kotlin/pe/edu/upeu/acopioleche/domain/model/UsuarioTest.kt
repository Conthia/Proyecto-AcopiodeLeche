package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertFailsWith
import pe.edu.upeu.acopioleche.domain.service.PasswordHasher

class UsuarioTest {

    private val contrasena = PasswordHasher.hash(contrasenaPlano = "Acopio2026")

    @Test
    fun `un ACOPIADOR con centro de acopio asignado se construye correctamente`() {
        Usuario(
            id = "A-01",
            nombreUsuario = "jmamani",
            contrasena = contrasena,
            rol = RolUsuario.ACOPIADOR,
            nombreCompleto = "Juan Mamani",
            centroAcopioId = "CA-002",
        )
    }

    @Test
    fun `un ACOPIADOR sin centro de acopio asignado lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Usuario(
                id = "A-01",
                nombreUsuario = "jmamani",
                contrasena = contrasena,
                rol = RolUsuario.ACOPIADOR,
                nombreCompleto = "Juan Mamani",
                centroAcopioId = null,
            )
        }
    }

    @Test
    fun `un ADMINISTRADOR sin centro de acopio asignado se construye correctamente`() {
        Usuario(
            id = "ADM-01",
            nombreUsuario = "admin",
            contrasena = contrasena,
            rol = RolUsuario.ADMINISTRADOR,
            nombreCompleto = "Elena Vargas",
            centroAcopioId = null,
        )
    }

    @Test
    fun `un ADMINISTRADOR con un centro de acopio unico asignado lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Usuario(
                id = "ADM-01",
                nombreUsuario = "admin",
                contrasena = contrasena,
                rol = RolUsuario.ADMINISTRADOR,
                nombreCompleto = "Elena Vargas",
                centroAcopioId = "CA-002",
            )
        }
    }

    @Test
    fun `nombreUsuario vacio o en blanco lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Usuario(
                id = "A-01",
                nombreUsuario = "  ",
                contrasena = contrasena,
                rol = RolUsuario.ACOPIADOR,
                nombreCompleto = "Juan Mamani",
                centroAcopioId = "CA-002",
            )
        }
    }

    @Test
    fun `contrasena hash vacia lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Usuario(
                id = "A-01",
                nombreUsuario = "jmamani",
                contrasena = ContrasenaHash(hash = "", sal = "salt"),
                rol = RolUsuario.ACOPIADOR,
                nombreCompleto = "Juan Mamani",
                centroAcopioId = "CA-002",
            )
        }
    }
}
