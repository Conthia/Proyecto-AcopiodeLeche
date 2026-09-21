package pe.edu.upeu.acopioleche.presentation.conciliacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

/** Lista, para el Administrador, las entregas de hoy que aún no tienen volumen de planta (RF-21). */
class ConciliacionListaViewModel(
    scope: CoroutineScope,
    entregaRepository: EntregaRepository,
    proveedorRepository: ProveedorRepository,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(ConciliacionListaUiState())
    val uiState: StateFlow<ConciliacionListaUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                entregaRepository.observarEntregasDeHoy(),
                proveedorRepository.observarProveedores(),
            ) { entregas, proveedores ->
                ConciliacionListaUiState(
                    pendientes = entregas.filter { entrega -> entrega.volumenPlantaLitros == null }.map { entrega ->
                        val proveedor = proveedores.find { it.id == entrega.proveedorId }
                        EntregaPendienteConciliacion(
                            entregaId = entrega.id,
                            nombreProveedor = proveedor?.nombre ?: entrega.proveedorId,
                            volumenCampoLitros = entrega.volumenLitros,
                        )
                    },
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }
}
