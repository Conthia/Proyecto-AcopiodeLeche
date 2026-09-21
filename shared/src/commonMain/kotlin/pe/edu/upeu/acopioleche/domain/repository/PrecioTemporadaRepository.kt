package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.PrecioVigente

interface PrecioTemporadaRepository {
    fun observarPrecios(): Flow<List<PrecioTemporada>>

    suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioVigente

    suspend fun guardar(precio: PrecioTemporada)
}
