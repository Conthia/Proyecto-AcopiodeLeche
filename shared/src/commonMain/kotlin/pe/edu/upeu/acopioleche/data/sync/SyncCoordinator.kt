package pe.edu.upeu.acopioleche.data.sync

/**
 * Punto único para disparar "sincronizar todo lo que haya" — lo usa tanto el botón manual
 * (Cola de envío) como [AutoSyncController] cuando detecta que volvió la conexión. Recorre los
 * sincronizadores en orden y sigue con el resto aunque uno falle, para que un backend caído para
 * una entidad no bloquee la sincronización de las demás.
 */
class SyncCoordinator(
    private val sincronizadores: List<SincronizadorDeEntidad>,
) {
    suspend fun sincronizarTodo(): List<ResultadoSincronizacion> =
        sincronizadores.map { it.sincronizar() }
}
