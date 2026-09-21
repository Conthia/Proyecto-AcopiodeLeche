package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Liquidacion

interface LiquidacionRepository {
    fun observarLiquidacionesDe(proveedorId: String): Flow<List<Liquidacion>>

    fun observarTodas(): Flow<List<Liquidacion>>

    suspend fun buscar(proveedorId: String, semanaInicio: LocalDate): Liquidacion?

    suspend fun registrar(liquidacion: Liquidacion)
}
