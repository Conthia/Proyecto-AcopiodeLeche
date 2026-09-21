package pe.edu.upeu.acopioleche.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Construye el único `HttpClient` de la app. Todas las llamadas al backend Laravel pasan por
 * acá (vía las clases `*Api` de `data/remote/api`), así que este es el lugar para agregar
 * headers comunes, timeouts o reintentos más adelante sin tocar cada `*Api` una por una.
 */
object HttpClientFactory {
    fun create(): HttpClient =
        HttpClient(CIO) {
            expectSuccess = false

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                level = LogLevel.INFO
            }

            // Único punto de inyección de autenticación (ver AuthTokenProvider): el bloque de
            // defaultRequest se evalúa en cada request, así que basta con asignar
            // `AuthTokenProvider.token` desde donde sea para que empiece a viajar en el header.
            defaultRequest {
                AuthTokenProvider.token?.let { token -> header(HttpHeaders.Authorization, "Bearer $token") }
            }
        }
}
