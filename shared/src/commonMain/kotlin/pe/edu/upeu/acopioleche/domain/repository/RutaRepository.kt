package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.ResultadoEliminacionRuta
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio

interface RutaRepository {
    fun observarRutaDelDia(acopiadorId: String, fecha: LocalDate): Flow<RutaAcopio?>

    fun observarTodasRutas(): Flow<List<RutaAcopio>>

    suspend fun asignarRuta(ruta: RutaAcopio)

    suspend fun actualizarRuta(ruta: RutaAcopio)

    suspend fun cerrarRuta(rutaId: String, volumenTotalDescargado: Double): Boolean

    /** Bloquea el borrado si el acopiador ya avanzó en la ruta (paradas visitadas/omitidas). */
    suspend fun eliminarRuta(rutaId: String): ResultadoEliminacionRuta
}
