package pe.edu.upeu.acopioleche.presentation.centro

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeCentroAcopioRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEquipoCampoRepository
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class CentrosAcopioViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = CentrosAcopioViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryQueNuncaEmite(),
            entregaRepository = FakeEntregaRepository(),
            equipoCampoRepository = FakeEquipoCampoRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de centros cuando hay datos`() = runBlocking {
        val viewModel = CentrosAcopioViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = FakeCentroAcopioRepository(),
            entregaRepository = FakeEntregaRepository(),
            equipoCampoRepository = FakeEquipoCampoRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<CentrosAcopioUiState>>(estado)
        assertTrue(estado.datos.centros.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay centros registrados`() = runBlocking {
        val viewModel = CentrosAcopioViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryVacio(),
            entregaRepository = FakeEntregaRepository(),
            equipoCampoRepository = FakeEquipoCampoRepository(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de centros falla`() = runBlocking {
        val viewModel = CentrosAcopioViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            centroAcopioRepository = CentroAcopioRepositoryQueFalla(),
            entregaRepository = FakeEntregaRepository(),
            equipoCampoRepository = FakeEquipoCampoRepository(),
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
        override fun observarCentros(): Flow<List<CentroAcopio>> = MutableStateFlow(emptyList())
    }

    private class CentroAcopioRepositoryQueFalla(
        private val delegado: CentroAcopioRepository = FakeCentroAcopioRepository(),
    ) : CentroAcopioRepository by delegado {
        override fun observarCentros(): Flow<List<CentroAcopio>> = flow {
            throw RuntimeException("Fallo simulado de lectura de centros")
        }
    }
}
