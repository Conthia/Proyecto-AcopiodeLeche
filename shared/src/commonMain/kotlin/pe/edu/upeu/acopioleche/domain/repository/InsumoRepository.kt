package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo

interface InsumoRepository {
    fun observarInsumos(): Flow<List<InsumoLacteo>>

    suspend fun guardar(insumo: InsumoLacteo)

    suspend fun eliminar(id: String)
}
