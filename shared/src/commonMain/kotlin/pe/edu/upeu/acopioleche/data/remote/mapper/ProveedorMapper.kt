package pe.edu.upeu.acopioleche.data.remote.mapper

import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.data.remote.dto.ProveedorDto
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.domain.model.Proveedor

fun ProveedorDto.toDomain(): Proveedor =
    Proveedor(
        id = id,
        nombre = nombre,
        documento = documento,
        telefono = telefono,
        sector = sector,
        entregaDirectaEnPlanta = entregaDirectaEnPlanta,
        numeroVacas = numeroVacas,
        calificacion = CalificacionProveedor.valueOf(calificacion),
        activo = activo,
    )

fun ProveedorDto.actualizadoEnLocalDateTime(): LocalDateTime = LocalDateTime.parse(actualizadoEn)

fun Proveedor.toDto(actualizadoEn: LocalDateTime, eliminado: Boolean = false): ProveedorDto =
    ProveedorDto(
        id = id,
        nombre = nombre,
        documento = documento,
        telefono = telefono,
        sector = sector,
        entregaDirectaEnPlanta = entregaDirectaEnPlanta,
        numeroVacas = numeroVacas,
        calificacion = calificacion.name,
        activo = activo,
        actualizadoEn = actualizadoEn.toString(),
        eliminado = eliminado,
    )
