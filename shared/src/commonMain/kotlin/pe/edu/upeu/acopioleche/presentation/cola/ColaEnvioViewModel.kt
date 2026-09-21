package pe.edu.upeu.acopioleche.presentation.cola

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.data.sync.SyncCoordinator
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.aResumen

/**
 * Hub de "Pendientes de envío" de la app. Hoy combina dos mecanismos de sincronización
 * distintos a propósito: el de Entrega es el simulador local pre-existente (marca todo como
 * enviado sin llamar a ningún backend, ver `EntregaRepository.sincronizarPendientes`), mientras
 * que el de Proveedor (piloto Fase 1, [syncCoordinator]) sí llama al backend Laravel de verdad
 * vía Ktor. Cuando Entrega pase por la misma capa de red (Fase 2), este ViewModel deja de
 * necesitar el caso especial y todo pasa por [syncCoordinator].
 */
class ColaEnvioViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    private val proveedorRepository: ProveedorRepository,
    private val syncCoordinator: SyncCoordinator,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(ColaEnvioUiState())
    val uiState: StateFlow<ColaEnvioUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                entregaRepository.observarEntregasDeHoy(),
                entregaRepository.observarPendientesDeSincronizar(),
                proveedorRepository.observarProveedores(),
                proveedorRepository.observarPendientesDeSincronizar(),
            ) { entregas, pendientes, proveedores, proveedoresPendientes ->
                val pendientesIds = pendientes.map { it.id }.toSet()
                val outbox = entregas.map { entrega -> entrega.aResumen(proveedores = proveedores, sincronizada = !pendientesIds.contains(entrega.id)) }
                outbox to proveedoresPendientes.size
            }.collect { (outbox, proveedoresPendientes) ->
                _uiState.value = _uiState.value.copy(outbox = outbox, proveedoresPendientes = proveedoresPendientes)
            }
        }
    }

    fun onToggleConexion() {
        val enLinea = !_uiState.value.enLinea
        _uiState.value = _uiState.value.copy(
            enLinea = enLinea,
            ultimoMensaje = if (enLinea) null else "Modo sin conexión: se guarda en el equipo",
        )
        if (enLinea) onSincronizarClick()
    }

    fun onSincronizarClick() {
        if (!_uiState.value.enLinea) {
            _uiState.value = _uiState.value.copy(ultimoMensaje = "Sin conexión: la cola se enviará automáticamente")
            return
        }
        if (_uiState.value.numeroPendientes == 0) {
            _uiState.value = _uiState.value.copy(ultimoMensaje = "Todo está sincronizado")
            return
        }
        scope.launch {
            _uiState.value = _uiState.value.copy(sincronizando = true, ultimoMensaje = null)
            val enviados = entregaRepository.sincronizarPendientes()
            val resultados = syncCoordinator.sincronizarTodo()
            val errores = resultados.mapNotNull { it.error }
            val enviadosProveedores = resultados.sumOf { it.enviados }
            val mensaje = when {
                errores.isNotEmpty() -> "Sin conexión con el servidor todavía (${errores.first()}). Se reintentará más tarde."
                else -> "${enviados + enviadosProveedores} registro(s) enviados al servidor"
            }
            _uiState.value = _uiState.value.copy(sincronizando = false, ultimoMensaje = mensaje)
        }
    }
}
