package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.domain.model.PagoEntrega
import pe.edu.upeu.acopioleche.domain.repository.PagoRepository

class FakePagoRepository : PagoRepository {

    private val _pagos: MutableStateFlow<List<PagoEntrega>> = MutableStateFlow(emptyList())
    private val pagos: StateFlow<List<PagoEntrega>> = _pagos.asStateFlow()

    override fun observarPagos(): StateFlow<List<PagoEntrega>> = pagos

    override fun observarPagosDe(proveedorId: String): Flow<List<PagoEntrega>> =
        pagos.map { lista -> lista.filter { it.proveedorId == proveedorId } }

    override suspend fun registrarPago(pago: PagoEntrega) {
        _pagos.value = listOf(pago) + _pagos.value
    }
}
