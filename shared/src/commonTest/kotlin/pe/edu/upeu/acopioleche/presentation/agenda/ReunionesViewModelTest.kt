package pe.edu.upeu.acopioleche.presentation.agenda

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
import pe.edu.upeu.acopioleche.data.fake.FakeAsistenciaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeReunionRepository
import pe.edu.upeu.acopioleche.domain.model.Asistencia
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.presentation.core.UiState

class ReunionesViewModelTest {

    @Test
    fun `el estado inicial es Cargando antes de la primera emision`() {
        val viewModel = ReunionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            reunionRepository = ReunionRepositoryQueNuncaEmite(),
            asistenciaRepository = FakeAsistenciaRepository(),
        )

        assertEquals(expected = UiState.Cargando, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Exito con la lista de reuniones cuando hay datos`() = runBlocking {
        val viewModel = ReunionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            reunionRepository = FakeReunionRepository(),
            asistenciaRepository = FakeAsistenciaRepository(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Exito<ReunionesUiState>>(estado)
        assertTrue(estado.datos.reuniones.isNotEmpty())
    }

    @Test
    fun `uiState es Vacio cuando no hay reuniones registradas`() = runBlocking {
        val viewModel = ReunionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            reunionRepository = ReunionRepositoryVacio(),
            asistenciaRepository = FakeAsistenciaRepository(),
        )

        assertEquals(expected = UiState.Vacio, actual = viewModel.uiState.value)
    }

    @Test
    fun `uiState es Error con mensaje fijo cuando falla un flujo interno de asistencia`() = runBlocking {
        // Prueba explícitamente el ajuste pedido: el .catch va al final de TODA la cadena
        // (después del flatMapLatest), así que también debe atrapar un fallo del Flow de
        // asistencia (interno), no solo uno de observarReuniones().
        val viewModel = ReunionesViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            reunionRepository = FakeReunionRepository(),
            asistenciaRepository = AsistenciaRepositoryQueFalla(),
        )

        val estado = viewModel.uiState.value
        assertIs<UiState.Error>(estado)
        assertEquals(expected = "No se pudo cargar la información", actual = estado.mensaje)
    }

    private class ReunionRepositoryQueNuncaEmite(
        private val delegado: ReunionRepository = FakeReunionRepository(),
    ) : ReunionRepository by delegado {
        override fun observarReuniones(): Flow<List<Reunion>> = flow { /* nunca emite */ }
    }

    private class ReunionRepositoryVacio(
        private val delegado: ReunionRepository = FakeReunionRepository(),
    ) : ReunionRepository by delegado {
        override fun observarReuniones(): Flow<List<Reunion>> = MutableStateFlow(emptyList())
    }

    private class AsistenciaRepositoryQueFalla(
        private val delegado: AsistenciaRepository = FakeAsistenciaRepository(),
    ) : AsistenciaRepository by delegado {
        override fun observarAsistencia(reunionId: String): Flow<List<Asistencia>> = flow {
            throw RuntimeException("Fallo simulado de lectura de asistencia")
        }
    }
}
