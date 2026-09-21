package pe.edu.upeu.acopioleche.data.sync

import kotlinx.coroutines.flow.Flow

/**
 * Detección de conectividad "hay señal en la zona", no "el backend está disponible" — eso último
 * lo determina cada intento real de sincronizar (ver [SincronizadorDeEntidad]).
 *
 * No es un `expect class`: igual que [pe.edu.upeu.acopioleche.data.sqldelight.DatabaseDriverFactory],
 * Android necesita un `Context` para pedirle al sistema los cambios de red y Desktop/JVM no
 * tiene ese concepto, así que cada plataforma implementa esta interfaz
 * (`AndroidConnectivityObserver` / `DesktopConnectivityObserver`) y `ServiceLocator.init(...)`
 * la recibe ya construida, con el mismo patrón que el driver de SQLite.
 */
interface ConnectivityObserver {
    /** Emite el estado de conectividad actual y cada vez que cambia. */
    fun observarConexion(): Flow<Boolean>

    /** Lectura puntual, para el botón "Sincronizar ahora". */
    suspend fun hayConexion(): Boolean
}
