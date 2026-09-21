package pe.edu.upeu.acopioleche.presentation.core

import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Turno

/**
 * Proyección de presentación que combina una [Entrega] con el nombre de su [Proveedor] para
 * listas de UI. No es una entidad de dominio: el dominio no conoce nada de cómo se muestra en
 * pantalla.
 */
data class EntregaResumen(
    val id: String,
    val nombreProveedor: String,
    val codigoProveedor: String,
    val turno: Turno,
    val volumenLitros: Double,
    val cantidadPorongos: Int,
    val sincronizada: Boolean,
    val estado: EstadoEntrega = EstadoEntrega.Pendiente,
    // RF-21: nulo hasta que se registre la conciliación campo-planta.
    val volumenPlantaLitros: Double?,
    val diferenciaLitros: Double?,
)

fun Entrega.aResumen(proveedores: List<Proveedor>, sincronizada: Boolean): EntregaResumen {
    val proveedor = proveedores.find { it.id == proveedorId }
    return EntregaResumen(
        id = id,
        nombreProveedor = proveedor?.nombre ?: proveedorId,
        codigoProveedor = proveedorId,
        turno = turno,
        volumenLitros = volumenLitros,
        cantidadPorongos = cantidadPorongos,
        sincronizada = sincronizada,
        estado = estado,
        volumenPlantaLitros = volumenPlantaLitros,
        diferenciaLitros = diferenciaLitros,
    )
}
