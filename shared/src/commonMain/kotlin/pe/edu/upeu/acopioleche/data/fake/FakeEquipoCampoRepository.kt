package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.acopioleche.domain.model.EquipoCampo
import pe.edu.upeu.acopioleche.domain.model.EstadoConexion
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository

class FakeEquipoCampoRepository : EquipoCampoRepository {

    private val equipos: StateFlow<List<EquipoCampo>> = MutableStateFlow(seed()).asStateFlow()

    override fun observarEquipos(): StateFlow<List<EquipoCampo>> = equipos

    private fun seed(): List<EquipoCampo> =
        listOf(
            EquipoCampo(
                id = "EQ-01",
                nombre = "Tablet acopio · Huata Centro",
                tipo = "Tablet",
                centroAcopioId = "CA-001",
                estadoConexion = EstadoConexion.AlDia,
            ),
            EquipoCampo(
                id = "EQ-02",
                nombre = "Celular ruta Coyme",
                tipo = "Celular",
                centroAcopioId = "CA-002",
                estadoConexion = EstadoConexion.Pendiente(diasSinSincronizar = 1),
            ),
            EquipoCampo(
                id = "EQ-03",
                nombre = "Tablet acopio · Pallalla",
                tipo = "Tablet",
                centroAcopioId = "CA-003",
                estadoConexion = EstadoConexion.SinConexion(dias = 2),
            ),
        )
}
