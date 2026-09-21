package pe.edu.upeu.acopioleche.data.sync

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import pe.edu.upeu.acopioleche.data.remote.api.ProveedorApi
import pe.edu.upeu.acopioleche.data.remote.mapper.actualizadoEnLocalDateTime
import pe.edu.upeu.acopioleche.data.remote.mapper.toDomain
import pe.edu.upeu.acopioleche.data.remote.mapper.toDto
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.sync.AccionPendiente

/**
 * Sincronizador piloto (Fase 1). El patrón push-luego-pull de acá es el que se debe replicar
 * para Entrega/AnalisisCalidad/RutaAcopio/Liquidacion:
 *   1. Sube cada cambio local pendiente (CREAR/ACTUALIZAR/ELIMINAR) y confirma contra la
 *      respuesta del servidor.
 *   2. Trae el listado completo del servidor y lo aplica localmente con "el más reciente gana"
 *      (ver [ProveedorRepository.aplicarCambioRemoto]).
 *
 * Nota para Entrega (RN-20): a diferencia de Proveedor, aplicar un cambio remoto de Entrega no
 * puede ser un simple "el más reciente gana" — antes de sobrescribir localmente hay que validar
 * que la entrega no tenga ya un AnalisisCalidad registrado ni esté EnTransitoAPlanta/Liquidada
 * en ninguno de los dos lados (local o remoto), igual que ya se valida para ediciones desde la
 * UI. Ese guard adicional va en el equivalente a `EntregaRepository.aplicarCambioRemoto`, no acá.
 *
 * El listado completo en cada ciclo es intencional y simple para el piloto; con más volumen
 * conviene paginar o mandar `?desde=<ultimaSincronizacion>` — el backend tendría que soportarlo.
 */
class ProveedorSyncManager(
    private val repository: ProveedorRepository,
    private val api: ProveedorApi,
) : SincronizadorDeEntidad {

    override val nombreEntidad: String = "Proveedor"

    override suspend fun sincronizar(): ResultadoSincronizacion {
        var enviados = 0
        try {
            val pendientes = repository.observarPendientesDeSincronizar().first()
            pendientes.forEach { cambio ->
                when (cambio.accion) {
                    AccionPendiente.CREAR -> {
                        val respuesta = api.crear(cambio.entidad.toDto(actualizadoEn = cambio.actualizadoEn))
                        repository.marcarSincronizado(cambio.entidad.id, respuesta.actualizadoEnLocalDateTime())
                    }
                    AccionPendiente.ACTUALIZAR -> {
                        val respuesta = api.actualizar(cambio.entidad.id, cambio.entidad.toDto(actualizadoEn = cambio.actualizadoEn))
                        repository.marcarSincronizado(cambio.entidad.id, respuesta.actualizadoEnLocalDateTime())
                    }
                    AccionPendiente.ELIMINAR -> {
                        api.eliminar(cambio.entidad.id)
                        repository.confirmarEliminacionRemota(cambio.entidad.id)
                    }
                }
                enviados++
            }

            val remotos = api.listar()
            remotos.forEach { dto ->
                if (dto.eliminado) {
                    repository.aplicarEliminacionRemota(dto.id, dto.actualizadoEnLocalDateTime())
                } else {
                    repository.aplicarCambioRemoto(dto.toDomain(), dto.actualizadoEnLocalDateTime())
                }
            }
            return ResultadoSincronizacion(entidad = nombreEntidad, enviados = enviados, recibidos = remotos.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Sin conexión real al backend, timeout, 5xx, etc.: lo que ya se envió queda
            // confirmado (marcarSincronizado ya corrió); el resto sigue pendiente para el
            // próximo intento. No se relanza: un fallo de red no debe tumbar la app.
            return ResultadoSincronizacion(entidad = nombreEntidad, enviados = enviados, error = e.message ?: "Error de red desconocido")
        }
    }
}
