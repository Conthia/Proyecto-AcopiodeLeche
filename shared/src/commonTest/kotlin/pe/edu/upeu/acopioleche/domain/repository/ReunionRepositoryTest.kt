package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.fake.FakeReunionRepository
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ReunionRepositoryTest {

    private val repository = FakeReunionRepository()

    @Test
    fun observarReunionesDevuelveLaListaInicial() {
        runBlocking {
            val lista = repository.observarReuniones().first()
            assertFalse(lista.isEmpty())
        }
    }

    @Test
    fun guardarAgregaUnaNuevaReunion() {
        runBlocking {
            val nueva = Reunion(
                id = "R-99",
                tipo = TipoEvento.CAPACITACION,
                tema = "Buenas Practicas de Ordeño",
                fecha = LocalDate(2026, 10, 1),
                horaInicioMinutos = 540,
                horaFinMinutos = 660,
                lugar = "Sede Central",
            )
            repository.guardar(nueva)
            val lista = repository.observarReuniones().first()
            assertNotNull(lista.find { it.id == "R-99" })
        }
    }

    @Test
    fun actualizarModificaUnaReunionExistente() {
        runBlocking {
            val actual = repository.observarReuniones().first().first()
            val modificada = actual.copy(tema = "Tema Modificado")
            repository.actualizar(modificada)

            val lista = repository.observarReuniones().first()
            val buscada = lista.find { it.id == actual.id }
            assertNotNull(buscada)
            assertEquals("Tema Modificado", buscada.tema)
        }
    }

    @Test
    fun eliminarRemueveUnaReunionPorId() {
        runBlocking {
            val actual = repository.observarReuniones().first().first()
            repository.eliminar(actual.id)

            val lista = repository.observarReuniones().first()
            assertEquals(null, lista.find { it.id == actual.id })
        }
    }
}
