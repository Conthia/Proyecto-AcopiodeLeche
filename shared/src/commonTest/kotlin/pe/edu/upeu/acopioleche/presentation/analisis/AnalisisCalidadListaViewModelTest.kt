package pe.edu.upeu.acopioleche.presentation.analisis

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
import pe.edu.upeu.acopioleche.data.fake.FakeAnalisisCalidadRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class AnalisisCalidadListaViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = AnalisisCalidadListaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            entregaRepository = EntregaRepositoryQueNuncaEmite(),
            proveedorRepository = FakeProveedorRepository(),
            analisisCalidadRepository = FakeAnalisisCalidadRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de pendientes cuando hay entregas sin analizar`() = runBlocking {
        // E-1042 (FakeEntregaRepository) no tiene analisis en FakeAnalisisCalidadRepository.
        val viewModel = AnalisisCalidadListaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            entregaRepository = FakeEntregaRepository(),
            proveedorRepository = FakeProveedorRepository(),
            analisisCalidadRepository = FakeAnalisisCalidadRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<AnalisisCalidadListaUiState>>(estado)
        assertTrue(estado.datos.pendientes.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay entregas pendientes de analisis`() = runBlocking {
        val viewModel = AnalisisCalidadListaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            entregaRepository = EntregaRepositoryVacio(),
            proveedorRepository = FakeProveedorRepository(),
            analisisCalidadRepository = FakeAnalisisCalidadRepository(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de entregas falla`() = runBlocking {
        val viewModel = AnalisisCalidadListaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            entregaRepository = EntregaRepositoryQueFalla(),
            proveedorRepository = FakeProveedorRepository(),
            analisisCalidadRepository = FakeAnalisisCalidadRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class EntregaRepositoryQueNuncaEmite(
        private val delegado: EntregaRepository = FakeEntregaRepository(),
    ) : EntregaRepository by delegado {
        override fun observarEntregasDeHoy(): Flow<List<Entrega>> = flow { /* nunca emite */ }
    }

    private class EntregaRepositoryVacio(
        private val delegado: EntregaRepository = FakeEntregaRepository(),
    ) : EntregaRepository by delegado {
        override fun observarEntregasDeHoy(): Flow<List<Entrega>> = MutableStateFlow(emptyList())
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar), no al llamar al método. */
    private class EntregaRepositoryQueFalla(
        private val delegado: EntregaRepository = FakeEntregaRepository(),
    ) : EntregaRepository by delegado {
        override fun observarEntregasDeHoy(): Flow<List<Entrega>> = flow {
            throw RuntimeException("Fallo simulado de lectura de entregas")
        }
    }
}
