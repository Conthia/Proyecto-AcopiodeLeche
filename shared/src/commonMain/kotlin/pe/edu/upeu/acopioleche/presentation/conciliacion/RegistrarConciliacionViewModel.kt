package pe.edu.upeu.acopioleche.presentation.conciliacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

/**
 * RF-21: registra el volumen medido al descargar en planta y calcula la diferencia contra el
 * volumen de campo (`Entrega.diferenciaLitros`, en el dominio). No hay una regla de negocio que
 * "corrija" la entrega por esto — el interesado solo pidió registrar ambos volúmenes y mostrar
 * la diferencia, no ajustar el pago (eso no está pedido para esta fase).
 */
class RegistrarConciliacionViewModel(
    scope: CoroutineScope,
    private val entregaRepository: EntregaRepository,
    proveedorRepository: ProveedorRepository,
    private val entregaId: String,
) : AppViewModel(scope = scope) {

    private val _uiState = MutableStateFlow(RegistrarConciliacionUiState(entregaId = entregaId))
    val uiState: StateFlow<RegistrarConciliacionUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            val entrega = entregaRepository.observarEntregasDeHoy().first().find { it.id == entregaId }
            val proveedor = entrega?.let { e ->
                proveedorRepository.observarProveedores().first().find { it.id == e.proveedorId }
            }
            _uiState.value = _uiState.value.copy(
                nombreProveedor = proveedor?.nombre ?: entrega?.proveedorId.orEmpty(),
                volumenCampoLitros = entrega?.volumenLitros ?: 0.0,
            )
        }
    }

    fun onVolumenPlantaChange(texto: String) {
        val filtrado = texto.filterIndexed { index, c -> c.isDigit() || (c == '.' && !texto.take(index).contains('.')) }
        _uiState.value = _uiState.value.copy(volumenPlantaTexto = filtrado, mensajeError = null)
    }

    fun onGuardarClick() {
        val estado = _uiState.value
        val volumenPlanta = estado.volumenPlanta
        if (volumenPlanta == null) {
            _uiState.value = estado.copy(mensajeError = "Ingresa el volumen medido en planta")
            return
        }
        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            val entrega = entregaRepository.observarEntregasDeHoy().first().find { it.id == entregaId }
            if (entrega != null) {
                val actualizada = entrega.copy(volumenPlantaLitros = volumenPlanta)
                entregaRepository.registrar(actualizada)
                _uiState.value = _uiState.value.copy(guardando = false, diferenciaLitros = actualizada.diferenciaLitros)
            } else {
                _uiState.value = _uiState.value.copy(guardando = false, mensajeError = "No se encontró la entrega")
            }
        }
    }
}
