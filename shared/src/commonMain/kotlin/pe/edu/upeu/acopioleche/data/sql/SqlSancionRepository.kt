package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.SancionAplicadaEntity
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository

/** `SancionAplicada` (sealed interface) se aplana en columnas — ver `SancionAplicada.sq`. */
class SqlSancionRepository(
    database: AcopioLecheDatabase,
) : SancionRepository {

    private val queries = database.sancionAplicadaQueries

    override fun observarSancionesDe(proveedorId: String): Flow<List<SancionAplicada>> =
        queries.selectDeProveedor(proveedorId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarTodas(): Flow<List<SancionAplicada>> =
        queries.selectTodas().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun registrar(sancion: SancionAplicada) {
        when (sancion) {
            is SancionAplicada.ReduccionPrecioSemanal -> queries.insertar(
                id = sancion.id,
                proveedorId = sancion.proveedorId,
                fecha = sancion.fecha.toString(),
                tipo = "REDUCCION_PRECIO_SEMANAL",
                semanaInicio = sancion.semanaInicio.toString(),
                montoMulta = null,
            )
            is SancionAplicada.RetiroPorAdulteracion -> queries.insertar(
                id = sancion.id,
                proveedorId = sancion.proveedorId,
                fecha = sancion.fecha.toString(),
                tipo = "RETIRO_POR_ADULTERACION",
                semanaInicio = null,
                montoMulta = sancion.montoMulta,
            )
        }
    }

    private fun SancionAplicadaEntity.toDomain(): SancionAplicada =
        when (tipo) {
            "REDUCCION_PRECIO_SEMANAL" -> SancionAplicada.ReduccionPrecioSemanal(
                id = id,
                proveedorId = proveedorId,
                fecha = LocalDate.parse(fecha),
                semanaInicio = LocalDate.parse(requireNotNull(semanaInicio)),
            )
            "RETIRO_POR_ADULTERACION" -> SancionAplicada.RetiroPorAdulteracion(
                id = id,
                proveedorId = proveedorId,
                fecha = LocalDate.parse(fecha),
                montoMulta = requireNotNull(montoMulta),
            )
            else -> error("tipo desconocido en SancionAplicadaEntity: $tipo")
        }
}
