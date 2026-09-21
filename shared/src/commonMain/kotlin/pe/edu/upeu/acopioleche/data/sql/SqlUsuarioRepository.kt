package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.UsuarioEntity
import pe.edu.upeu.acopioleche.domain.model.ContrasenaHash
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.domain.model.Usuario
import pe.edu.upeu.acopioleche.domain.repository.UsuarioRepository
import pe.edu.upeu.acopioleche.domain.service.PasswordHasher

/**
 * Persiste las cuentas y el estado de bloqueo de RF-01/RNF-02 (antes solo vivían en memoria en
 * `FakeUsuarioRepository`, así que se perdían al cerrar la app — incluidos los intentos
 * fallidos, lo que en la práctica anulaba el bloqueo de 10 minutos en cuanto se reiniciaba).
 */
class SqlUsuarioRepository(
    database: AcopioLecheDatabase,
) : UsuarioRepository {

    private val queries = database.usuarioQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { insertar(it) }
        }
    }

    override fun observarUsuarios(): Flow<List<Usuario>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun buscarPorNombreUsuario(nombreUsuario: String): Usuario? =
        queries.selectPorNombreUsuario(nombreUsuario).executeAsOneOrNull()?.toDomain()

    override suspend fun registrarIntentoFallido(usuarioId: String, momento: Instant) {
        val actual = queries.selectPorId(usuarioId).executeAsOneOrNull() ?: return
        queries.actualizarIntentoFallido(
            intentosFallidos = actual.intentosFallidos + 1,
            ultimoIntentoFallidoEn = momento.toString(),
            id = usuarioId,
        )
    }

    override suspend fun registrarLoginExitoso(usuarioId: String) {
        queries.reiniciarIntentos(usuarioId)
    }

    override suspend fun reiniciarIntentos(usuarioId: String) {
        queries.reiniciarIntentos(usuarioId)
    }

    private fun insertar(usuario: Usuario) {
        queries.insertar(
            id = usuario.id,
            nombreUsuario = usuario.nombreUsuario,
            contrasenaHash = usuario.contrasena.hash,
            contrasenaSal = usuario.contrasena.sal,
            rol = usuario.rol.name,
            nombreCompleto = usuario.nombreCompleto,
            centroAcopioId = usuario.centroAcopioId,
            intentosFallidos = usuario.intentosFallidos.toLong(),
            ultimoIntentoFallidoEn = usuario.ultimoIntentoFallidoEn?.toString(),
            proveedorId = usuario.proveedorId,
        )
    }

    private fun UsuarioEntity.toDomain(): Usuario =
        Usuario(
            id = id,
            nombreUsuario = nombreUsuario,
            contrasena = ContrasenaHash(hash = contrasenaHash, sal = contrasenaSal),
            rol = RolUsuario.valueOf(rol),
            nombreCompleto = nombreCompleto,
            centroAcopioId = centroAcopioId,
            proveedorId = proveedorId,
            intentosFallidos = intentosFallidos.toInt(),
            ultimoIntentoFallidoEn = ultimoIntentoFallidoEn?.let { Instant.parse(it) },
        )

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
