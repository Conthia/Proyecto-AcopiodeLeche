package pe.edu.upeu.acopioleche.presentation.dashboard.pagos

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeLiquidacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakeNotificacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakePagoRepository
import pe.edu.upeu.acopioleche.data.fake.FakePrecioTemporadaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.data.fake.FakeSancionRepository
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.SancionAplicada
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio
import pe.edu.upeu.acopioleche.presentation.liquidacion.LiquidacionesViewModel

/**
 * Verifica que la vista en vivo de Pagos y una liquidación realmente generada por
 * `LiquidacionesViewModel` coincidan en `montoFinal` para la misma semana y el mismo proveedor —
 * ambas pasan por `CalculadoraLiquidacion` con el mismo precio
 * (`CalculadoraLiquidacion.fechaReferenciaPrecio`) y la misma lógica de sanción (RN-10).
 */
class PagosHomeViewModelTest {

    private val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // Misma fórmula que `LiquidacionesViewModel.semanaMostrada`: la última semana ya cerrada.
    private val semanaInicio = CicloSemanal.inicioDeSemana(hoy).minus(7, DateTimeUnit.DAY)

    private fun entregaDeLaSemana(id: String, litros: Double) = Entrega(
        id = id,
        proveedorId = "P-014",
        acopiadorId = "A-01",
        centroAcopioId = "CA-002",
        fecha = semanaInicio.plus(1, DateTimeUnit.DAY),
        turno = Turno.MANANA,
        volumenLitros = litros,
        estado = EstadoEntrega.Aceptada,
        cantidadPorongos = 1,
    )

    @Test
    fun `Pagos en vivo y Liquidaciones generan el mismo montoFinal, sin sancion pendiente`() = runBlocking {
        val entregaRepo = FakeEntregaRepository()
        entregaRepo.registrar(entregaDeLaSemana(id = "E-COMPARA-1", litros = 40.0))
        val sancionRepo = FakeSancionRepository()
        val precioRepo = PrecioTemporadaRepositoryFijo(precioTemporadaFija(precioPorLitro = 1.90))

        val (montoLiquidaciones, montoPagos) = generarYCompararMontos(entregaRepo, sancionRepo, precioRepo)

        assertEquals(expected = montoLiquidaciones, actual = montoPagos)
        assertEquals(expected = 76.0, actual = montoLiquidaciones) // 40.0 L x S/1.90, sin sancion
    }

    @Test
    fun `Pagos en vivo y Liquidaciones generan el mismo montoFinal, con sancion pendiente (RN-10)`() = runBlocking {
        val entregaRepo = FakeEntregaRepository()
        entregaRepo.registrar(entregaDeLaSemana(id = "E-COMPARA-2", litros = 40.0))
        val sancionRepo = FakeSancionRepository()
        sancionRepo.registrar(
            SancionAplicada.ReduccionPrecioSemanal(
                id = "S-COMPARA-1",
                proveedorId = "P-014",
                fecha = semanaInicio,
                semanaInicio = semanaInicio,
            ),
        )
        val precioRepo = PrecioTemporadaRepositoryFijo(precioTemporadaFija(precioPorLitro = 1.90))

        val (montoLiquidaciones, montoPagos) = generarYCompararMontos(entregaRepo, sancionRepo, precioRepo)

        assertEquals(expected = montoLiquidaciones, actual = montoPagos)
        assertTrue(montoLiquidaciones < 76.0) // debe reflejar la reduccion por adulteracion (RN-10)
    }

    /** Genera la liquidación real con `LiquidacionesViewModel` y calcula la vista en vivo con
     * `PagosHomeViewModel`, ambas para [semanaInicio] y el proveedor P-014, y devuelve sus
     * `montoFinal` (liquidaciones, pagos) para comparar. */
    private suspend fun generarYCompararMontos(
        entregaRepo: FakeEntregaRepository,
        sancionRepo: FakeSancionRepository,
        precioRepo: PrecioTemporadaRepository,
    ): Pair<Double, Double> {
        val liquidacionRepo = FakeLiquidacionRepository()
        val liquidacionesViewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = entregaRepo,
            sancionRepository = sancionRepo,
            liquidacionRepository = liquidacionRepo,
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = precioRepo,
            reglasNegocio = ReglasNegocio(),
        )
        liquidacionesViewModel.onGenerarClick()
        val generada = liquidacionRepo.buscar(proveedorId = "P-014", semanaInicio = semanaInicio)
        assertNotNull(generada)

        val pagosViewModel = PagosHomeViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            liquidacionRepository = FakeLiquidacionRepository(),
            pagoRepository = FakePagoRepository(),
            precioTemporadaRepository = precioRepo,
            sancionRepository = sancionRepo,
            reglasNegocio = ReglasNegocio(),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = entregaRepo,
            encargadoId = "PAGOS-01",
            nombreEncargado = "Encargado de prueba",
        )
        val entregasDeLaSemana = entregaRepo.observarTodasLasEntregas().first()
        val sanciones = sancionRepo.observarTodas().first()
        val enVivo = pagosViewModel.calcularLiquidacionesSemanaActual(
            entregas = entregasDeLaSemana,
            sanciones = sanciones,
            semanaInicio = semanaInicio,
        )
        val enVivoP014 = enVivo.find { it.proveedorId == "P-014" }
        assertNotNull(enVivoP014)

        return generada.montoFinal to enVivoP014.montoFinal
    }

    /** Devuelve siempre [precio], sin importar la fecha consultada. */
    private class PrecioTemporadaRepositoryFijo(
        private val precio: PrecioTemporada,
        private val delegado: PrecioTemporadaRepository = FakePrecioTemporadaRepository(),
    ) : PrecioTemporadaRepository by delegado {
        override suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioTemporada = precio
    }

    private companion object {
        fun precioTemporadaFija(precioPorLitro: Double): PrecioTemporada = PrecioTemporada(
            id = "PT-FIJO",
            nombreTemporada = "Fija para test",
            fechaInicio = LocalDate(2000, 1, 1),
            fechaFin = LocalDate(2100, 1, 1),
            precioPorLitro = precioPorLitro,
        )
    }
}
