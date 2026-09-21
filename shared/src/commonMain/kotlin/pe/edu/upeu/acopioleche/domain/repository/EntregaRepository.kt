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

    /** Volumen total (litros) de cada uno de los últimos 7 días, de lunes a domingo. */
    fun observarVolumenUltimaSemana(): Flow<List<Double>>

    suspend fun registrar(entrega: Entrega)

    suspend fun buscarPorId(id: String): Entrega?

    suspend fun eliminar(id: String)

    suspend fun sincronizarPendientes(): Int
}
