package pe.edu.upeu.acopioleche.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Dispara `sincronizarTodo()` automáticamente cada vez que [ConnectivityObserver] detecta que
 * apareció señal. `drop(1)` evita disparar un sync apenas arranca la app si ya había conexión
 * desde el inicio (ese caso ya lo cubre el sync manual / la sincronización al abrir pantallas);
 * solo importa la *transición* de sin-conexión a con-conexión.
 */
class AutoSyncController(
    private val connectivityObserver: ConnectivityObserver,
    private val coordinator: SyncCoordinator,
) {
    fun iniciar(scope: CoroutineScope) {
        scope.launch {
            connectivityObserver.observarConexion()
                .distinctUntilChanged()
                .drop(1)
                .filter { conectado -> conectado }
                .collect { coordinator.sincronizarTodo() }
        }
    }
}
