package pe.edu.upeu.acopioleche.presentation.dashboard.lacteos

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
import pe.edu.upeu.acopioleche.data.fake.FakeInsumoRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class LacteosHomeViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = LacteosHomeViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            produccionDerivadoRepository = ProduccionDerivadoRepositoryQueNuncaEmite(),
            insumoRepository = FakeInsumoRepository(),
            nombreResponsable = "Marisol",
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito cuando hay producciones o insumos`() = runBlocking {
        val viewModel = LacteosHomeViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            produccionDerivadoRepository = FakeProduccionDerivadoRepository(),
            insumoRepository = FakeInsumoRepository(),
            nombreResponsable = "Marisol",
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<LacteosHomeUiState>>(estado)
        assertEquals(expected = "Marisol", actual = estado.datos.nombreResponsable)
        assertTrue(estado.datos.producciones.isNotEmpty() || estado.datos.insumos.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay producciones ni insumos`() = runBlocking {
        val viewModel = LacteosHomeViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            produccionDerivadoRepository = ProduccionDerivadoRepositoryVacio(),
            insumoRepository = InsumoRepositoryVacio(),
            nombreResponsable = "Marisol",
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de produccion falla`() = runBlocking {
        val viewModel = LacteosHomeViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            produccionDerivadoRepository = ProduccionDerivadoRepositoryQueFalla(),
            insumoRepository = FakeInsumoRepository(),
            nombreResponsable = "Marisol",
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class ProduccionDerivadoRepositoryQueNuncaEmite(
        private val delegado: ProduccionDerivadoRepository = FakeProduccionDerivadoRepository(),
    ) : ProduccionDerivadoRepository by delegado {
        override fun observarProduccion(): Flow<List<ProduccionDerivado>> = flow { /* nunca emite */ }
    }

    private class ProduccionDerivadoRepositoryVacio(
        private val delegado: ProduccionDerivadoRepository = FakeProduccionDerivadoRepository(),
    ) : ProduccionDerivadoRepository by delegado {
        override fun observarProduccion(): Flow<List<ProduccionDerivado>> = MutableStateFlow(emptyList())
    }

    private class InsumoRepositoryVacio(
        private val delegado: pe.edu.upeu.acopioleche.domain.repository.InsumoRepository = FakeInsumoRepository(),
    ) : pe.edu.upeu.acopioleche.domain.repository.InsumoRepository by delegado {
        override fun observarInsumos(): Flow<List<pe.edu.upeu.acopioleche.domain.model.InsumoLacteo>> = MutableStateFlow(emptyList())
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar), no al llamar al método. */
    private class ProduccionDerivadoRepositoryQueFalla(
        private val delegado: ProduccionDerivadoRepository = FakeProduccionDerivadoRepository(),
    ) : ProduccionDerivadoRepository by delegado {
        override fun observarProduccion(): Flow<List<ProduccionDerivado>> = flow {
            throw RuntimeException("Fallo simulado de lectura de producción")
        }
    }
}
