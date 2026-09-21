package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository

class FakeLiquidacionRepository : LiquidacionRepository {

    private val _liquidaciones: MutableStateFlow<List<Liquidacion>> = MutableStateFlow(emptyList())

    private val liquidaciones: StateFlow<List<Liquidacion>> = _liquidaciones.asStateFlow()

    override fun observarLiquidacionesDe(proveedorId: String): Flow<List<Liquidacion>> =
        liquidaciones.map { lista -> lista.filter { it.proveedorId == proveedorId } }

    override fun observarTodas(): Flow<List<Liquidacion>> = liquidaciones

    override suspend fun buscar(proveedorId: String, semanaInicio: LocalDate): Liquidacion? =
        _liquidaciones.value.find { it.proveedorId == proveedorId && it.semanaInicio == semanaInicio }

    override suspend fun registrar(liquidacion: Liquidacion) {
        _liquidaciones.value = _liquidaciones.value
            .filterNot { it.proveedorId == liquidacion.proveedorId && it.semanaInicio == liquidacion.semanaInicio }
            .plus(liquidacion)
    }
}
