package pe.edu.upeu.acopioleche.ui.entrega

import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion

/**
 * Envuelve una Entrega del dominio con datos que aun no existen en el modelo (nombre del
 * proveedor y estado de sincronizacion local), ya que esa informacion vive en la capa de
 * datos/infraestructura, no en la entidad de dominio.
 */
data class EntregaUi(
    val entrega: Entrega,
    val proveedorNombre: String,
    val estadoSincronizacion: EstadoSincronizacion,
)
