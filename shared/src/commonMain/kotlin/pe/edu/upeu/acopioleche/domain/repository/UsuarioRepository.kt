package pe.edu.upeu.acopioleche.domain.repository

import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.Usuario

interface UsuarioRepository {
    fun observarUsuarios(): Flow<List<Usuario>>

    suspend fun buscarPorNombreUsuario(nombreUsuario: String): Usuario?

    suspend fun registrarIntentoFallido(usuarioId: String, momento: Instant)

    suspend fun registrarLoginExitoso(usuarioId: String)

    suspend fun reiniciarIntentos(usuarioId: String)
}
