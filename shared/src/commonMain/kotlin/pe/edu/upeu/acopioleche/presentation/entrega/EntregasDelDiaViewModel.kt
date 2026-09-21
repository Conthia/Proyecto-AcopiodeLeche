package pe.edu.upeu.acopioleche.presentation.entrega

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.aResumen

class EntregasDelDiaViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    private val proveedorRepository: ProveedorRepository,
    private val analisisCalidadRepository: AnalisisCalidadRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(EntregasDelDiaUiState())
    val uiState: StateFlow<EntregasDelDiaUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                entregaRepository.observarEntregasDeHoy(),
                entregaRepository.observarPendientesDeSincronizar(),
                proveedorRepository.observarProveedores(),
            ) { entregas, pendientes, proveedores ->
                val pendientesIds = pendientes.map { it.id }.toSet()
                EntregasDelDiaUiState(
                    totalLitros = entregas.filter { it.estado !is EstadoEntrega.Cancelada && it.estado !is EstadoEntrega.NoRecogida }.sumOf { it.volumenLitros },
                    numeroProveedoresAtendidos = entregas.filter { it.estado !is EstadoEntrega.Cancelada }.map { it.proveedorId }.distinct().size,
                    entregas = entregas.map { entrega ->
                        entrega.aResumen(
                            proveedores = proveedores,
                            sincronizada = !pendientesIds.contains(entrega.id),
                        )
                    },
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun editarEntrega(
        id: String,
        nuevoVolumen: Double,
        nuevosPorongos: Int,
        alBloqueadoPorAnalisis: () -> Unit,
        alExito: () -> Unit,
    ) {
        scope.launch {
            val entrega = entregaRepository.buscarPorId(id)
            val analisisList = analisisCalidadRepository.observarAnalisisRecientes().first()
            val tieneAnalisis = analisisList.any { it.entregaId == id }

            if (entrega == null || tieneAnalisis || (entrega.estado !is EstadoEntrega.Pendiente && entrega.estado !is EstadoEntrega.NoRecogida)) {
                alBloqueadoPorAnalisis()
                return@launch
            }

            val editada = entrega.copy(
                volumenLitros = nuevoVolumen,
                cantidadPorongos = nuevosPorongos,
                estado = EstadoEntrega.Pendiente,
            )
            entregaRepository.registrar(editada)
            alExito()
        }
    }

    fun cancelarEntrega(
        id: String,
        motivo: String,
        usuarioId: String,
        alBloqueadoPorAnalisis: () -> Unit,
        alExito: () -> Unit,
    ) {
        scope.launch {
            val entrega = entregaRepository.buscarPorId(id)
            val analisisList = analisisCalidadRepository.observarAnalisisRecientes().first()
            val tieneAnalisis = analisisList.any { it.entregaId == id }

            if (entrega == null || tieneAnalisis || (entrega.estado !is EstadoEntrega.Pendiente && entrega.estado !is EstadoEntrega.NoRecogida)) {
                alBloqueadoPorAnalisis()
                return@launch
            }

            val cancelada = entrega.copy(
                estado = EstadoEntrega.Cancelada(
                    motivo = motivo,
                    canceladaPor = usuarioId,
                    fechaHora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                ),
            )
            entregaRepository.registrar(cancelada)
            alExito()
        }
    }
}
