package pe.edu.upeu.acopioleche.domain.sync

import kotlinx.datetime.LocalDateTime

/**
 * Operación local que todavía no fue confirmada por el backend. Cada `Sql*Repository` que
 * participe en sincronización expone su cola de pendientes con este tipo, para que un
 * `Sincronizador` (ver `data/sync`) no necesite conocer los detalles de persistencia de cada
 * entidad.
 */
enum class AccionPendiente {
    CREAR,
    ACTUALIZAR,
    ELIMINAR,
}

data class CambioPendiente<T>(
    val entidad: T,
    val accion: AccionPendiente,
    val actualizadoEn: LocalDateTime,
)
