package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeCentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class CentroAcopioRepositoryTest {

    private val repository = FakeCentroAcopioRepository()

    @Test
    fun observarCentrosDevuelveLaListaInicial() {
        runBlocking {
            val lista = repository.observarCentros().first()
            assertFalse(lista.isEmpty())
        }
    }

    @Test
    fun guardarAgregaUnNuevoCentro() {
        runBlocking {
            val nuevo = CentroAcopio(
                id = "CA-009",
                nombre = "Nuevo Centro Sector",
                ubicacion = "Plaza sector",
                capacidadLitrosDia = 500.0,
                activo = true,
                capacidadTanqueLitros = 800.0,
            )
            repository.guardar(nuevo)
            val lista = repository.observarCentros().first()
            assertNotNull(lista.find { it.id == "CA-009" })
        }
    }

    @Test
    fun actualizarModificaUnCentroExistente() {
        runBlocking {
            val actual = repository.observarCentros().first().first()
            val modificado = actual.copy(nombre = "Centro Actualizado", activo = false)
            repository.actualizar(modificado)

            val lista = repository.observarCentros().first()
            val buscado = lista.find { it.id == actual.id }
            assertNotNull(buscado)
            assertEquals("Centro Actualizado", buscado.nombre)
            assertFalse(buscado.activo)
        }
    }

    @Test
    fun eliminarRemueveUnCentroPorId() {
        runBlocking {
            val actual = repository.observarCentros().first().first()
            repository.eliminar(actual.id)

            val lista = repository.observarCentros().first()
            assertEquals(null, lista.find { it.id == actual.id })
        }
    }
}
