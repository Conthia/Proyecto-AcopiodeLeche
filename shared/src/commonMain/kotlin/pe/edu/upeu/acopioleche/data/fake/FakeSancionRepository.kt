package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository

class FakeSancionRepository : SancionRepository {

    private val _sanciones: MutableStateFlow<List<SancionAplicada>> = MutableStateFlow(emptyList())

    private val sanciones: StateFlow<List<SancionAplicada>> = _sanciones.asStateFlow()

    override fun observarSancionesDe(proveedorId: String): Flow<List<SancionAplicada>> =
        sanciones.map { lista -> lista.filter { it.proveedorId == proveedorId } }

    override fun observarTodas(): Flow<List<SancionAplicada>> = sanciones

    override suspend fun registrar(sancion: SancionAplicada) {
        _sanciones.value = listOf(sancion) + _sanciones.value
    }
}
