package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada

interface PrecioTemporadaRepository {
    fun observarPrecios(): Flow<List<PrecioTemporada>>

    /**
     * La [PrecioTemporada] vigente en [fecha], o `null` si ninguna la cubre. El repositorio solo
     * informa el dato — no decide ningún respaldo: eso es una regla de negocio
     * ([pe.edu.upeu.acopioleche.domain.service.ReglasNegocio.precioReferenciaPorLitro]), no un
     * detalle de acceso a datos, y le corresponde al llamador.
     */
    suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioTemporada?

    suspend fun guardar(precio: PrecioTemporada)
}
