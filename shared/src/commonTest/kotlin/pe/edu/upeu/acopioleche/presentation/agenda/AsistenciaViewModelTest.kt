package pe.edu.upeu.acopioleche.presentation.agenda

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeAsistenciaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.data.fake.FakeReunionRepository
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class AsistenciaViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = FakeAsistenciaRepository(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = ReunionRepositoryQueNuncaEmite(),
            reunionId = "R-08",
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de convocados cuando la reunion tiene asistencia registrada`() = runBlocking {
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = FakeAsistenciaRepository(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = FakeReunionRepository(),
            reunionId = "R-08",
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<AsistenciaUiState>>(estado)
        assertTrue(estado.datos.convocados.isNotEmpty())
    }

    @Test
    fun `uiState es Exito con lista de convocados vacia cuando la reunion no tiene convocados`() = runBlocking {
        // R-07 existe en FakeReunionRepository pero no tiene registros en FakeAsistenciaRepository.
        // 0 convocados no es Vacio: la tarjeta de la reunion (tema/fecha/lugar) sigue siendo
        // informacion propia de la pantalla, asi que el estado correcto es Exito con la lista vacia.
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = FakeAsistenciaRepository(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = FakeReunionRepository(),
            reunionId = "R-07",
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<AsistenciaUiState>>(estado)
        assertTrue(estado.datos.convocados.isEmpty())
        assertTrue(estado.datos.temaReunion.isNotEmpty())
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando el flujo de asistencia falla`() = runBlocking {
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = AsistenciaRepositoryQueFalla(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = FakeReunionRepository(),
            reunionId = "R-08",
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    @Test
    fun `los mutadores no cambian el estado ni lanzan excepcion si uiState es Cargando`() {
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = FakeAsistenciaRepository(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = ReunionRepositoryQueNuncaEmite(),
            reunionId = "R-08",
        )

        viewModel.onToggleConvocado(actorId = "P-014")
        viewModel.onEscanearQr()

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `los mutadores no cambian el estado ni lanzan excepcion si uiState es Error`() = runBlocking {
        val viewModel = AsistenciaViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            asistenciaRepository = AsistenciaRepositoryQueFalla(),
            proveedorRepository = FakeProveedorRepository(),
            reunionRepository = FakeReunionRepository(),
            reunionId = "R-08",
        )

        viewModel.onToggleConvocado(actorId = "P-014")
        viewModel.onEscanearQr()

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class ReunionRepositoryQueNuncaEmite(
        private val delegado: ReunionRepository = FakeReunionRepository(),
    ) : ReunionRepository by delegado {
        override fun observarReuniones(): Flow<List<pe.edu.upeu.acopioleche.domain.model.Reunion>> = flow { /* nunca emite */ }
    }

    private class AsistenciaRepositoryQueFalla(
        private val delegado: AsistenciaRepository = FakeAsistenciaRepository(),
    ) : AsistenciaRepository by delegado {
        override fun observarAsistencia(reunionId: String): Flow<List<Asistencia>> = flow {
            throw RuntimeException("Fallo simulado de lectura de asistencia")
        }
    }
}
