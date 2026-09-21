package pe.edu.upeu.acopioleche.presentation.liquidacion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeLiquidacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakeNotificacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakePrecioTemporadaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.data.fake.FakeSancionRepository
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio
import pe.edu.upeu.acopioleche.presentation.core.UiState

class LiquidacionesViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositoryQueNuncaEmite(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = FakeLiquidacionRepository(),
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = FakePrecioTemporadaRepository(),
            reglasNegocio = ReglasNegocio(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con un resumen por proveedor activo`() = runBlocking {
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = FakeLiquidacionRepository(),
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = FakePrecioTemporadaRepository(),
            reglasNegocio = ReglasNegocio(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<LiquidacionesUiState>>(estado)
        assertTrue(estado.datos.resumenes.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay proveedores activos`() = runBlocking {
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositorySinActivos(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = FakeLiquidacionRepository(),
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = FakePrecioTemporadaRepository(),
            reglasNegocio = ReglasNegocio(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando falla la carga de proveedores`() = runBlocking {
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositoryQueFalla(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = FakeLiquidacionRepository(),
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = FakePrecioTemporadaRepository(),
            reglasNegocio = ReglasNegocio(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    @Test
    fun `onGenerarClick usa el precio de temporada vigente, no el respaldo`() = runBlocking {
        val liquidacionRepo = FakeLiquidacionRepository()
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = liquidacionRepo,
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = PrecioTemporadaRepositoryFijo(
                precioTemporadaFija(precioPorLitro = 2.20),
            ),
            reglasNegocio = ReglasNegocio(),
        )

        viewModel.onGenerarClick()

        val generadas = liquidacionRepo.observarTodas().first()
        assertTrue(generadas.isNotEmpty())
        generadas.forEach { assertEquals(expected = 2.20, actual = it.precioPorLitroAplicado) }
    }

    @Test
    fun `onGenerarClick usa el respaldo cuando no hay precio de temporada vigente`() = runBlocking {
        val liquidacionRepo = FakeLiquidacionRepository()
        val reglasNegocio = ReglasNegocio()
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = liquidacionRepo,
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = PrecioTemporadaRepositoryFijo(precio = null),
            reglasNegocio = reglasNegocio,
        )

        viewModel.onGenerarClick()

        val generadas = liquidacionRepo.observarTodas().first()
        assertTrue(generadas.isNotEmpty())
        generadas.forEach {
            assertEquals(expected = reglasNegocio.precioReferenciaPorLitro, actual = it.precioPorLitroAplicado)
        }
    }

    @Test
    fun `una liquidacion ya generada no cambia de monto si despues cambia el precio de temporada`() = runBlocking {
        val liquidacionRepo = FakeLiquidacionRepository()
        val precioRepo = PrecioTemporadaRepositoryFijo(precioTemporadaFija(precioPorLitro = 1.70))
        val viewModel = LiquidacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = FakeEntregaRepository(),
            sancionRepository = FakeSancionRepository(),
            liquidacionRepository = liquidacionRepo,
            notificacionRepository = FakeNotificacionRepository(),
            precioTemporadaRepository = precioRepo,
            reglasNegocio = ReglasNegocio(),
        )

        viewModel.onGenerarClick()
        val montosAntes = liquidacionRepo.observarTodas().first().associate { it.id to it.montoFinal }
        assertTrue(montosAntes.isNotEmpty())

        // Cambia el precio vigente DESPUES de generar; las liquidaciones ya persistidas para esa
        // semana no deben regenerarse (cargar()/generarPara() son idempotentes: solo generan si
        // liquidacionRepository.buscar(...) devuelve null).
        precioRepo.precio = precioTemporadaFija(precioPorLitro = 9.99)
        viewModel.onGenerarClick()

        val montosDespues = liquidacionRepo.observarTodas().first().associate { it.id to it.montoFinal }
        assertEquals(expected = montosAntes, actual = montosDespues)
    }

    /**
     * A diferencia de los ViewModel basados en `combine()`, `LiquidacionesViewModel.cargar()` usa
     * `.first()`: un `flow { }` vacío completa sin emitir y `.first()` lanzaría
     * `NoSuchElementException` de inmediato (terminaría en `Error`, no en `Cargando`). Se necesita
     * un flow que de verdad quede suspendido para siempre.
     */
    private class ProveedorRepositoryQueNuncaEmite(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = flow { awaitCancellation() }
    }

    private class ProveedorRepositorySinActivos(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = MutableStateFlow(emptyList())
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar con `.first()`), no al llamar al método. */
    private class ProveedorRepositoryQueFalla(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = flow {
            throw RuntimeException("Fallo simulado de lectura de proveedores")
        }
    }

    /** Devuelve siempre [precio] (o `null`), sin importar la fecha consultada ni el catálogo sembrado. */
    private class PrecioTemporadaRepositoryFijo(
        var precio: PrecioTemporada?,
        private val delegado: PrecioTemporadaRepository = FakePrecioTemporadaRepository(),
    ) : PrecioTemporadaRepository by delegado {
        override suspend fun obtenerPrecioVigenteEn(fecha: LocalDate): PrecioTemporada? = precio
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
