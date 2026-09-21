package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.domain.model.RolUsuario

/**
 * Verifica la persistencia real de cuentas de usuario y control de intentos fallidos (RF-01, RNF-02, RNF-03)
 * usando SQLDelight sobre un archivo SQLite temporal.
 */
class SqlUsuarioRepositoryTest {

    private val archivo = File.createTempFile("acopioleche-user-test-", ".db")
    private val url = "jdbc:sqlite:${archivo.absolutePath}"

    @AfterTest
    fun limpiar() {
        archivo.delete()
    }

    private fun abrirRepositorio(): SqlUsuarioRepository {
        val driver = JdbcSqliteDriver(url)
        if (!archivo.exists() || archivo.length() == 0L) {
            AcopioLecheDatabase.Schema.create(driver)
        }
        return SqlUsuarioRepository(AcopioLecheDatabase(driver))
    }

    @Test
    fun buscarPorNombreUsuarioRetornaLasCuentasSemillaIniciales() {
        runBlocking {
            val repo = abrirRepositorio()

            val acopiador = repo.buscarPorNombreUsuario("jmamani")
            assertNotNull(acopiador)
            assertEquals("Juan Mamani", acopiador.nombreCompleto)
            assertEquals(RolUsuario.ACOPIADOR, acopiador.rol)

            val admin = repo.buscarPorNombreUsuario("admin")
            assertNotNull(admin)
            assertEquals("Elena Vargas", admin.nombreCompleto)
            assertEquals(RolUsuario.ADMINISTRADOR, admin.rol)
        }
    }

    @Test
    fun registrarIntentoFallidoIncrementaLosIntentosYSePersiste() {
        runBlocking {
            val repo = abrirRepositorio()
            val ahora = Clock.System.now()

            repo.registrarIntentoFallido("A-01", ahora)

            val acopiador = repo.buscarPorNombreUsuario("jmamani")
            assertNotNull(acopiador)
            assertEquals(1, acopiador.intentosFallidos)
            assertNotNull(acopiador.ultimoIntentoFallidoEn)
        }
    }

    @Test
    fun registrarLoginExitosoReiniciaElContadorDeIntentosFallidosACero() {
        runBlocking {
            val repo = abrirRepositorio()
            val ahora = Clock.System.now()

            repo.registrarIntentoFallido("A-01", ahora)
            repo.registrarLoginExitoso("A-01")

            val acopiador = repo.buscarPorNombreUsuario("jmamani")
            assertNotNull(acopiador)
            assertEquals(0, acopiador.intentosFallidos)
            assertNull(acopiador.ultimoIntentoFallidoEn)
        }
    }

    @Test
    fun losDatosDeUsuarioYEstadoDeIntentosSobrevivenAlReabrirLaBaseDeDatos() {
        runBlocking {
            val primeraApertura = abrirRepositorio()
            val ahora = Clock.System.now()
            primeraApertura.registrarIntentoFallido("A-01", ahora)

            val segundaApertura = abrirRepositorio()
            val acopiador = segundaApertura.buscarPorNombreUsuario("jmamani")

            assertNotNull(acopiador)
            assertEquals(1, acopiador.intentosFallidos)
        }
    }
}
