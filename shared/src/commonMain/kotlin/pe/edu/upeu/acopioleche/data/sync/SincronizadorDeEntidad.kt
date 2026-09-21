package pe.edu.upeu.acopioleche.data.sync

/**
 * Resultado de un ciclo de sincronización de una entidad. `error != null` significa que el
 * ciclo se cortó (sin conexión real al backend, 5xx, timeout, etc.); lo que ya se envió antes
 * del error queda contado en [enviados] y nada se pierde localmente — los pendientes que no se
 * alcanzaron a enviar siguen marcados como tal para el próximo intento.
 */
data class ResultadoSincronizacion(
    val entidad: String,
    val enviados: Int = 0,
    val recibidos: Int = 0,
    val error: String? = null,
)

/**
 * Contrato que implementa cada `*SyncManager` (uno por entidad transaccional: Proveedor ahora,
 * Entrega/AnalisisCalidad/RutaAcopio/Liquidacion después). [SyncCoordinator] solo conoce esta
 * interfaz, así que agregar una entidad nueva a la sincronización automática es: implementar
 * este contrato + agregar la instancia a la lista del coordinador, sin tocar nada más.
 */
interface SincronizadorDeEntidad {
    val nombreEntidad: String

    suspend fun sincronizar(): ResultadoSincronizacion
}
