package pe.edu.upeu.acopioleche.presentation.analisis

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.data.fake.FakeAnalisisCalidadRepository
import pe.edu.upeu.acopioleche.data.fake.FakeCapacitacionCorrectivaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeEntregaRepository
import pe.edu.upeu.acopioleche.data.fake.FakeNotificacionRepository
import pe.edu.upeu.acopioleche.data.fake.FakeProveedorRepository
import pe.edu.upeu.acopioleche.data.fake.FakeSancionRepository
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.MotivoRechazo
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.model.Turno
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * RF-05 / RN-02: registrar un AnalisisCalidad con densidad, grasa (u otro parámetro medido por
 * el lactoescan) fuera de rango debe rechazar la entrega automáticamente, sin acción separada
 * del técnico. `acidez` no se prueba aquí porque el lactoescan real no la mide y
 * [pe.edu.upeu.acopioleche.domain.service.EvaluadorCalidad] nunca la evalúa (ver
 * ResultadoAnalisis.kt y MotivoRechazo.kt) — MotivoRechazo.ACIDEZ_FUERA_DE_RANGO solo se ejercita
 * indirectamente vía el resultado de FueraDeRango que sí llega desde el evaluador.
 */
class RegistrarAnalisisViewModelTest {

    private fun crearEntrega(id: String, estado: EstadoEntrega = EstadoEntrega.Pendiente): Entrega =
        Entrega(
            id = id,
            proveedorId = "P-014",
            acopiadorId = "A-01",
            centroAcopioId = "CA-001",
            fecha = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            turno = Turno.MANANA,
            volumenLitros = 20.0,
            estado = estado,
            cantidadPorongos = 1,
        )

    private fun crearViewModel(
        entregaRepo: FakeEntregaRepository,
        entregaId: String,
    ): RegistrarAnalisisViewModel =
        RegistrarAnalisisViewModel(
            scope = CoroutineScope(Dispatchers.Unconfined),
            entregaRepository = entregaRepo,
            proveedorRepository = FakeProveedorRepository(),
            analisisCalidadRepository = FakeAnalisisCalidadRepository(),
            sancionRepository = FakeSancionRepository(),
            capacitacionCorrectivaRepository = FakeCapacitacionCorrectivaRepository(),
            notificacionRepository = FakeNotificacionRepository(),
            entregaId = entregaId,
            tecnicoId = "T-01",
        )

    private fun RegistrarAnalisisViewModel.capturarLecturaValida() {
        onDensidadChange("1.031")
        onGrasaChange("3.5")
        onProteinaChange("3.2")
        onLactosaChange("4.6")
        onTemperaturaChange("4.0")
        onPhChange("6.7")
        onPorcentajeAguaChange("0.0")
    }

    @Test
    fun densidadFueraDeRangoRechazaLaEntregaAutomaticamente() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            entregaRepo.registrar(crearEntrega(id = "E-TEST-DENSIDAD"))
            val viewModel = crearViewModel(entregaRepo, "E-TEST-DENSIDAD")

            viewModel.capturarLecturaValida()
            viewModel.onDensidadChange("1.020")
            viewModel.onGuardarClick()

            val entrega = entregaRepo.buscarPorId("E-TEST-DENSIDAD")
            val rechazada = assertIs<EstadoEntrega.Rechazada>(entrega?.estado)
            assertEquals(MotivoRechazo.DENSIDAD_FUERA_DE_RANGO, rechazada.motivo)
            assertNull(viewModel.uiState.value.advertenciaEstado)
        }
    }

    @Test
    fun grasaFueraDeRangoRechazaLaEntregaAutomaticamente() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            entregaRepo.registrar(crearEntrega(id = "E-TEST-GRASA"))
            val viewModel = crearViewModel(entregaRepo, "E-TEST-GRASA")

            viewModel.capturarLecturaValida()
            viewModel.onGrasaChange("0.5")
            viewModel.onGuardarClick()

            val entrega = entregaRepo.buscarPorId("E-TEST-GRASA")
            val rechazada = assertIs<EstadoEntrega.Rechazada>(entrega?.estado)
            assertEquals(MotivoRechazo.GRASA_FUERA_DE_RANGO, rechazada.motivo)
        }
    }

    @Test
    fun losTresParametrosDentroDeRangoNoRechazaLaEntrega() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            entregaRepo.registrar(crearEntrega(id = "E-TEST-NORMAL"))
            val viewModel = crearViewModel(entregaRepo, "E-TEST-NORMAL")

            viewModel.capturarLecturaValida()
            viewModel.onGuardarClick()

            val resultado = viewModel.uiState.value.resultadoGuardado
            assertIs<ResultadoAnalisis.Normal>(resultado)

            val entrega = entregaRepo.buscarPorId("E-TEST-NORMAL")
            assertIs<EstadoEntrega.Pendiente>(entrega?.estado)
        }
    }

    @Test
    fun rn20BloqueaElRechazoAutomaticoSiLaEntregaYaEstaEnTransitoAPlanta() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            val estadoOriginal = EstadoEntrega.EnTransitoAPlanta(
                transportistaId = "T-01",
                horaSalida = LocalDateTime(2026, 9, 14, 6, 0),
            )
            entregaRepo.registrar(crearEntrega(id = "E-TEST-TRANSITO", estado = estadoOriginal))
            val viewModel = crearViewModel(entregaRepo, "E-TEST-TRANSITO")

            viewModel.capturarLecturaValida()
            viewModel.onDensidadChange("1.020")
            viewModel.onGuardarClick()

            val entrega = entregaRepo.buscarPorId("E-TEST-TRANSITO")
            assertEquals(estadoOriginal, entrega?.estado)
            assertNotNull(viewModel.uiState.value.advertenciaEstado)
        }
    }

    @Test
    fun rn20BloqueaElRechazoAutomaticoSiLaEntregaYaEstaLiquidada() {
        runBlocking {
            val entregaRepo = FakeEntregaRepository()
            val estadoOriginal = EstadoEntrega.Liquidada(liquidacionId = "LIQ-2026-09")
            entregaRepo.registrar(crearEntrega(id = "E-TEST-LIQUIDADA", estado = estadoOriginal))
            val viewModel = crearViewModel(entregaRepo, "E-TEST-LIQUIDADA")

            viewModel.capturarLecturaValida()
            viewModel.onGrasaChange("0.5")
            viewModel.onGuardarClick()

            val entrega = entregaRepo.buscarPorId("E-TEST-LIQUIDADA")
            assertEquals(estadoOriginal, entrega?.estado)
            assertNotNull(viewModel.uiState.value.advertenciaEstado)
        }
    }
}
