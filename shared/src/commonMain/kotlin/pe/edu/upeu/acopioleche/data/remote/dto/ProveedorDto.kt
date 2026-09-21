package pe.edu.upeu.acopioleche.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Espejo JSON de [pe.edu.upeu.acopioleche.domain.model.Proveedor] para el backend Laravel.
 * Deliberadamente separado del modelo de dominio: el dominio no debe conocer nombres de columnas
 * de API ni convenciones de serialización (snake_case acá porque así son las respuestas típicas
 * de Laravel/Eloquent).
 *
 * `actualizadoEn` y `eliminado` no existen en el dominio: son metadatos de sincronización que
 * solo le importan a esta capa y a `data/sync` (ver [pe.edu.upeu.acopioleche.domain.sync.CambioPendiente]).
 */
@Serializable
data class ProveedorDto(
    val id: String,
    val nombre: String,
    val documento: String,
    val telefono: String,
    val sector: String,
    @SerialName("entrega_directa_en_planta") val entregaDirectaEnPlanta: Boolean,
    @SerialName("numero_vacas") val numeroVacas: Int,
    val calificacion: String,
    val activo: Boolean,
    @SerialName("actualizado_en") val actualizadoEn: String,
    val eliminado: Boolean = false,
)
