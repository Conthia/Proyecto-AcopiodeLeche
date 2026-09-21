package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.EquipoCampo

interface EquipoCampoRepository {
    fun observarEquipos(): Flow<List<EquipoCampo>>
}
