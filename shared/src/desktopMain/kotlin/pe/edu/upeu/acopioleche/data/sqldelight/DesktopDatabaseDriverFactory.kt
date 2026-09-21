package pe.edu.upeu.acopioleche.data.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/**
 * A diferencia de `AndroidSqliteDriver`, `JdbcSqliteDriver` no crea el esquema solo: hay que
 * llamar a `Schema.create(driver)` explícitamente, y solo la primera vez (si el archivo ya
 * existía, las tablas ya están creadas de una corrida anterior).
 */
class DesktopDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val archivo = File(File(System.getProperty("user.home"), ".acopioleche"), "acopioleche.db")
        archivo.parentFile?.mkdirs()
        val yaExistia = archivo.exists()
        val driver: SqlDriver = JdbcSqliteDriver(url = "jdbc:sqlite:${archivo.absolutePath}")
        if (!yaExistia) {
            AcopioLecheDatabase.Schema.create(driver)
        }
        return driver
    }
}
