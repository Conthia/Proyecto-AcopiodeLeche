package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.AnalisisCalidadEntity
import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad
import pe.edu.upeu.acopioleche.domain.model.CriterioAnalisis
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.OrigenDatoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository

class SqlAnalisisCalidadRepository(
    database: AcopioLecheDatabase,
) : AnalisisCalidadRepository {

    private val queries = database.analisisCalidadQueries

    init {
        if (queries.contarFilas().executeAsOne() == 0L) {
            seed().forEach { insertarSinFlow(it) }
        }
    }

    override fun observarAnalisisRecientes(): Flow<List<AnalisisCalidad>> =
        queries.selectRecientes().asFlow().mapToList(Dispatchers.Default).map { filas -> filas.map { it.toDomain() } }

    override suspend fun registrar(analisis: AnalisisCalidad) {
        insertarSinFlow(analisis)
    }

    private fun insertarSinFlow(analisis: AnalisisCalidad) {
        val columnas = analisis.resultado.aColumnas()
        queries.insertar(
            id = analisis.id,
            entregaId = analisis.entregaId,
            tecnicoId = analisis.tecnicoId,
            fecha = analisis.fecha.toString(),
            resultadoTipo = columnas.tipo,
            densidad = columnas.densidad,
            acidez = columnas.acidez,
            grasa = columnas.grasa,
            proteina = columnas.proteina,
            lactosa = columnas.lactosa,
            temperatura = columnas.temperatura,
            ph = columnas.ph,
            motivo = columnas.motivo,
            valorMedido = columnas.valorMedido,
            rangoMin = columnas.rangoMin,
            rangoMax = columnas.rangoMax,
            indicio = columnas.indicio,
            porcentajeAgua = columnas.porcentajeAgua,
            criterioSeleccion = analisis.criterioSeleccion.name,
            origenDato = analisis.origenDato.name,
            firmaProductorPresente = if (analisis.firmaProductorPresente) 1L else 0L,
        )
    }

    private data class ColumnasResultado(
        val tipo: String,
        val densidad: Double? = null,
        val acidez: Double? = null,
        val grasa: Double? = null,
        val proteina: Double? = null,
        val lactosa: Double? = null,
        val temperatura: Double? = null,
        val ph: Double? = null,
        val motivo: String? = null,
        val valorMedido: Double? = null,
        val rangoMin: Double? = null,
        val rangoMax: Double? = null,
        val indicio: String? = null,
        val porcentajeAgua: Double? = null,
    )

    private fun ResultadoAnalisis.aColumnas(): ColumnasResultado =
        when (this) {
            is ResultadoAnalisis.Normal -> ColumnasResultado(
                tipo = "NORMAL",
                densidad = densidad,
                acidez = acidez,
                grasa = grasa,
                proteina = proteina,
                lactosa = lactosa,
                temperatura = temperatura,
                ph = ph,
            )
            is ResultadoAnalisis.FueraDeRango -> ColumnasResultado(
                tipo = "FUERA_DE_RANGO",
                motivo = motivo.name,
                valorMedido = valorMedido,
                rangoMin = rangoPermitido.start,
                rangoMax = rangoPermitido.endInclusive,
            )
            is ResultadoAnalisis.Adulterada -> ColumnasResultado(tipo = "ADULTERADA", indicio = indicio, porcentajeAgua = porcentajeAgua)
        }

    private fun AnalisisCalidadEntity.resultadoDeColumnas(): ResultadoAnalisis =
        when (resultadoTipo) {
            "NORMAL" -> ResultadoAnalisis.Normal(
                densidad = requireNotNull(densidad),
                acidez = requireNotNull(acidez),
                grasa = requireNotNull(grasa),
                proteina = requireNotNull(proteina),
                lactosa = requireNotNull(lactosa),
                temperatura = requireNotNull(temperatura),
                ph = requireNotNull(ph),
            )
            "FUERA_DE_RANGO" -> ResultadoAnalisis.FueraDeRango(
                motivo = MotivoRechazo.valueOf(requireNotNull(motivo)),
                valorMedido = requireNotNull(valorMedido),
                rangoPermitido = requireNotNull(rangoMin)..requireNotNull(rangoMax),
            )
            "ADULTERADA" -> ResultadoAnalisis.Adulterada(
                indicio = requireNotNull(indicio),
                porcentajeAgua = requireNotNull(porcentajeAgua),
            )
            else -> error("resultadoTipo desconocido en AnalisisCalidadEntity: $resultadoTipo")
        }

    private fun AnalisisCalidadEntity.toDomain(): AnalisisCalidad =
        AnalisisCalidad(
            id = id,
            entregaId = entregaId,
            tecnicoId = tecnicoId,
            fecha = LocalDateTime.parse(fecha),
            resultado = resultadoDeColumnas(),
            criterioSeleccion = criterioSeleccion?.let { CriterioAnalisis.valueOf(it) } ?: CriterioAnalisis.ALEATORIO,
            origenDato = origenDato?.let { OrigenDatoAnalisis.valueOf(it) } ?: OrigenDatoAnalisis.INGRESO_MANUAL,
            firmaProductorPresente = firmaProductorPresente != 0L,
        )

    // Sin semilla deliberadamente: cada análisis debe registrarse desde RegistrarAnalisisScreen
    // contra una Entrega real (ver nota en SqlEntregaRepository/SqlRutaRepository) — un análisis
    // de ejemplo referenciando una entrega que no existe quedaría huérfano.
    private fun seed(): List<AnalisisCalidad> = emptyList()
}
