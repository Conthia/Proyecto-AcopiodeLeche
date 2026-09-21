package pe.edu.upeu.acopioleche.data.sqldelight

import app.cash.sqldelight.db.SqlDriver

/**
 * Cada plataforma construye el driver de SQLite de forma distinta (Android necesita un
 * `Context`, Desktop/JVM abre un archivo directamente), así que no es un `expect class` con
 * constructor común: es una interfaz simple que implementa cada módulo de plataforma
 * (`androidMain`/`desktopMain` de `shared`) y que `ServiceLocator.init(...)` recibe ya
 * construida desde `AcopioLecheApplication` (Android) o `Main.kt` (Desktop).
 */
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
