package pe.edu.upeu.acopioleche.presentation.notificacion

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
import pe.edu.upeu.acopioleche.data.fake.FakeNotificacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.domain.model.Notificacion
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class NotificacionesViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = NotificacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            notificacionRepository = NotificacionRepositoryQueNuncaEmite(),
            proveedorRepository = FakeProveedorRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de notificaciones cuando hay datos`() = runBlocking {
        val viewModel = NotificacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            notificacionRepository = FakeNotificacionRepository(),
            proveedorRepository = FakeProveedorRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<NotificacionesUiState>>(estado)
        assertTrue(estado.datos.notificaciones.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay notificaciones registradas`() = runBlocking {
        val viewModel = NotificacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            notificacionRepository = NotificacionRepositoryVacio(),
            proveedorRepository = FakeProveedorRepository(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de notificaciones falla`() = runBlocking {
        val viewModel = NotificacionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            notificacionRepository = NotificacionRepositoryQueFalla(),
            proveedorRepository = FakeProveedorRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class NotificacionRepositoryQueNuncaEmite(
        private val delegado: NotificacionRepository = FakeNotificacionRepository(),
    ) : NotificacionRepository by delegado {
        override fun observarTodas(): Flow<List<Notificacion>> = flow { /* nunca emite */ }
    }

    private class NotificacionRepositoryVacio(
        private val delegado: NotificacionRepository = FakeNotificacionRepository(),
    ) : NotificacionRepository by delegado {
        override fun observarTodas(): Flow<List<Notificacion>> = MutableStateFlow(emptyList())
    }

    /** La excepción se lanza DENTRO del `Flow` (al recolectar), no al llamar al método. */
    private class NotificacionRepositoryQueFalla(
        private val delegado: NotificacionRepository = FakeNotificacionRepository(),
    ) : NotificacionRepository by delegado {
        override fun observarTodas(): Flow<List<Notificacion>> = flow {
            throw RuntimeException("Fallo simulado de lectura de notificaciones")
        }
    }
}
