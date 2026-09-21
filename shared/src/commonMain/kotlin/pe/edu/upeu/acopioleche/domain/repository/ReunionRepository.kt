package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.Reunion

interface ReunionRepository {
    fun observarReuniones(): Flow<List<Reunion>>

    suspend fun guardar(reunion: Reunion)

    suspend fun actualizar(reunion: Reunion)

    suspend fun eliminar(id: String)
}
