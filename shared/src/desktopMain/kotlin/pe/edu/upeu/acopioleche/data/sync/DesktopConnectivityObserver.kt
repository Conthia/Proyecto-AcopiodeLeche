package pe.edu.upeu.acopioleche.data.sync

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.net.NetworkInterface

/**
 * JVM/Desktop no tiene un callback de sistema para cambios de red como Android, así que esto
 * hace polling simple cada [intervaloMs] revisando si hay alguna interfaz de red activa
 * (no loopback, no virtual). Es una señal de "hay red en la zona", no de "el backend responde":
 * eso lo decide el intento real de sincronizar.
 */
class DesktopConnectivityObserver(
    private val intervaloMs: Long = 5_000,
) : ConnectivityObserver {

    override fun observarConexion(): Flow<Boolean> =
        flow {
            while (true) {
                emit(hayConexion())
                delay(intervaloMs)
            }
        }.distinctUntilChanged()

    override suspend fun hayConexion(): Boolean =
        try {
            NetworkInterface.getNetworkInterfaces().asSequence().any { it.isUp && !it.isLoopback && !it.isVirtual }
        } catch (e: Exception) {
            false
        }
}
