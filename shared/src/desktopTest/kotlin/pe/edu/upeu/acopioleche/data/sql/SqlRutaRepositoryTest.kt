package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.ParadaRuta
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio

class SqlRutaRepositoryTest {

    private val archivo = File.createTempFile("acopioleche-ruta-test-", ".db")
    private val url = "jdbc:sqlite:${archivo.absolutePath}"
    private val hoy: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    @AfterTest
    fun limpiar() {
        archivo.delete()
    }

    private fun abrirRepositorio(): SqlRutaRepository {
        val driver = JdbcSqliteDriver(url)
        if (!archivo.exists() || archivo.length() == 0L) {
            AcopioLecheDatabase.Schema.create(driver)
        }
        return SqlRutaRepository(AcopioLecheDatabase(driver))
    }

    @Test
    fun `no hay ninguna ruta precargada al abrir la base por primera vez`() {
        runBlocking {
            val repo = abrirRepositorio()
            val ruta = repo.observarRutaDelDia(acopiadorId = "A-01", fecha = hoy).first()

            assertEquals(null, ruta)
        }
    }

    @Test
    fun `asignarRuta persiste una ruta consultable por acopiadorId y fecha`() {
        runBlocking {
            val repo = abrirRepositorio()
            val ruta = RutaAcopio(
                id = "R-TEST-01",
                nombre = "Ruta de prueba",
                acopiadorId = "A-01",
                centroSectorId = "CA-002",
                fecha = hoy,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-027")),
            )
            repo.asignarRuta(ruta)

            val recuperada = repo.observarRutaDelDia(acopiadorId = "A-01", fecha = hoy).first()
            assertNotNull(recuperada)
            assertEquals("CA-002", recuperada.centroSectorId)
            assertTrue(recuperada.paradas.isNotEmpty())
        }
    }

    @Test
    fun `asignarRuta reemplaza la ruta anterior del mismo acopiador y fecha en vez de duplicarla`() {
        runBlocking {
            val repo = abrirRepositorio()
            repo.asignarRuta(
                RutaAcopio(
                    id = "R-TEST-VIEJA",
                    nombre = "Ruta vieja",
                    acopiadorId = "A-01",
                    centroSectorId = "CA-001",
                    fecha = hoy,
                    paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-014")),
                ),
            )
            repo.asignarRuta(
                RutaAcopio(
                    id = "R-TEST-NUEVA",
                    nombre = "Ruta nueva",
                    acopiadorId = "A-01",
                    centroSectorId = "CA-002",
                    fecha = hoy,
                    paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-027")),
                ),
            )

            val actual = repo.observarRutaDelDia(acopiadorId = "A-01", fecha = hoy).first()
            assertNotNull(actual)
            assertEquals("R-TEST-NUEVA", actual.id)
            assertEquals(1, repo.observarTodasRutas().first().count { it.acopiadorId == "A-01" })
        }
    }

    @Test
    fun `cerrarRuta con ID inexistente devuelve false`() {
        runBlocking {
            val repo = abrirRepositorio()
            val resultado = repo.cerrarRuta(rutaId = "R-INEXISTENTE", volumenTotalDescargado = 100.0)

            assertFalse(resultado)
        }
    }

    @Test
    fun `cerrarRuta con ID existente persiste el volumenDescargadoLitros y fechaHoraCierre`() {
        runBlocking {
            val repo = abrirRepositorio()
            val rutaInicial = RutaAcopio(
                id = "R-TEST-CIERRE",
                nombre = "Ruta a cerrar",
                acopiadorId = "A-01",
                centroSectorId = "CA-002",
                fecha = hoy,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-027")),
            )
            repo.asignarRuta(rutaInicial)

            val exito = repo.cerrarRuta(rutaId = rutaInicial.id, volumenTotalDescargado = 82.0)
            assertTrue(exito)

            val rutaCerrada = repo.observarRutaDelDia(acopiadorId = "A-01", fecha = hoy).first()
            assertNotNull(rutaCerrada)
            assertEquals(EstadoRuta.FINALIZADA, rutaCerrada.estado)
            assertEquals(82.0, rutaCerrada.volumenDescargadoLitros)
            assertNotNull(rutaCerrada.fechaHoraCierre)
        }
    }

    @Test
    fun `las rutas asignadas sobreviven a reabrir la base de datos`() {
        runBlocking {
            val primeraApertura = abrirRepositorio()
            val nuevaRuta = RutaAcopio(
                id = "R-TEST-SQL-99",
                nombre = "Ruta Test Persistencia",
                acopiadorId = "A-05",
                centroSectorId = "CA-003",
                fecha = hoy,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-014")),
            )
            primeraApertura.asignarRuta(nuevaRuta)

            val segundaApertura = abrirRepositorio()
            val recuperada = segundaApertura.observarRutaDelDia(acopiadorId = "A-05", fecha = hoy).first()

            assertNotNull(recuperada)
            assertEquals("Ruta Test Persistencia", recuperada.nombre)
            assertEquals(1, recuperada.paradas.size)
        }
    }
}
