package pe.edu.upeu.acopioleche.data.remote

/**
 * Configuración del backend Laravel. Único lugar que hay que tocar para apuntar a otro entorno
 * (dev/staging/prod) o para pruebas manuales — ver el README de esta fase.
 *
 * El valor por defecto (`10.0.2.2`) es el alias que usa el emulador de Android para llegar al
 * `localhost` de la máquina donde corre `php artisan serve`. En un dispositivo físico o en
 * Desktop hay que reemplazarlo por la IP real del servidor en la red local.
 */
object NetworkConfig {
    var baseUrl: String = "http://10.0.2.2:8000/api"
}

/**
 * Único punto de inyección del token de autenticación contra el backend. Todavía no hay login
 * remoto (el login sigue siendo 100% local con SQLite), así que `token` queda en `null` por
 * ahora. Cuando se agregue autenticación contra Laravel, basta con asignar el token acá —
 * `HttpClientFactory` ya lo agrega a cada request— sin tocar ninguna otra clase de esta capa.
 */
object AuthTokenProvider {
    @Volatile
    var token: String? = null
}
