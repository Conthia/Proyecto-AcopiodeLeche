package pe.edu.upeu.acopioleche.presentation.proveedor

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
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class ProveedoresViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = ProveedoresViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositoryQueNuncaEmite(),
            entregaRepository = FakeEntregaRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de proveedores cuando hay datos`() = runBlocking {
        val viewModel = ProveedoresViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = FakeProveedorRepository(),
            entregaRepository = FakeEntregaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<ProveedoresUiState>>(estado)
        assertTrue(estado.datos.proveedores.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay proveedores registrados`() = runBlocking {
        val viewModel = ProveedoresViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositoryVacio(),
            entregaRepository = FakeEntregaRepository(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de proveedores falla`() = runBlocking {
        val viewModel = ProveedoresViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            proveedorRepository = ProveedorRepositoryQueFalla(),
            entregaRepository = FakeEntregaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    /** Nunca emite: sirve para observar el valor inicial del `StateFlow` sin que el `combine` lo pise. */
    private class ProveedorRepositoryQueNuncaEmite(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = flow { /* nunca emite */ }
    }

    private class ProveedorRepositoryVacio(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = MutableStateFlow(emptyList())
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar), no al llamar al método. */
    private class ProveedorRepositoryQueFalla(
        private val delegado: ProveedorRepository = FakeProveedorRepository(),
    ) : ProveedorRepository by delegado {
        override fun observarProveedores(): Flow<List<Proveedor>> = flow {
            throw RuntimeException("Fallo simulado de lectura de proveedores")
        }
    }
}
