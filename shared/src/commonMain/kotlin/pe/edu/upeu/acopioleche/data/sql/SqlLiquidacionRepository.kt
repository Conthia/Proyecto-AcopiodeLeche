package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.LiquidacionEntity
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository

class SqlLiquidacionRepository(
    database: AcopioLecheDatabase,
) : LiquidacionRepository {

    private val queries = database.liquidacionQueries

    override fun observarLiquidacionesDe(proveedorId: String): Flow<List<Liquidacion>> =
        queries.selectDeProveedor(proveedorId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarTodas(): Flow<List<Liquidacion>> =
        queries.selectTodas().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun buscar(proveedorId: String, semanaInicio: LocalDate): Liquidacion? =
        queries.selectDeProveedorYSemana(proveedorId, semanaInicio.toString()).executeAsOneOrNull()?.toDomain()

    override suspend fun registrar(liquidacion: Liquidacion) {
        queries.insertar(
            id = liquidacion.id,
            proveedorId = liquidacion.proveedorId,
            semanaInicio = liquidacion.semanaInicio.toString(),
            litrosAceptados = liquidacion.litrosAceptados,
            montoBruto = liquidacion.montoBruto,
            montoFinal = liquidacion.montoFinal,
            tieneSancionPendienteDeMonto = if (liquidacion.tieneSancionPendienteDeMonto) 1L else 0L,
            fechaPago = liquidacion.fechaPago.toString(),
            generadaAutomaticamente = if (liquidacion.generadaAutomaticamente) 1L else 0L,
        )
    }

    private fun LiquidacionEntity.toDomain(): Liquidacion =
        Liquidacion(
            id = id,
            proveedorId = proveedorId,
            semanaInicio = LocalDate.parse(semanaInicio),
            litrosAceptados = litrosAceptados,
            montoBruto = montoBruto,
            montoFinal = montoFinal,
            tieneSancionPendienteDeMonto = tieneSancionPendienteDeMonto != 0L,
            fechaPago = LocalDate.parse(fechaPago),
            generadaAutomaticamente = generadaAutomaticamente != 0L,
        )
}
