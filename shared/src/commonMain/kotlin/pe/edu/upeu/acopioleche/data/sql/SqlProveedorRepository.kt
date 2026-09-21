package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.ProveedorEntity
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Sector
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.sync.AccionPendiente
import pe.edu.upeu.acopioleche.domain.sync.CambioPendiente

class SqlProveedorRepository(
    database: AcopioLecheDatabase,
) : ProveedorRepository {

    private val queries = database.proveedorQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            // Datos de ejemplo: se insertan ya "sincronizados" (synced = 1) para que el primer
            // ciclo de sincronización no intente subirlos al backend como si fueran altas
            // reales del usuario.
            seed().forEach { proveedor -> insertarFila(proveedor, accion = null, synced = true, actualizadoEn = FECHA_SEED) }
            RUTAS.forEach { (acopiadorId, proveedorIds) ->
                proveedorIds.forEach { proveedorId -> queries.insertarRuta(acopiadorId = acopiadorId, proveedorId = proveedorId) }
            }
        }
    }

    override fun observarProveedores(): Flow<List<Proveedor>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override fun observarProveedoresDeRuta(acopiadorId: String): Flow<List<Proveedor>> =
        queries.selectDeRuta(acopiadorId).asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun guardar(proveedor: Proveedor) {
        upsertPendiente(proveedor)
    }

    override suspend fun actualizar(proveedor: Proveedor) {
        upsertPendiente(proveedor)
    }

    override suspend fun eliminar(id: String) {
        val fila = queries.selectPorId(id).executeAsOneOrNull() ?: return
        if (fila.pendingAction == AccionPendiente.CREAR.name) {
            // Nunca viajó al servidor: no hay nada que avisarle, se borra de una vez.
            queries.eliminar(id)
        } else {
            queries.marcarPendienteEliminacion(id = id, updatedAt = ahora().toString())
        }
    }

    override fun observarPendientesDeSincronizar(): Flow<List<CambioPendiente<Proveedor>>> =
        queries.selectPendientes().asFlow().mapToList(Dispatchers.Default).map { filas ->
            filas.map { fila ->
                CambioPendiente(
                    entidad = fila.toDomain(),
                    accion = AccionPendiente.valueOf(requireNotNull(fila.pendingAction) { "Fila synced=0 sin pendingAction: ${fila.id}" }),
                    actualizadoEn = LocalDateTime.parse(fila.updatedAt),
                )
            }
        }

    override suspend fun marcarSincronizado(id: String, actualizadoEn: LocalDateTime) {
        queries.marcarSincronizado(updatedAt = actualizadoEn.toString(), id = id)
    }

    override suspend fun confirmarEliminacionRemota(id: String) {
        queries.eliminar(id)
    }

    override suspend fun aplicarCambioRemoto(proveedor: Proveedor, actualizadoEn: LocalDateTime) {
        val local = queries.selectPorId(proveedor.id).executeAsOneOrNull()
        if (local == null || local.synced == 1L) {
            insertarFila(proveedor, accion = null, synced = true, actualizadoEn = actualizadoEn)
            return
        }
        // Hay un cambio local sin enviar todavía: gana el más reciente por marca de tiempo.
        // Si gana el local, no se hace nada acá: se subirá tal cual en el próximo push.
        if (actualizadoEn > LocalDateTime.parse(local.updatedAt)) {
            insertarFila(proveedor, accion = null, synced = true, actualizadoEn = actualizadoEn)
        }
    }

    override suspend fun aplicarEliminacionRemota(id: String, actualizadoEn: LocalDateTime) {
        val local = queries.selectPorId(id).executeAsOneOrNull() ?: return
        if (local.synced == 1L || actualizadoEn > LocalDateTime.parse(local.updatedAt)) {
            queries.eliminar(id)
        }
    }

    /** `guardar`/`actualizar` llegan al mismo lugar: la UI actual no distingue alta de edición. */
    private fun upsertPendiente(proveedor: Proveedor) {
        val filaExistente = queries.selectPorId(proveedor.id).executeAsOneOrNull()
        val accion = when {
            filaExistente == null -> AccionPendiente.CREAR
            filaExistente.pendingAction == AccionPendiente.CREAR.name -> AccionPendiente.CREAR
            else -> AccionPendiente.ACTUALIZAR
        }
        insertarFila(proveedor, accion = accion, synced = false, actualizadoEn = ahora())
    }

    private fun ahora(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    private fun insertarFila(proveedor: Proveedor, accion: AccionPendiente?, synced: Boolean, actualizadoEn: LocalDateTime) {
        queries.insertar(
            id = proveedor.id,
            nombre = proveedor.nombre,
            documento = proveedor.documento,
            telefono = proveedor.telefono,
            sector = proveedor.sector,
            entregaDirectaEnPlanta = if (proveedor.entregaDirectaEnPlanta) 1L else 0L,
            numeroVacas = proveedor.numeroVacas.toLong(),
            calificacion = proveedor.calificacion.name,
            activo = if (proveedor.activo) 1L else 0L,
            synced = if (synced) 1L else 0L,
            pendingAction = accion?.name,
            updatedAt = actualizadoEn.toString(),
            deleted = 0L,
        )
    }

    private fun ProveedorEntity.toDomain(): Proveedor =
        Proveedor(
            id = id,
            nombre = nombre,
            documento = documento,
            telefono = telefono,
            sector = sector,
            entregaDirectaEnPlanta = entregaDirectaEnPlanta != 0L,
            numeroVacas = numeroVacas.toInt(),
            calificacion = CalificacionProveedor.valueOf(calificacion),
            activo = activo != 0L,
        )

    private fun seed(): List<Proveedor> =
        listOf(
            Proveedor(
                id = "P-014",
                nombre = "Rosa Quispe Mamani",
                documento = "41028573",
                telefono = "951034221",
                sector = Sector.NORTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 9,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-027",
                nombre = "Elías Cutipa Apaza",
                documento = "42911087",
                telefono = "951034222",
                sector = Sector.ESTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 12,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-003",
                nombre = "Juana Choque Yupanqui",
                documento = "40119345",
                telefono = "951034223",
                sector = Sector.SUR,
                entregaDirectaEnPlanta = false,
                numeroVacas = 6,
                calificacion = CalificacionProveedor.B,
            ),
            Proveedor(
                id = "P-041",
                nombre = "Mario Tinta Cruz",
                documento = "43567812",
                telefono = "951034224",
                sector = Sector.OESTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 8,
                calificacion = CalificacionProveedor.B,
            ),
            Proveedor(
                id = "P-052",
                nombre = "Asoc. Ganadera Pallalla",
                documento = "20458713690",
                telefono = "951034225",
                sector = Sector.ESTE,
                entregaDirectaEnPlanta = true,
                numeroVacas = 31,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-008",
                nombre = "Felipa Aguilar Vilca",
                documento = "40774213",
                telefono = "951034226",
                sector = Sector.NORTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 5,
                calificacion = CalificacionProveedor.C,
            ),
        )

    private companion object {
        val RUTAS: Map<String, Set<String>> = mapOf(
            "A-01" to setOf("P-027", "P-052", "P-041"),
            "A-02" to setOf("P-014", "P-003", "P-008"),
        )
        val FECHA_SEED = LocalDateTime(2026, 1, 1, 0, 0)
    }
}
