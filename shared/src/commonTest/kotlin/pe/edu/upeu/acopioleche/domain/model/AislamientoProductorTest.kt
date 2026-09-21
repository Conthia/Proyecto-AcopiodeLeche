package pe.edu.upeu.acopioleche.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeAnalisisCalidadRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeLiquidacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.presentation.dashboard.productor.ProductorHomeViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AislamientoProductorTest {

    @Test
    fun entregaRepository_observarEntregasDe_retornaUnicamenteLasEntregasDelProductorSolicitado() {
        runBlocking {
            val repository = FakeEntregaRepository()
            val proveedorA = "P-014"
            val proveedorB = "P-027"

            val entregasA = repository.observarEntregasDe(proveedorId = proveedorA).first()
            val entregasB = repository.observarEntregasDe(proveedorId = proveedorB).first()

            assertTrue(entregasA.isNotEmpty())
            assertTrue(entregasB.isNotEmpty())

            // RN-29: Aislamiento estricto en capa de datos — ningun registro de B debe aparecer en las entregas de A
            assertTrue(entregasA.all { it.proveedorId == proveedorA })
            assertTrue(entregasA.none { it.proveedorId == proveedorB })

            // Ningun registro de A debe aparecer en las entregas de B
            assertTrue(entregasB.all { it.proveedorId == proveedorB })
            assertTrue(entregasB.none { it.proveedorId == proveedorA })
        }
    }

    @Test
    fun liquidacionRepository_observarLiquidacionesDe_retornaUnicamenteLasLiquidacionesDelProductorSolicitado() {
        runBlocking {
            val repository = FakeLiquidacionRepository()
            val proveedorA = "P-014"
            val proveedorB = "P-027"

            val liquidacionesA = repository.observarLiquidacionesDe(proveedorId = proveedorA).first()
            val liquidacionesB = repository.observarLiquidacionesDe(proveedorId = proveedorB).first()

            assertTrue(liquidacionesA.all { it.proveedorId == proveedorA })
            assertTrue(liquidacionesA.none { it.proveedorId == proveedorB })

            assertTrue(liquidacionesB.all { it.proveedorId == proveedorB })
            assertTrue(liquidacionesB.none { it.proveedorId == proveedorA })
        }
    }

    @Test
    fun productorHomeViewModel_calculaAcumuladosAisladosExclusivamenteDeSuProveedorId() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            val provRepo = FakeProveedorRepository()
            val analisisRepo = FakeAnalisisCalidadRepository()
            val liqRepo = FakeLiquidacionRepository()

            val proveedorA = "P-014"
            val viewModelA = ProductorHomeViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                entregaRepository = entregaRepo,
                proveedorRepository = provRepo,
                analisisCalidadRepository = analisisRepo,
                liquidacionRepository = liqRepo,
                proveedorId = proveedorA,
                nombreProductor = "Rosa Quispe",
            )

            val stateA = viewModelA.uiState.first { it.misEntregas.isNotEmpty() }
            assertEquals(proveedorA, stateA.proveedorId)
            assertTrue(stateA.misEntregas.isNotEmpty(), "La lista de entregas no debe estar vacia para probar aislamiento de datos")
            assertTrue(stateA.misEntregas.all { it.codigoProveedor == proveedorA })
            assertTrue(stateA.misLiquidaciones.all { it.proveedorId == proveedorA })
        }
    }

    @Test
    fun unProductorSinEntregasOInexistenteRecibeListasVacias() {
        runBlocking {
            val repository = FakeEntregaRepository()
            val proveedorDesconocido = "P-INEXISTENTE-999"

            val entregas = repository.observarEntregasDe(proveedorId = proveedorDesconocido).first()
            assertTrue(entregas.isEmpty())
        }
    }
}
