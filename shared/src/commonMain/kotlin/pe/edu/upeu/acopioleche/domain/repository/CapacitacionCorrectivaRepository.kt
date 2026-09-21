package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.CapacitacionCorrectiva

interface CapacitacionCorrectivaRepository {
    fun observarPendientes(): Flow<List<CapacitacionCorrectiva>>

    suspend fun registrar(capacitacion: CapacitacionCorrectiva)
}
