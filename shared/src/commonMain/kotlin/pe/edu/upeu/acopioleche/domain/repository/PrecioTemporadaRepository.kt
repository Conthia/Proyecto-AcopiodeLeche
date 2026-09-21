package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada

interface PrecioTemporadaRepository {
    fun observarPrecios(): Flow<List<PrecioTemporada>>

    suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): Double

    suspend fun guardar(precio: PrecioTemporada)
}
