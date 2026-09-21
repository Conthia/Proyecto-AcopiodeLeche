package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository

/**
 * Implementación en memoria para esta versión exploratoria: no hay backend ni base de datos
 * local todavía (ver docs/modelo-dominio.md, sección "Decisiones de alcance"). Simula la cola
 * de sincronización marcando cada [Entrega] como pendiente hasta que se llama a
 * [sincronizarPendientes].
 */
class FakeEntregaRepository : EntregaRepository {

    private val pendientesIds = mutableSetOf("E-1042", "E-1041")

    private val _entregas: MutableStateFlow<List<Entrega>> = MutableStateFlow(seed())

    private val entregas: StateFlow<List<Entrega>> = _entregas.asStateFlow()

    override fun observarEntregasDeHoy(): StateFlow<List<Entrega>> = entregas

    override fun observarEntregasDe(proveedorId: String): Flow<List<Entrega>> =
        entregas.map { lista -> lista.filter { it.proveedorId == proveedorId } }

    override fun observarTodasLasEntregas(): Flow<List<Entrega>> = entregas

    override fun observarPendientesDeSincronizar(): Flow<List<Entrega>> =
        entregas.map { lista -> lista.filter { entrega -> pendientesIds.contains(entrega.id) } }

    // Datos de ejemplo fijos: aún no existe historial persistido de varios días (ver
    // docs/modelo-dominio.md, sección "Decisiones de alcance").
    override fun observarVolumenUltimaSemana(): Flow<List<Double>> =
        MutableStateFlow(listOf(124.0, 168.0, 152.0, 191.0, 176.0, 204.0, 188.0)).asStateFlow()

    override suspend fun registrar(entrega: Entrega) {
        pendientesIds += entrega.id
        val existe = _entregas.value.any { it.id == entrega.id }
        if (existe) {
            _entregas.value = _entregas.value.map { if (it.id == entrega.id) entrega else it }
        } else {
            _entregas.value = listOf(entrega) + _entregas.value
        }
    }

    override suspend fun buscarPorId(id: String): Entrega? = _entregas.value.find { it.id == id }

    override suspend fun eliminar(id: String) {
        pendientesIds -= id
        _entregas.value = _entregas.value.filter { it.id != id }
    }

    override suspend fun sincronizarPendientes(): Int {
        delay(timeMillis = 900)
        val enviados = pendientesIds.size
        pendientesIds.clear()
        _entregas.value = _entregas.value.map { entrega -> entrega.copy(estado = EstadoEntrega.Aceptada) }
        return enviados
    }

    fun estaPendiente(entregaId: String): Boolean = pendientesIds.contains(entregaId)

    private fun seed(): List<Entrega> =
        listOf(
            Entrega(
                id = "E-1042",
                proveedorId = "P-027",
                acopiadorId = "A-01",
                centroAcopioId = "CA-002",
                fecha = HOY,
                turno = Turno.MANANA,
                volumenLitros = 24.0,
                estado = EstadoEntrega.Pendiente,
                cantidadPorongos = 1,
            ),
            Entrega(
                id = "E-1041",
                proveedorId = "P-052",
                acopiadorId = "A-01",
                centroAcopioId = "CA-003",
                fecha = HOY,
                turno = Turno.MANANA,
                volumenLitros = 58.0,
                estado = EstadoEntrega.Pendiente,
                cantidadPorongos = 2,
            ),
            Entrega(
                id = "E-1040",
                proveedorId = "P-014",
                acopiadorId = "A-01",
                centroAcopioId = "CA-001",
                fecha = HOY,
                turno = Turno.MANANA,
                volumenLitros = 18.5,
                estado = EstadoEntrega.Aceptada,
                cantidadPorongos = 1,
            ),
            Entrega(
                id = "E-1039",
                proveedorId = "P-003",
                acopiadorId = "A-02",
                centroAcopioId = "CA-001",
                fecha = HOY,
                turno = Turno.MANANA,
                volumenLitros = 11.0,
                estado = EstadoEntrega.Aceptada,
                cantidadPorongos = 1,
            ),
            Entrega(
                id = "E-1038",
                proveedorId = "P-008",
                acopiadorId = "A-02",
                centroAcopioId = "CA-001",
                fecha = HOY,
                turno = Turno.MANANA,
                volumenLitros = 9.4,
                estado = EstadoEntrega.Aceptada,
                cantidadPorongos = 1,
            ),
        )

    private companion object {
        val HOY: LocalDate = LocalDate(2026, 9, 5)
    }
}
