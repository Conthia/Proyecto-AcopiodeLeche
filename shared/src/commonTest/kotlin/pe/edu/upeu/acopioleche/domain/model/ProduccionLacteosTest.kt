package pe.edu.upeu.acopioleche.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.fake.FakeInsumoRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.presentation.dashboard.lacteos.LacteosHomeViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ProduccionLacteosTest {

    private val fechaPrueba = LocalDate(2026, 9, 15)

    @Test
    fun guardarYEliminarProduccionDeDerivadosLacteos() {
        runBlocking {
            val repository = FakeProduccionDerivadoRepository()
            val nuevo = ProduccionDerivado(
                id = "PROD-TEST-99",
                tipoProducto = "Queso Mantequilloso",
                codigoLote = "LOTE-TEST-99",
                cantidadUnidades = 30.0,
                fechaProduccion = fechaPrueba,
                responsableId = "PROD-LACT-01",
            )
            repository.guardar(nuevo)

            val lista = repository.observarProduccion().first()
            assertNotNull(lista.find { it.id == "PROD-TEST-99" })

            repository.eliminar("PROD-TEST-99")
            val listaTrasEliminar = repository.observarProduccion().first()
            assertEquals(null, listaTrasEliminar.find { it.id == "PROD-TEST-99" })
        }
    }

    @Test
    fun guardarYEliminarInsumosLacteos() {
        runBlocking {
            val repository = FakeInsumoRepository()
            val nuevoInsumo = InsumoLacteo(
                id = "INS-TEST-88",
                nombreInsumo = "Cloruro de Calcio",
                cantidad = 2.5,
                unidadMedida = "Litros",
                fechaIngreso = fechaPrueba,
            )
            repository.guardar(nuevoInsumo)

            val lista = repository.observarInsumos().first()
            assertNotNull(lista.find { it.id == "INS-TEST-88" })

            repository.eliminar("INS-TEST-88")
            val listaTrasEliminar = repository.observarInsumos().first()
            assertEquals(null, listaTrasEliminar.find { it.id == "INS-TEST-88" })
        }
    }

    @Test
    fun lacteosHomeViewModel_observaProduccionEInsumos() {
        runBlocking {
            val repoProduccion = FakeProduccionDerivadoRepository()
            val repoInsumos = FakeInsumoRepository()

            val viewModel = LacteosHomeViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                produccionDerivadoRepository = repoProduccion,
                insumoRepository = repoInsumos,
                nombreResponsable = "Asoc. Procesadora Huata",
            )

            val state = viewModel.uiState.first { it.producciones.isNotEmpty() }
            assertFalse(state.producciones.isEmpty())
            assertFalse(state.insumos.isEmpty())
            assertEquals("Asoc. Procesadora Huata", state.nombreResponsable)
        }
    }

    @Test
    fun produccionDerivado_y_InsumoLacteo_conservanCampos() {
        val p = ProduccionDerivado(
            id = "P1",
            tipoProducto = "Yogurt",
            codigoLote = "L1",
            cantidadUnidades = 100.0,
            fechaProduccion = fechaPrueba,
            responsableId = "R1",
        )
        val i = InsumoLacteo(
            id = "I1",
            nombreInsumo = "Cuajo",
            cantidad = 10.0,
            unidadMedida = "Litros",
            fechaIngreso = fechaPrueba,
        )

        assertEquals("Yogurt", p.tipoProducto)
        assertEquals(100.0, p.cantidadUnidades)
        assertEquals("Cuajo", i.nombreInsumo)
        assertEquals("Litros", i.unidadMedida)
    }
}
