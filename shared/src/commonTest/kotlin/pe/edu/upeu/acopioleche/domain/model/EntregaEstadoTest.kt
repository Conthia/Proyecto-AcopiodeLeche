package pe.edu.upeu.acopioleche.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.fake.FakeAnalisisCalidadRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.presentation.entrega.EntregasDelDiaViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EntregaEstadoTest {

    @Test
    fun seDistingueEntregasRechazadaDeNoRecogida() {
        val rechazada = EstadoEntrega.Rechazada(motivo = MotivoRechazo.ACIDEZ_FUERA_DE_RANGO)
        val noRecogida = EstadoEntrega.NoRecogida(motivo = "No había leche")

        assertIs<EstadoEntrega.Rechazada>(rechazada)
        assertIs<EstadoEntrega.NoRecogida>(noRecogida)
        assertEquals("No había leche", noRecogida.motivo)
    }

    @Test
    fun cancelacionDeEntregaAlmacenaUsuarioIdAutenticado() {
        val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val cancelada = EstadoEntrega.Cancelada(
            motivo = "Registro duplicado",
            canceladaPor = "ADM-01",
            fechaHora = ahora,
        )

        assertIs<EstadoEntrega.Cancelada>(cancelada)
        assertEquals("ADM-01", cancelada.canceladaPor)
        assertEquals("Registro duplicado", cancelada.motivo)
    }

    @Test
    fun cancelacionExitosaDeEntregaPendienteSinAnalisis() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            val provRepo = FakeProveedorRepository()
            val analisisRepo = FakeAnalisisCalidadRepository()
            val viewModel = EntregasDelDiaViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                entregaRepository = entregaRepo,
                proveedorRepository = provRepo,
                analisisCalidadRepository = analisisRepo,
            )

            var exito = false
            viewModel.cancelarEntrega(
                id = "E-1042",
                motivo = "Error al seleccionar proveedor",
                usuarioId = "A-01",
                alBloqueadoPorAnalisis = {},
                alExito = { exito = true },
            )

            assertTrue(exito)
            val entregas = entregaRepo.observarEntregasDeHoy().first()
            val cancelada = entregas.find { it.id == "E-1042" }
            assertNotNull(cancelada)
            assertIs<EstadoEntrega.Cancelada>(cancelada.estado)
        }
    }

    @Test
    fun reglaAntifraudeBloqueaCancelacionSiExisteAnalisisDeCalidad() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            val provRepo = FakeProveedorRepository()
            val analisisRepo = FakeAnalisisCalidadRepository()

            val ahora = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val entregaPendiente = Entrega(
                id = "E-TEST-PENDIENTE",
                proveedorId = "P-014",
                acopiadorId = "A-01",
                centroAcopioId = "CA-001",
                fecha = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                turno = Turno.MANANA,
                volumenLitros = 20.0,
                estado = EstadoEntrega.Pendiente,
                cantidadPorongos = 1,
            )
            entregaRepo.registrar(entregaPendiente)

            val analisis = AnalisisCalidad(
                id = "AC-TEST-01",
                entregaId = "E-TEST-PENDIENTE",
                tecnicoId = "A-01",
                fecha = ahora,
                resultado = ResultadoAnalisis.Adulterada(indicio = "Agua añadida", porcentajeAgua = 15.0),
            )
            analisisRepo.registrar(analisis)

            val viewModel = EntregasDelDiaViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                entregaRepository = entregaRepo,
                proveedorRepository = provRepo,
                analisisCalidadRepository = analisisRepo,
            )

            var bloqueado = false
            var exito = false
            viewModel.cancelarEntrega(
                id = "E-TEST-PENDIENTE",
                motivo = "Intento de borrar evidencia de adulteracion",
                usuarioId = "A-01",
                alBloqueadoPorAnalisis = { bloqueado = true },
                alExito = { exito = true },
            )

            assertTrue(bloqueado)
            assertTrue(!exito)
        }
    }
}
