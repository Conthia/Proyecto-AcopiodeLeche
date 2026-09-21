package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.EquipoCampoEntity
import pe.edu.upeu.acopioleche.domain.model.EquipoCampo
import pe.edu.upeu.acopioleche.domain.model.EstadoConexion
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository

/** `EstadoConexion` (sealed interface) se aplana en columnas — ver `EquipoCampo.sq`. */
class SqlEquipoCampoRepository(
    database: AcopioLecheDatabase,
) : EquipoCampoRepository {

    private val queries = database.equipoCampoQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { insertar(it) }
        }
    }

    override fun observarEquipos(): Flow<List<EquipoCampo>> =
        queries.selectTodos().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    private fun insertar(equipo: EquipoCampo) {
        val (tipo, dias) = equipo.estadoConexion.aColumnas()
        queries.insertar(
            id = equipo.id,
            nombre = equipo.nombre,
            tipo = equipo.tipo,
            centroAcopioId = equipo.centroAcopioId,
            estadoConexionTipo = tipo,
            estadoConexionDias = dias,
        )
    }

    private fun EstadoConexion.aColumnas(): Pair<String, Long?> =
        when (this) {
            is EstadoConexion.AlDia -> "AL_DIA" to null
            is EstadoConexion.Pendiente -> "PENDIENTE" to diasSinSincronizar.toLong()
            is EstadoConexion.SinConexion -> "SIN_CONEXION" to dias.toLong()
        }

    private fun EquipoCampoEntity.estadoDeColumnas(): EstadoConexion =
        when (estadoConexionTipo) {
            "AL_DIA" -> EstadoConexion.AlDia
            "PENDIENTE" -> EstadoConexion.Pendiente(diasSinSincronizar = requireNotNull(estadoConexionDias).toInt())
            "SIN_CONEXION" -> EstadoConexion.SinConexion(dias = requireNotNull(estadoConexionDias).toInt())
            else -> error("estadoConexionTipo desconocido en EquipoCampoEntity: $estadoConexionTipo")
        }

    private fun EquipoCampoEntity.toDomain(): EquipoCampo =
        EquipoCampo(
            id = id,
            nombre = nombre,
            tipo = tipo,
            centroAcopioId = centroAcopioId,
            estadoConexion = estadoDeColumnas(),
        )

    private fun seed(): List<EquipoCampo> =
        listOf(
            EquipoCampo(
                id = "EQ-01",
                nombre = "Tablet acopio · Huata Centro",
                tipo = "Tablet",
                centroAcopioId = "CA-001",
                estadoConexion = EstadoConexion.AlDia,
            ),
            EquipoCampo(
                id = "EQ-02",
                nombre = "Celular ruta Coyme",
                tipo = "Celular",
                centroAcopioId = "CA-002",
                estadoConexion = EstadoConexion.Pendiente(diasSinSincronizar = 1),
            ),
            EquipoCampo(
                id = "EQ-03",
                nombre = "Tablet acopio · Pallalla",
                tipo = "Tablet",
                centroAcopioId = "CA-003",
                estadoConexion = EstadoConexion.SinConexion(dias = 2),
            ),
        )
}
