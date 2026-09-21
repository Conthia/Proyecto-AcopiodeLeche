package pe.edu.upeu.acopioleche.data.fake

import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.domain.model.Usuario
import pe.edu.upeu.acopioleche.domain.repository.UsuarioRepository
import pe.edu.upeu.acopioleche.domain.service.PasswordHasher

/**
 * Cuentas de demostración en memoria. Las contraseñas de [seed] nunca se guardan en texto plano:
 * pasan por [PasswordHasher] antes de construir cada [Usuario], igual que exigiría un backend
 * real (RNF-02). Los IDs "A-01"/"CA-002" coinciden a propósito con los que ya usan
 * `FakeProveedorRepository` y `FakeEntregaRepository`, para no romper esos datos de ejemplo.
 */
class FakeUsuarioRepository : UsuarioRepository {

    private val _usuarios: MutableStateFlow<List<Usuario>> = MutableStateFlow(seed())
    private val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    override fun observarUsuarios(): StateFlow<List<Usuario>> = usuarios

    override suspend fun buscarPorNombreUsuario(nombreUsuario: String): Usuario? =
        usuarios.value.find { it.nombreUsuario.equals(nombreUsuario, ignoreCase = true) }

    override suspend fun registrarIntentoFallido(usuarioId: String, momento: Instant) {
        actualizar(usuarioId = usuarioId) { usuario ->
            usuario.copy(intentosFallidos = usuario.intentosFallidos + 1, ultimoIntentoFallidoEn = momento)
        }
    }

    override suspend fun registrarLoginExitoso(usuarioId: String) {
        actualizar(usuarioId = usuarioId) { usuario ->
            usuario.copy(intentosFallidos = 0, ultimoIntentoFallidoEn = null)
        }
    }

    override suspend fun reiniciarIntentos(usuarioId: String) {
        actualizar(usuarioId = usuarioId) { usuario ->
            usuario.copy(intentosFallidos = 0, ultimoIntentoFallidoEn = null)
        }
    }

    private fun actualizar(usuarioId: String, transformar: (Usuario) -> Usuario) {
        _usuarios.value = _usuarios.value.map { usuario -> if (usuario.id == usuarioId) transformar(usuario) else usuario }
    }

    private fun seed(): List<Usuario> =
        listOf(
            Usuario(
                id = "A-01",
                nombreUsuario = "jmamani",
                contrasena = PasswordHasher.hash(contrasenaPlano = "Acopio2026"),
                rol = RolUsuario.ACOPIADOR,
                nombreCompleto = "Juan Mamani",
                centroAcopioId = "CA-002",
            ),
            Usuario(
                id = "ADM-01",
                nombreUsuario = "admin",
                contrasena = PasswordHasher.hash(contrasenaPlano = "Admin2026"),
                rol = RolUsuario.ADMINISTRADOR,
                nombreCompleto = "Elena Vargas",
                centroAcopioId = null,
            ),
            Usuario(
                id = "PAGOS-01",
                nombreUsuario = "pagos",
                contrasena = PasswordHasher.hash(contrasenaPlano = "Pagos2026"),
                rol = RolUsuario.ENCARGADO_PAGOS,
                nombreCompleto = "Carlos Finanzas",
                centroAcopioId = null,
            ),
            Usuario(
                id = "PROD-014",
                nombreUsuario = "rquispe",
                contrasena = PasswordHasher.hash(contrasenaPlano = "Productor2026"),
                rol = RolUsuario.PRODUCTOR,
                nombreCompleto = "Rosa Quispe Mamani",
                centroAcopioId = null,
                proveedorId = "P-014",
            ),
            Usuario(
                id = "PROD-LACT-01",
                nombreUsuario = "lacteos",
                contrasena = PasswordHasher.hash(contrasenaPlano = "Lacteos2026"),
                rol = RolUsuario.PRODUCTOR_LACTEOS,
                nombreCompleto = "Asoc. Procesadora Huata",
                centroAcopioId = null,
            ),
        )
}
