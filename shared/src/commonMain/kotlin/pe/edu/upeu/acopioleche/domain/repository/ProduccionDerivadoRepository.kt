package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado

interface ProduccionDerivadoRepository {
    fun observarProduccion(): Flow<List<ProduccionDerivado>>

    suspend fun guardar(produccion: ProduccionDerivado)

    suspend fun eliminar(id: String)
}
