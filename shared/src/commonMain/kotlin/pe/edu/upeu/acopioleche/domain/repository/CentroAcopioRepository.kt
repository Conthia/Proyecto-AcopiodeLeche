package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio

interface CentroAcopioRepository {
    fun observarCentros(): Flow<List<CentroAcopio>>

    suspend fun guardar(centro: CentroAcopio)

    suspend fun actualizar(centro: CentroAcopio)

    suspend fun eliminar(id: String)
}
