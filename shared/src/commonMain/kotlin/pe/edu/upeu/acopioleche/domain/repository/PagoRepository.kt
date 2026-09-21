package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.PagoEntrega

interface PagoRepository {
    fun observarPagos(): Flow<List<PagoEntrega>>

    fun observarPagosDe(proveedorId: String): Flow<List<PagoEntrega>>

    suspend fun registrarPago(pago: PagoEntrega)
}
