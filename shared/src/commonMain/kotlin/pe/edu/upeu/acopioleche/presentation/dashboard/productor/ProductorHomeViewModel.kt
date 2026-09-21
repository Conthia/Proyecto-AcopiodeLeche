package pe.edu.upeu.acopioleche.presentation.dashboard.productor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.service.CicloSemanal
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.aResumen

class ProductorHomeViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    private val proveedorRepository: ProveedorRepository,
    private val analisisCalidadRepository: AnalisisCalidadRepository,
    private val liquidacionRepository: LiquidacionRepository,
    val proveedorId: String,
    nombreProductor: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(ProductorHomeUiState(proveedorId = proveedorId, nombreProductor = nombreProductor))
    val uiState: StateFlow<ProductorHomeUiState> = _uiState.asStateFlow()

    init {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val inicioSemana = CicloSemanal.inicioDeSemana(hoy)

        scope.launch {
            combine(
                entregaRepository.observarEntregasDe(proveedorId = proveedorId),
                analisisCalidadRepository.observarAnalisisRecientes(),
                liquidacionRepository.observarLiquidacionesDe(proveedorId = proveedorId),
                proveedorRepository.observarProveedores(),
            ) { entregasDelProductor, todosAnalisis, misLiquidaciones, proveedores ->
                val misEntregasValidas = entregasDelProductor.filter { it.estado !is EstadoEntrega.Cancelada && it.estado !is EstadoEntrega.NoRecogida }
                val misEntregasIds = entregasDelProductor.map { it.id }.toSet()

                val litrosHoy = misEntregasValidas.filter { it.fecha == hoy }.sumOf { it.volumenLitros }
                val acumuladoSemana = misEntregasValidas.filter { it.fecha >= inicioSemana }.sumOf { it.volumenLitros }

                val misAnalisis = todosAnalisis.filter { misEntregasIds.contains(it.entregaId) }

                ProductorHomeUiState(
                    proveedorId = proveedorId,
                    nombreProductor = nombreProductor,
                    litrosHoy = litrosHoy,
                    litrosAcumuladoSemana = acumuladoSemana,
                    misEntregas = entregasDelProductor.map { it.aResumen(proveedores, sincronizada = true) },
                    misAnalisis = misAnalisis,
                    misLiquidaciones = misLiquidaciones,
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }
}
