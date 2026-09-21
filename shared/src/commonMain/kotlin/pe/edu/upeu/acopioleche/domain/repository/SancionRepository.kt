package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada

interface SancionRepository {
    fun observarSancionesDe(proveedorId: String): Flow<List<SancionAplicada>>

    fun observarTodas(): Flow<List<SancionAplicada>>

    suspend fun registrar(sancion: SancionAplicada)
}
