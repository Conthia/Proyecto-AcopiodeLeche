package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.acopioleche.domain.model.AnalisisCalidad

interface AnalisisCalidadRepository {
    /** Todos los análisis recientes, tanto normales como con alertas de calidad. */
    fun observarAnalisisRecientes(): Flow<List<AnalisisCalidad>>

    suspend fun registrar(analisis: AnalisisCalidad)
}
