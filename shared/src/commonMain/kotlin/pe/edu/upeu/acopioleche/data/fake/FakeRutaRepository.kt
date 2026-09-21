package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.EstadoParada
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.ParadaRuta
import pe.edu.upeu.acopioleche.domain.model.ResultadoEliminacionRuta
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository

class FakeRutaRepository : RutaRepository {

    private val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _rutas: MutableStateFlow<List<RutaAcopio>> = MutableStateFlow(seed(hoy))
    private val rutas: StateFlow<List<RutaAcopio>> = _rutas.asStateFlow()

    override fun observarRutaDelDia(acopiadorId: String, fecha: LocalDate): Flow<RutaAcopio?> =
        rutas.map { lista -> lista.find { it.acopiadorId == acopiadorId && it.fecha == fecha } }

    override fun observarTodasRutas(): Flow<List<RutaAcopio>> = rutas

    override suspend fun asignarRuta(ruta: RutaAcopio) {
        _rutas.value = _rutas.value.filterNot { it.acopiadorId == ruta.acopiadorId && it.fecha == ruta.fecha } + ruta
    }

    override suspend fun actualizarRuta(ruta: RutaAcopio) {
        _rutas.value = _rutas.value
            .filterNot { it.id != ruta.id && it.acopiadorId == ruta.acopiadorId && it.fecha == ruta.fecha }
            .map { if (it.id == ruta.id) ruta else it }
            .let { lista -> if (lista.any { it.id == ruta.id }) lista else lista + ruta }
    }

    override suspend fun cerrarRuta(rutaId: String, volumenTotalDescargado: Double): Boolean {
        if (_rutas.value.none { it.id == rutaId }) return false
        _rutas.value = _rutas.value.map {
            if (it.id == rutaId) it.copy(estado = EstadoRuta.FINALIZADA) else it
        }
        return true
    }

    override suspend fun eliminarRuta(rutaId: String): ResultadoEliminacionRuta {
        val ruta = _rutas.value.find { it.id == rutaId } ?: return ResultadoEliminacionRuta.NoEncontrada
        val conAvance = ruta.paradas.count { it.estadoParada != EstadoParada.PENDIENTE }
        if (conAvance > 0) return ResultadoEliminacionRuta.TieneAvance(paradasConAvance = conAvance)
        _rutas.value = _rutas.value.filterNot { it.id == rutaId }
        return ResultadoEliminacionRuta.Eliminada
    }

    private fun seed(fechaHoy: LocalDate): List<RutaAcopio> =
        listOf(
            RutaAcopio(
                id = "R-2026-01",
                nombre = "Ruta Sector Coyme / Pallalla",
                acopiadorId = "A-01",
                centroSectorId = "CA-002",
                fecha = fechaHoy,
                paradas = listOf(
                    ParadaRuta(orden = 1, proveedorId = "P-027"),
                    ParadaRuta(orden = 2, proveedorId = "P-052"),
                    ParadaRuta(orden = 3, proveedorId = "P-041"),
                ),
            ),
            RutaAcopio(
                id = "R-2026-02",
                nombre = "Ruta Huata Centro - Norte",
                acopiadorId = "A-02",
                centroSectorId = "CA-001",
                fecha = fechaHoy,
                paradas = listOf(
                    ParadaRuta(orden = 1, proveedorId = "P-014"),
                    ParadaRuta(orden = 2, proveedorId = "P-003"),
                    ParadaRuta(orden = 3, proveedorId = "P-008"),
                ),
            ),
        )
}
