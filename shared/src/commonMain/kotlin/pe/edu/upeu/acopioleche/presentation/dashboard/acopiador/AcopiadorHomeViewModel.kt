package pe.edu.upeu.acopioleche.presentation.dashboard.acopiador

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.aResumen
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas

class AcopiadorHomeViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    proveedorRepository: ProveedorRepository,
    private val rutaRepository: RutaRepository,
    acopiadorId: String = "A-01",
    nombreAcopiador: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(AcopiadorHomeUiState(nombreAcopiador = nombreAcopiador))
    val uiState: StateFlow<AcopiadorHomeUiState> = _uiState.asStateFlow()

    init {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        scope.launch {
            combine(
                entregaRepository.observarEntregasDeHoy(),
                entregaRepository.observarPendientesDeSincronizar(),
                proveedorRepository.observarProveedores(),
                rutaRepository.observarRutaDelDia(acopiadorId = acopiadorId, fecha = hoy),
            ) { entregas, pendientes, proveedores, rutaActual ->
                val pendientesIds = pendientes.map { it.id }.toSet()
                val entregaronHoyIds = entregas
                    .filter { it.estado !is EstadoEntrega.Cancelada }
                    .map { it.proveedorId }.toSet()

                // Los proveedores pendientes son los de la ruta del día realmente asignada por
                // el admin (RF-15), no un padrón fijo aparte: si no coincidieran, el acopiador
                // vería una lista de "pendientes hoy" que no tiene nada que ver con la ruta que
                // le acaban de asignar.
                val proveedoresRuta = rutaActual?.paradas.orEmpty().mapNotNull { parada ->
                    proveedores.find { it.id == parada.proveedorId }
                }

                val pendientesEntrega = proveedoresRuta
                    .filter { !entregaronHoyIds.contains(it.id) }
                    .map { p ->
                        ProveedorConEntregas(
                            id = p.id,
                            nombre = p.nombre,
                            sector = p.sector,
                            numeroVacas = p.numeroVacas,
                            litrosHoy = 0.0,
                            calificacion = p.calificacion,
                            activo = true,
                        )
                    }

                AcopiadorHomeUiState(
                    nombreAcopiador = nombreAcopiador,
                    litrosHoy = entregas.filter { it.estado !is EstadoEntrega.Cancelada && it.estado !is EstadoEntrega.NoRecogida }.sumOf { it.volumenLitros },
                    numeroEntregas = entregas.filter { it.estado !is EstadoEntrega.Cancelada }.size,
                    numeroPendientes = pendientes.size,
                    rutaActual = rutaActual,
                    ultimasEntregas = entregas.take(3).map { entrega ->
                        entrega.aResumen(
                            proveedores = proveedores,
                            sincronizada = !pendientesIds.contains(entrega.id),
                        )
                    },
                    proveedoresPendientesHoy = pendientesEntrega,
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun realizarCierreDeRuta(alExito: (volumenTotal: Double) -> Unit) {
        scope.launch {
            val ruta = _uiState.value.rutaActual ?: return@launch
            val entregas = entregaRepository.observarEntregasDeHoy().first()
            val totalLitros = entregas.filter { it.estado !is EstadoEntrega.Cancelada && it.estado !is EstadoEntrega.NoRecogida }.sumOf { it.volumenLitros }
            rutaRepository.cerrarRuta(rutaId = ruta.id, volumenTotalDescargado = totalLitros)
            alExito(totalLitros)
        }
    }
}
