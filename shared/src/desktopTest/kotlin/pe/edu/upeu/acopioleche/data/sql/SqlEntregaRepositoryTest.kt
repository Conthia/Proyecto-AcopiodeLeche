package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Turno

/**
 * Verifica RNF-03 con la base real (no la Fake): los datos deben sobrevivir a un "reinicio" —
 * simulado abriendo un segundo driver/Database sobre el mismo archivo, tal como pasaría al
 * volver a abrir la app.
 */
class SqlEntregaRepositoryTest {

    private val archivo = File.createTempFile("acopioleche-test-", ".db")
    private val url = "jdbc:sqlite:${archivo.absolutePath}"
    private val hoy: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    @AfterTest
    fun limpiar() {
        archivo.delete()
    }

    private fun abrirRepositorio(): SqlEntregaRepository {
        val driver = JdbcSqliteDriver(url)
        if (!archivo.exists() || archivo.length() == 0L) {
            AcopioLecheDatabase.Schema.create(driver)
        }
        return SqlEntregaRepository(AcopioLecheDatabase(driver))
    }

    @Test
    fun `no hay ninguna entrega precargada al abrir la base por primera vez`() = runBlocking {
        val repo = abrirRepositorio()

        assertTrue(repo.observarEntregasDeHoy().first().isEmpty())
        assertTrue(repo.observarTodasLasEntregas().first().isEmpty())
    }

    @Test
    fun `observarTodasLasEntregas incluye entregas de cualquier proveedor y fecha`() = runBlocking {
        val repo = abrirRepositorio()
        val nueva = Entrega(
            id = "E-TEST-TODAS",
            proveedorId = "P-041",
            acopiadorId = "A-01",
            centroAcopioId = "CA-002",
            fecha = hoy,
            turno = Turno.MANANA,
            volumenLitros = 20.0,
            estado = EstadoEntrega.Aceptada,
            cantidadPorongos = 1,
        )

        repo.registrar(nueva)

        val todas = repo.observarTodasLasEntregas().first()
        assertTrue(todas.any { it.id == "E-TEST-TODAS" })
    }

    @Test
    fun `registrar agrega la entrega a la cola de pendientes`() = runBlocking {
        val repo = abrirRepositorio()
        val nueva = Entrega(
            id = "E-TEST-1",
            proveedorId = "P-014",
            acopiadorId = "A-01",
            centroAcopioId = "CA-002",
            fecha = hoy,
            turno = Turno.MANANA,
            volumenLitros = 30.0,
            estado = EstadoEntrega.Pendiente,
            cantidadPorongos = 1,
        )

        repo.registrar(nueva)

        val pendientes = repo.observarPendientesDeSincronizar().first()
        assertTrue(pendientes.any { it.id == "E-TEST-1" })
    }

    @Test
    fun `sincronizarPendientes acepta las pendientes y vacia la cola`() = runBlocking {
        val repo = abrirRepositorio()
        val antes = repo.observarPendientesDeSincronizar().first().size

        val enviados = repo.sincronizarPendientes()

        assertEquals(expected = antes, actual = enviados)
        assertTrue(repo.observarPendientesDeSincronizar().first().isEmpty())
        val entregas = repo.observarEntregasDeHoy().first()
        assertTrue(entregas.all { it.estado is EstadoEntrega.Aceptada || it.estado !is EstadoEntrega.Pendiente })
    }

    @Test
    fun `los datos sobreviven a reabrir la base de datos, como al reiniciar la app`() = runBlocking {
        val primeraApertura = abrirRepositorio()
        val nueva = Entrega(
            id = "E-TEST-2",
            proveedorId = "P-003",
            acopiadorId = "A-02",
            centroAcopioId = "CA-001",
            fecha = hoy,
            turno = Turno.TARDE,
            volumenLitros = 12.5,
            estado = EstadoEntrega.Pendiente,
            cantidadPorongos = 1,
        )
        primeraApertura.registrar(nueva)

        val segundaApertura = abrirRepositorio()
        val entregas = segundaApertura.observarEntregasDeHoy().first()

        assertTrue(entregas.any { it.id == "E-TEST-2" })
    }
}
