package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.domain.model.EstadoParada
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.ParadaRuta
import pe.edu.upeu.acopioleche.domain.model.ResultadoEliminacionRuta
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SqlRutaRepository(
    private val database: AcopioLecheDatabase,
) : RutaRepository {

    private val queries = database.rutaQueries

    override fun observarRutaDelDia(acopiadorId: String, fecha: LocalDate): Flow<RutaAcopio?> =
        queries.selectRutaDelDia(acopiadorId, fecha.toString()).asFlow().mapToOneOrNull(Dispatchers.Default)
            .flatMapLatest { entidad ->
                if (entidad == null) {
                    flowOf(null)
                } else {
                    queries.selectParadasDeRuta(entidad.id).asFlow().mapToList(Dispatchers.Default).map { paradas ->
                        RutaAcopio(
                            id = entidad.id,
                            nombre = entidad.nombre,
                            acopiadorId = entidad.acopiadorId,
                            centroSectorId = entidad.centroSectorId,
                            fecha = LocalDate.parse(entidad.fecha),
                            paradas = paradas.map { ParadaRuta(it.orden.toInt(), it.proveedorId, EstadoParada.valueOf(it.estadoParada)) },
                            estado = EstadoRuta.valueOf(entidad.estado),
                            volumenDescargadoLitros = entidad.volumenDescargadoLitros,
                            fechaHoraCierre = entidad.fechaHoraCierre?.let { LocalDateTime.parse(it) },
                        )
                    }
                }
            }

    override fun observarTodasRutas(): Flow<List<RutaAcopio>> =
        queries.selectTodasRutas().asFlow().mapToList(Dispatchers.Default).flatMapLatest { entidades ->
            if (entidades.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(entidades.map { e ->
                    queries.selectParadasDeRuta(e.id).asFlow().mapToList(Dispatchers.Default).map { paradas ->
                        RutaAcopio(
                            id = e.id,
                            nombre = e.nombre,
                            acopiadorId = e.acopiadorId,
                            centroSectorId = e.centroSectorId,
                            fecha = LocalDate.parse(e.fecha),
                            paradas = paradas.map { ParadaRuta(it.orden.toInt(), it.proveedorId, EstadoParada.valueOf(it.estadoParada)) },
                            estado = EstadoRuta.valueOf(e.estado),
                            volumenDescargadoLitros = e.volumenDescargadoLitros,
                            fechaHoraCierre = e.fechaHoraCierre?.let { LocalDateTime.parse(it) },
                        )
                    }
                }) { it.toList() }
            }
        }

    override suspend fun asignarRuta(ruta: RutaAcopio) {
        eliminarOtrasRutasDelMismoDia(ruta)
        guardarSync(ruta)
    }

    override suspend fun actualizarRuta(ruta: RutaAcopio) {
        // La edición puede cambiar el acopiador o la fecha (RF-15): si el nuevo destino ya
        // tenía otra ruta asignada, se reemplaza igual que en asignarRuta — de lo contrario
        // quedarían dos rutas candidatas para selectRutaDelDia.
        eliminarOtrasRutasDelMismoDia(ruta)
        guardarSync(ruta)
    }

    /** Un acopiador tiene una única "ruta del día" (RF-15): si ya tenía otra asignada para esta
     * misma fecha, se reemplaza en vez de dejarla huérfana. De lo contrario selectRutaDelDia
     * queda con más de una fila candidata y, al desempatar por id, puede devolver la ruta vieja
     * en vez de la recién asignada/editada por el admin. */
    private fun eliminarOtrasRutasDelMismoDia(ruta: RutaAcopio) {
        database.transaction {
            queries.selectRutasPorAcopiadorYFecha(ruta.acopiadorId, ruta.fecha.toString())
                .executeAsList()
                .filter { it.id != ruta.id }
                .forEach { anterior ->
                    queries.eliminarParadasDeRuta(anterior.id)
                    queries.eliminarRuta(anterior.id)
                }
        }
    }

    override suspend fun cerrarRuta(rutaId: String, volumenTotalDescargado: Double): Boolean {
        val existe = queries.selectPorId(rutaId).executeAsOneOrNull() ?: return false
        val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        queries.actualizarEstadoRuta(
            estado = EstadoRuta.FINALIZADA.name,
            volumenDescargadoLitros = volumenTotalDescargado,
            fechaHoraCierre = ahora.toString(),
            id = rutaId,
        )
        return true
    }

    override suspend fun eliminarRuta(rutaId: String): ResultadoEliminacionRuta {
        queries.selectPorId(rutaId).executeAsOneOrNull() ?: return ResultadoEliminacionRuta.NoEncontrada
        val paradasConAvance = queries.selectParadasDeRuta(rutaId).executeAsList()
            .count { it.estadoParada != EstadoParada.PENDIENTE.name }
        if (paradasConAvance > 0) {
            return ResultadoEliminacionRuta.TieneAvance(paradasConAvance = paradasConAvance)
        }
        database.transaction {
            queries.eliminarParadasDeRuta(rutaId)
            queries.eliminarRuta(rutaId)
        }
        return ResultadoEliminacionRuta.Eliminada
    }

    private fun guardarSync(ruta: RutaAcopio) {
        database.transaction {
            queries.insertarRuta(
                id = ruta.id,
                nombre = ruta.nombre,
                acopiadorId = ruta.acopiadorId,
                centroSectorId = ruta.centroSectorId,
                fecha = ruta.fecha.toString(),
                estado = ruta.estado.name,
                volumenDescargadoLitros = ruta.volumenDescargadoLitros,
                fechaHoraCierre = ruta.fechaHoraCierre?.toString(),
            )
            // Limpiar antes de reinsertar: si una edición quita una parada, insertarParada (upsert)
            // no la borraría por sí sola y quedaría huérfana apuntando a una ruta que ya no la lista.
            queries.eliminarParadasDeRuta(ruta.id)
            ruta.paradas.forEach { parada ->
                queries.insertarParada(
                    rutaId = ruta.id,
                    orden = parada.orden.toLong(),
                    proveedorId = parada.proveedorId,
                    estadoParada = parada.estadoParada.name,
                )
            }
        }
    }
}
