package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.sync.CambioPendiente

interface ProveedorRepository {
    fun observarProveedores(): Flow<List<Proveedor>>

    fun observarProveedoresDeRuta(acopiadorId: String): Flow<List<Proveedor>>

    suspend fun guardar(proveedor: Proveedor)

    suspend fun actualizar(proveedor: Proveedor)

    suspend fun eliminar(id: String)

    // --- Sincronización offline-first con el backend Laravel (piloto Fase 1) ---
    // Ver data/sync/ProveedorSyncManager.kt para el flujo completo de subida/bajada.

    /** Cambios locales (alta/edición/baja) que todavía no viajaron al servidor. */
    fun observarPendientesDeSincronizar(): Flow<List<CambioPendiente<Proveedor>>>

    /** El servidor confirmó un CREAR/ACTUALIZAR: limpia la marca de pendiente. */
    suspend fun marcarSincronizado(id: String, actualizadoEn: LocalDateTime)

    /** El servidor confirmó el ELIMINAR: recién ahí se borra la fila local de verdad. */
    suspend fun confirmarEliminacionRemota(id: String)

    /**
     * Aplica un alta/edición que llegó del servidor. Si el registro local tiene cambios propios
     * sin enviar, resuelve el conflicto con "el más reciente gana" comparando [actualizadoEn]
     * contra la marca de tiempo local.
     */
    suspend fun aplicarCambioRemoto(proveedor: Proveedor, actualizadoEn: LocalDateTime)

    /** Igual que [aplicarCambioRemoto] pero para una baja notificada por el servidor. */
    suspend fun aplicarEliminacionRemota(id: String, actualizadoEn: LocalDateTime)
}
