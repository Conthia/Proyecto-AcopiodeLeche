package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository

class FakeCentroAcopioRepository : CentroAcopioRepository {

    private val _centros: MutableStateFlow<List<CentroAcopio>> = MutableStateFlow(seed())
    private val centros: StateFlow<List<CentroAcopio>> = _centros.asStateFlow()

    override fun observarCentros(): StateFlow<List<CentroAcopio>> = centros

    override suspend fun guardar(centro: CentroAcopio) {
        _centros.value = _centros.value + centro
    }

    override suspend fun actualizar(centro: CentroAcopio) {
        _centros.value = _centros.value.map { if (it.id == centro.id) centro else it }
    }

    override suspend fun eliminar(id: String) {
        _centros.value = _centros.value.filter { it.id != id }
    }

    private fun seed(): List<CentroAcopio> =
        listOf(
            CentroAcopio(
                id = "CA-001",
                nombre = "Huata Centro",
                ubicacion = "Plaza principal",
                capacidadLitrosDia = 450.0,
                activo = true,
                capacidadTanqueLitros = 600.0,
            ),
            CentroAcopio(
                id = "CA-002",
                nombre = "Coyme",
                ubicacion = "Local comunal",
                capacidadLitrosDia = 320.0,
                activo = true,
                capacidadTanqueLitros = 300.0,
            ),
            CentroAcopio(
                id = "CA-003",
                nombre = "Pallalla",
                ubicacion = "Módulo asociativo",
                capacidadLitrosDia = 250.0,
                activo = true,
                capacidadTanqueLitros = 400.0,
            ),
        )
}
