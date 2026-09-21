package pe.edu.upeu.acopioleche.presentation.reportes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeCentroAcopioRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class ReportesViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = ReportesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryQueNuncaEmite(),
            entregaRepository = FakeEntregaRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con el catalogo de reportes y el volumen por comunidad`() = runBlocking {
        val viewModel = ReportesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = FakeCentroAcopioRepository(),
            entregaRepository = FakeEntregaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<ReportesUiState>>(estado)
        assertTrue(estado.datos.reportes.isNotEmpty())
    }

    @Test
    fun `uiState es Exito con volumenPorComunidad vacio cuando no hay centros (no Vacio)`() = runBlocking {
        // El catalogo de reportes es informacion propia de la pantalla, independiente del
        // volumen reactivo, asi que la pantalla nunca es Vacio (igual que la tarjeta de
        // reunion en AsistenciaViewModel).
        val viewModel = ReportesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryVacio(),
            entregaRepository = FakeEntregaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<ReportesUiState>>(estado)
        assertTrue(estado.datos.reportes.isNotEmpty())
        assertTrue(estado.datos.volumenPorComunidad.isEmpty())
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de centros falla`() = runBlocking {
        val viewModel = ReportesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryQueFalla(),
            entregaRepository = FakeEntregaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class CentroAcopioRepositoryQueNuncaEmite(
        private val delegado: CentroAcopioRepository = FakeCentroAcopioRepository(),
    ) : CentroAcopioRepository by delegado {
        override fun observarCentros(): Flow<List<CentroAcopio>> = flow { /* nunca emite */ }
    }

    private class CentroAcopioRepositoryVacio(
        private val delegado: CentroAcopioRepository = FakeCentroAcopioRepository(),
    ) : CentroAcopioRepository by delegado {
        override fun observarCentros(): Flow<List<CentroAcopio>> = flow { emit(emptyList()) }
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar), no al llamar al método. */
    private class CentroAcopioRepositoryQueFalla(
        private val delegado: CentroAcopioRepository = FakeCentroAcopioRepository(),
    ) : CentroAcopioRepository by delegado {
        override fun observarCentros(): Flow<List<CentroAcopio>> = flow {
            throw RuntimeException("Fallo simulado de lectura de centros")
        }
    }
}
