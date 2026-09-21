package pe.edu.upeu.acopioleche.presentation.core

/**
 * Puente de logging mínimo: el proyecto todavía no adoptó ninguna librería (Napier/Kermit u otra)
 * para `shared` (KMP puro). Usa `println` para no introducir una dependencia nueva sin decidirlo
 * explícitamente; sirve para no perder el detalle técnico de una excepción que el `UiState.Error`
 * ya no expone al usuario.
 */
object AppLogger {
    fun error(tag: String, mensaje: String, throwable: Throwable) {
        println("[$tag] $mensaje: ${throwable::class.simpleName}: ${throwable.message}")
    }
}
