package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.Entrega

interface EntregaRepository {
    fun observarEntregasDeHoy(): Flow<List<Entrega>>

    /** Historial completo de un proveedor (RF-08), no solo hoy — habilitado por la persistencia de la Fase 2. */
    fun observarEntregasDe(proveedorId: String): Flow<List<Entrega>>

    /** Todas las entregas de todos los proveedores (RF-27: liquidaciones semanales por proveedor). */
    fun observarTodasLasEntregas(): Flow<List<Entrega>>

    fun observarPendientesDeSincronizar(): Flow<List<Entrega>>

    /**
     * Volumen total (litros) por día de la semana calendario actual (lunes a domingo, índice 0 =
     * lunes), con 0.0 en los días sin entregas registradas (incluidos los días futuros de la
     * semana en curso, que todavía no tienen entregas).
     */
    fun observarVolumenUltimaSemana(): Flow<List<Double>>

    suspend fun registrar(entrega: Entrega)

    suspend fun buscarPorId(id: String): Entrega?

    suspend fun eliminar(id: String)

    suspend fun sincronizarPendientes(): Int
}
