package pe.edu.upeu.acopioleche.presentation.proveedor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppLogger
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState

class ProveedoresViewModel(
    scope: CoroutineScope,
    private val proveedorRepository: ProveedorRepository,
    private val entregaRepository: EntregaRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow<UiState<ProveedoresUiState>>(UiState.Cargando)
    val uiState: StateFlow<UiState<ProveedoresUiState>> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                proveedorRepository.observarProveedores(),
                entregaRepository.observarEntregasDeHoy(),
                proveedorRepository.observarPendientesDeSincronizar(),
            ) { proveedores, entregas, pendientes ->
                val idsPendientes = pendientes.map { it.entidad.id }.toSet()
                ProveedoresUiState(
                    proveedores = proveedores.map { proveedor ->
                        ProveedorConEntregas(
                            id = proveedor.id,
                            nombre = proveedor.nombre,
                            sector = proveedor.sector,
                            numeroVacas = proveedor.numeroVacas,
                            litrosHoy = entregas.filter { it.proveedorId == proveedor.id }.sumOf { it.volumenLitros },
                            calificacion = proveedor.calificacion,
                            activo = proveedor.activo,
                            documento = proveedor.documento,
                            telefono = proveedor.telefono,
                            entregaDirectaEnPlanta = proveedor.entregaDirectaEnPlanta,
                            pendienteSync = idsPendientes.contains(proveedor.id),
                        )
                    },
                )
            }
                .map<ProveedoresUiState, UiState<ProveedoresUiState>> { estado ->
                    if (estado.proveedores.isEmpty()) UiState.Vacio else UiState.Exito(estado)
                }
                .catch { error ->
                    AppLogger.error(TAG, "Error al observar proveedores", error)
                    emit(UiState.Error("No se pudo cargar la información"))
                }
                .collect { estado -> _uiState.value = estado }
        }
    }

    fun guardarProveedor(proveedor: Proveedor) {
        scope.launch {
            proveedorRepository.guardar(proveedor)
        }
    }

    fun eliminarProveedor(id: String, alDesactivarPorDependencias: () -> Unit, alEliminarDefinitivo: () -> Unit) {
        scope.launch {
            val entregas = entregaRepository.observarEntregasDeHoy().first()
            val tieneEntregas = entregas.any { it.proveedorId == id }
            if (tieneEntregas) {
                val actual = proveedorRepository.observarProveedores().first().find { it.id == id }
                if (actual != null) {
                    proveedorRepository.actualizar(actual.copy(activo = false))
                }
                alDesactivarPorDependencias()
            } else {
                proveedorRepository.eliminar(id)
                alEliminarDefinitivo()
            }
        }
    }

    private companion object {
        const val TAG = "ProveedoresViewModel"
    }
}
