package pe.edu.upeu.acopioleche.domain.service

import kotlin.uuid.Uuid

/**
 * Único punto para generar el id de una entidad nueva creada offline (alta de Proveedor, y
 * cualquier otra entidad que en el futuro sincronice contra el backend Laravel siguiendo el
 * mismo patrón que [pe.edu.upeu.acopioleche.data.sync.ProveedorSyncManager]).
 *
 * Debe ser un UUID real, NO un id legible tipo "P-042" como los de
 * `SqlProveedorRepository.seed()`: desde que el backend acepta y conserva el id que manda el
 * cliente en el alta (`POST /api/proveedores`), ese id queda persistido tal cual en el
 * servidor — un id no-UUID igual funcionaría por ahora (la columna es un `string` sin validar
 * formato en el modelo Eloquent), pero corre riesgo de colisión entre dispositivos distintos
 * generando ids offline al mismo tiempo, que un UUID v4 evita por diseño.
 */
object GeneradorId {
    fun nuevo(): String = Uuid.random().toString()
}
