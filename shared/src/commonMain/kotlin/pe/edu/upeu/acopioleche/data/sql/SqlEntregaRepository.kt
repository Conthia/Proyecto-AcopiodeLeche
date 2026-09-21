package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.EntregaEntity
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository

class SqlEntregaRepository(
    private val database: AcopioLecheDatabase,
) : EntregaRepository {

    private val queries = database.entregaQueries

    override fun observarEntregasDeHoy(): Flow<List<Entrega>> {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return queries.selectDeFecha(hoy.toString()).asFlow().mapToList(Dispatchers.Default)
            .map { filas -> filas.map { it.toDomain() } }
    }

    override fun observarEntregasDe(proveedorId: String): Flow<List<Entrega>> =
        queries.selectPorProveedor(proveedorId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarTodasLasEntregas(): Flow<List<Entrega>> =
        queries.selectTodas().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarPendientesDeSincronizar(): Flow<List<Entrega>> =
        queries.selectPendientes().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarVolumenUltimaSemana(): Flow<List<Double>> =
        MutableStateFlow(listOf(124.0, 168.0, 152.0, 191.0, 176.0, 204.0, 188.0)).asStateFlow()

    override suspend fun registrar(entrega: Entrega) {
        insertar(entrega, sincronizada = false)
    }

    override suspend fun buscarPorId(id: String): Entrega? =
        queries.selectPorId(id).executeAsOneOrNull()?.toDomain()

    override suspend fun eliminar(id: String) {
        queries.eliminar(id)
    }

    override suspend fun sincronizarPendientes(): Int {
        val pendientes = queries.selectPendientes().executeAsList().size
        database.transaction {
            queries.aceptarPendientes()
            queries.marcarTodoSincronizado()
        }
        return pendientes
    }

    private fun insertar(entrega: Entrega, sincronizada: Boolean) {
        val cols = entrega.estado.aColumnas()
        queries.insertar(
            id = entrega.id,
            proveedorId = entrega.proveedorId,
            acopiadorId = entrega.acopiadorId,
            centroAcopioId = entrega.centroAcopioId,
            fecha = entrega.fecha.toString(),
            turno = entrega.turno.name,
            volumenLitros = entrega.volumenLitros,
            cantidadPorongos = entrega.cantidadPorongos.toLong(),
            estadoTipo = cols.tipo,
            estadoMotivoRechazo = cols.motivoRechazo,
            estadoTransportistaId = cols.transportistaId,
            estadoHoraSalida = cols.horaSalida,
            estadoLiquidacionId = cols.liquidacionId,
            sincronizada = if (sincronizada) 1L else 0L,
            volumenPlantaLitros = entrega.volumenPlantaLitros,
            estadoMotivoTexto = cols.motivoTexto,
            estadoCanceladaPor = cols.canceladaPor,
            estadoFechaHoraCancelacion = cols.fechaHoraCancelacion,
        )
    }

    private data class ColumnasEstado(
        val tipo: String,
        val motivoRechazo: String?,
        val transportistaId: String?,
        val horaSalida: String?,
        val liquidacionId: String?,
        val motivoTexto: String?,
        val canceladaPor: String?,
        val fechaHoraCancelacion: String?,
    )

    private fun EstadoEntrega.aColumnas(): ColumnasEstado =
        when (this) {
            is EstadoEntrega.Pendiente -> ColumnasEstado("PENDIENTE", null, null, null, null, null, null, null)
            is EstadoEntrega.Aceptada -> ColumnasEstado("ACEPTADA", null, null, null, null, null, null, null)
            is EstadoEntrega.Rechazada -> ColumnasEstado("RECHAZADA", motivo.name, null, null, null, null, null, null)
            is EstadoEntrega.NoRecogida -> ColumnasEstado("NO_RECOGIDA", null, null, null, null, motivo, null, null)
            is EstadoEntrega.EnTransitoAPlanta -> ColumnasEstado("EN_TRANSITO", null, transportistaId, horaSalida.toString(), null, null, null, null)
            is EstadoEntrega.Liquidada -> ColumnasEstado("LIQUIDADA", null, null, null, liquidacionId, null, null, null)
            is EstadoEntrega.Cancelada -> ColumnasEstado("CANCELADA", null, null, null, null, motivo, canceladaPor, fechaHora.toString())
        }

    private fun EntregaEntity.estadoDeColumnas(): EstadoEntrega =
        when (estadoTipo) {
            "PENDIENTE" -> EstadoEntrega.Pendiente
            "ACEPTADA" -> EstadoEntrega.Aceptada
            "RECHAZADA" -> EstadoEntrega.Rechazada(motivo = MotivoRechazo.valueOf(requireNotNull(estadoMotivoRechazo)))
            "NO_RECOGIDA" -> EstadoEntrega.NoRecogida(motivo = estadoMotivoTexto ?: "Visita sin acopio")
            "EN_TRANSITO" -> EstadoEntrega.EnTransitoAPlanta(
                transportistaId = requireNotNull(estadoTransportistaId),
                horaSalida = LocalDateTime.parse(requireNotNull(estadoHoraSalida)),
            )
            "LIQUIDADA" -> EstadoEntrega.Liquidada(liquidacionId = requireNotNull(estadoLiquidacionId))
            "CANCELADA" -> EstadoEntrega.Cancelada(
                motivo = estadoMotivoTexto ?: "Cancelada por error de registro",
                canceladaPor = estadoCanceladaPor ?: "desconocido",
                fechaHora = LocalDateTime.parse(requireNotNull(estadoFechaHoraCancelacion)),
            )
            else -> error("estadoTipo desconocido en EntregaEntity: $estadoTipo")
        }

    private fun EntregaEntity.toDomain(): Entrega =
        Entrega(
            id = id,
            proveedorId = proveedorId,
            acopiadorId = acopiadorId,
            centroAcopioId = centroAcopioId,
            fecha = LocalDate.parse(fecha),
            turno = Turno.valueOf(turno),
            volumenLitros = volumenLitros,
            estado = estadoDeColumnas(),
            cantidadPorongos = cantidadPorongos.toInt(),
            volumenPlantaLitros = volumenPlantaLitros,
        )

}
