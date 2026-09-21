package pe.edu.upeu.acopioleche.data.sql

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.domain.model.Liquidacion

class SqlLiquidacionRepositoryTest {

    private val archivo = File.createTempFile("acopioleche-test-", ".db")
    private val url = "jdbc:sqlite:${archivo.absolutePath}"

    @AfterTest
    fun limpiar() {
        archivo.delete()
    }

    private fun abrirRepositorio(): SqlLiquidacionRepository {
        val driver = JdbcSqliteDriver(url)
        if (!archivo.exists() || archivo.length() == 0L) {
            AcopioLecheDatabase.Schema.create(driver)
        }
        return SqlLiquidacionRepository(AcopioLecheDatabase(driver))
    }

    @Test
    fun `precioPorLitroAplicado sobrevive un viaje de ida y vuelta por la base real`() = runBlocking {
        val repo = abrirRepositorio()
        val liquidacion = Liquidacion(
            id = "LIQ-TEST-1",
            proveedorId = "P-014",
            semanaInicio = LocalDate(2026, 9, 3),
            litrosAceptados = 120.0,
            montoBruto = 228.0,
            montoFinal = 228.0,
            tieneSancionPendienteDeMonto = false,
            fechaPago = LocalDate(2026, 9, 11),
            generadaAutomaticamente = true,
            precioPorLitroAplicado = 1.90,
        )

        repo.registrar(liquidacion)

        val recuperada = repo.buscar(proveedorId = "P-014", semanaInicio = LocalDate(2026, 9, 3))

        assertNotNull(recuperada)
        assertEquals(expected = 1.90, actual = recuperada.precioPorLitroAplicado)
    }

    @Test
    fun `precioPorLitroAplicado por defecto es 1_70 para una liquidacion que no lo especifica`() = runBlocking {
        val repo = abrirRepositorio()
        val liquidacion = Liquidacion(
            id = "LIQ-TEST-2",
            proveedorId = "P-027",
            semanaInicio = LocalDate(2026, 9, 3),
            litrosAceptados = 50.0,
            montoBruto = 85.0,
            montoFinal = 85.0,
            tieneSancionPendienteDeMonto = false,
            fechaPago = LocalDate(2026, 9, 11),
            generadaAutomaticamente = true,
        )

        repo.registrar(liquidacion)

        val recuperada = repo.buscar(proveedorId = "P-027", semanaInicio = LocalDate(2026, 9, 3))

        assertNotNull(recuperada)
        assertEquals(expected = 1.70, actual = recuperada.precioPorLitroAplicado)
    }
}
