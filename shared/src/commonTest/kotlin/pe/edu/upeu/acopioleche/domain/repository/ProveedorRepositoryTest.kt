package pe.edu.upeu.acopioleche.domain.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Sector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ProveedorRepositoryTest {

    private val repository = FakeProveedorRepository()

    @Test
    fun observarProveedoresDevuelveLaListaInicial() {
        runBlocking {
            val lista = repository.observarProveedores().first()
            assertFalse(lista.isEmpty())
        }
    }

    @Test
    fun guardarAgregaUnNuevoProveedor() {
        runBlocking {
            val nuevo = Proveedor(
                id = "P-999",
                nombre = "Pedro Mamani",
                documento = "44332211",
                telefono = "951000111",
                sector = Sector.NORTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 8,
                calificacion = CalificacionProveedor.A,
            )
            repository.guardar(nuevo)
            val lista = repository.observarProveedores().first()
            assertNotNull(lista.find { it.id == "P-999" })
        }
    }

    @Test
    fun actualizarModificaUnProveedorExistente() {
        runBlocking {
            val actual = repository.observarProveedores().first().first()
            val modificado = actual.copy(nombre = "Nombre Modificado", activo = false)
            repository.actualizar(modificado)

            val lista = repository.observarProveedores().first()
            val buscado = lista.find { it.id == actual.id }
            assertNotNull(buscado)
            assertEquals("Nombre Modificado", buscado.nombre)
            assertFalse(buscado.activo)
        }
    }

    @Test
    fun eliminarRemueveUnProveedorPorId() {
        runBlocking {
            val actual = repository.observarProveedores().first().first()
            repository.eliminar(actual.id)

            val lista = repository.observarProveedores().first()
            assertEquals(null, lista.find { it.id == actual.id })
        }
    }
}
