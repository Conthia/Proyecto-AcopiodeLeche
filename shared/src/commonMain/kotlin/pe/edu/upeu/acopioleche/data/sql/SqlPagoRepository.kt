package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.PagoEntity
import pe.edu.upeu.acopioleche.domain.model.PagoEntrega
import pe.edu.upeu.acopioleche.domain.repository.PagoRepository

class SqlPagoRepository(
    database: AcopioLecheDatabase,
) : PagoRepository {

    private val queries = database.pagoQueries

    override fun observarPagos(): Flow<List<PagoEntrega>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarPagosDe(proveedorId: String): Flow<List<PagoEntrega>> =
        queries.selectPorProveedor(proveedorId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun registrarPago(pago: PagoEntrega) {
        queries.insertar(
            id = pago.id,
            liquidacionId = pago.liquidacionId,
            proveedorId = pago.proveedorId,
            monto = pago.monto,
            fechaHora = pago.fechaHora.toString(),
            encargadoId = pago.encargadoId,
            metodoPago = pago.metodoPago,
            notas = pago.notas,
        )
    }

    private fun PagoEntity.toDomain(): PagoEntrega =
        PagoEntrega(
            id = id,
            liquidacionId = liquidacionId,
            proveedorId = proveedorId,
            monto = monto,
            fechaHora = LocalDateTime.parse(fechaHora),
            encargadoId = encargadoId,
            metodoPago = metodoPago,
            notas = notas,
        )
}
