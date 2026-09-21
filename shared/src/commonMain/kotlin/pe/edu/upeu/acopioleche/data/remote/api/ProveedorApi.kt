package pe.edu.upeu.acopioleche.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import pe.edu.upeu.acopioleche.data.remote.NetworkConfig
import pe.edu.upeu.acopioleche.data.remote.dto.ProveedorDto

/**
 * Un 4xx/5xx del backend Laravel (típicamente un 422 de validación) con su formato de error
 * estándar `{"message": "...", "errors": {"campo": ["..."]}}`. `message` ya es apto para
 * mostrarlo al usuario o loguearlo tal cual; `errores` trae el detalle campo por campo cuando
 * el backend lo manda.
 */
class ValidacionRemotaException(
    message: String,
    val errores: Map<String, List<String>> = emptyMap(),
) : Exception(message)

@Serializable
private data class ErrorLaravelDto(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
)

/**
 * Cliente REST para el recurso `proveedores` del backend Laravel. Ver el contrato de endpoints
 * en el resumen entregado junto con esta fase (método, ruta y forma del JSON). Es el único lugar
 * de la app que arma URLs/verbos HTTP para esta entidad; `ProveedorSyncManager` no conoce Ktor.
 *
 * `HttpClientFactory` configura `expectSuccess = false`, así que Ktor NUNCA lanza por su cuenta
 * ante un 4xx/5xx: cada método revisa el status code a mano antes de deserializar como
 * [ProveedorDto] (ver [lanzarSiEsError]). Sin esto, un 422 de validación intentaría
 * deserializarse igual como `ProveedorDto` y reventaría con un error de parseo genérico en vez
 * de propagar el mensaje real de validación.
 */
class ProveedorApi(
    private val client: HttpClient,
    private val baseUrl: () -> String = { NetworkConfig.baseUrl },
) {
    suspend fun listar(): List<ProveedorDto> {
        val respuesta = client.get("${baseUrl()}/proveedores")
        lanzarSiEsError(respuesta)
        return respuesta.body()
    }

    suspend fun crear(dto: ProveedorDto): ProveedorDto {
        val respuesta = client.post("${baseUrl()}/proveedores") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        lanzarSiEsError(respuesta)
        return respuesta.body()
    }

    suspend fun actualizar(id: String, dto: ProveedorDto): ProveedorDto {
        val respuesta = client.put("${baseUrl()}/proveedores/$id") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        lanzarSiEsError(respuesta)
        return respuesta.body()
    }

    suspend fun eliminar(id: String) {
        lanzarSiEsError(client.delete("${baseUrl()}/proveedores/$id"))
    }

    private suspend fun lanzarSiEsError(respuesta: HttpResponse) {
        if (respuesta.status.isSuccess()) return

        val error = runCatching { respuesta.body<ErrorLaravelDto>() }.getOrNull()
        throw ValidacionRemotaException(
            message = error?.message ?: "Error del servidor (${respuesta.status.value})",
            errores = error?.errors ?: emptyMap(),
        )
    }
}
