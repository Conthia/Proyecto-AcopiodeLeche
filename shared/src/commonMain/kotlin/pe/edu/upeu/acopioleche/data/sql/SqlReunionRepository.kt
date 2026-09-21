package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.ReunionEntity
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository

class SqlReunionRepository(
    database: AcopioLecheDatabase,
) : ReunionRepository {

    private val queries = database.reunionQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { insertar(it) }
        }
    }

    override fun observarReuniones(): Flow<List<Reunion>> =
        queries.selectTodas().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun guardar(reunion: Reunion) {
        insertar(reunion)
    }

    override suspend fun actualizar(reunion: Reunion) {
        insertar(reunion)
    }

    override suspend fun eliminar(id: String) {
        queries.eliminar(id)
    }

    private fun insertar(reunion: Reunion) {
        queries.insertar(
            id = reunion.id,
            tipo = reunion.tipo.name,
            tema = reunion.tema,
            fecha = reunion.fecha.toString(),
            horaInicioMinutos = reunion.horaInicioMinutos.toLong(),
            horaFinMinutos = reunion.horaFinMinutos.toLong(),
            lugar = reunion.lugar,
        )
    }

    private fun ReunionEntity.toDomain(): Reunion =
        Reunion(
            id = id,
            tipo = TipoEvento.valueOf(tipo),
            tema = tema,
            fecha = LocalDate.parse(fecha),
            horaInicioMinutos = horaInicioMinutos.toInt(),
            horaFinMinutos = horaFinMinutos.toInt(),
            lugar = lugar,
        )

    private fun seed(): List<Reunion> =
        listOf(
            Reunion(
                id = "R-08",
                tipo = TipoEvento.REUNION,
                tema = "Reunión mensual de productores",
                fecha = LocalDate(2026, 9, 12),
                horaInicioMinutos = 9 * 60,
                horaFinMinutos = 11 * 60,
                lugar = "Local comunal Huata Centro",
            ),
            Reunion(
                id = "R-07",
                tipo = TipoEvento.CAPACITACION,
                tema = "Ordeño higiénico y cadena de frío",
                fecha = LocalDate(2026, 9, 10),
                horaInicioMinutos = 15 * 60,
                horaFinMinutos = 17 * 60,
                lugar = "Centro de acopio Coyme",
            ),
            Reunion(
                id = "R-06",
                tipo = TipoEvento.CAPACITACION,
                tema = "Prevención de mastitis",
                fecha = LocalDate(2026, 8, 29),
                horaInicioMinutos = 15 * 60,
                horaFinMinutos = 17 * 60,
                lugar = "Local comunal Huata Centro",
            ),
        )
}
