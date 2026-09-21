package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.domain.model.CapacitacionCorrectiva
import pe.edu.upeu.acopioleche.domain.repository.CapacitacionCorrectivaRepository

class FakeCapacitacionCorrectivaRepository : CapacitacionCorrectivaRepository {

    private val _capacitaciones: MutableStateFlow<List<CapacitacionCorrectiva>> = MutableStateFlow(emptyList())

    private val capacitaciones: StateFlow<List<CapacitacionCorrectiva>> = _capacitaciones.asStateFlow()

    override fun observarPendientes(): Flow<List<CapacitacionCorrectiva>> =
        capacitaciones.map { lista -> lista.filterNot { it.atendida } }

    override suspend fun registrar(capacitacion: CapacitacionCorrectiva) {
        _capacitaciones.value = listOf(capacitacion) + _capacitaciones.value
    }
}
