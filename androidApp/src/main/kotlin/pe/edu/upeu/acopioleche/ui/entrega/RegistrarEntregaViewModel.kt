package pe.edu.upeu.acopioleche.ui.entrega

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion
import pe.edu.upeu.acopioleche.ui.data.DatosDemo
import pe.edu.upeu.acopioleche.ui.data.RepositorioEntregasEnMemoria

class RegistrarEntregaViewModel : ViewModel() {

    private val estadoFormulario = MutableStateFlow(RegistrarEntregaUiState())

    val uiState: StateFlow<RegistrarEntregaUiState> = combine(
        estadoFormulario,
        RepositorioEntregasEnMemoria.entregas,
    ) { formulario, entregas ->
        formulario.copy(entregasRecientes = entregas)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RegistrarEntregaUiState(),
    )

    val proveedoresFiltrados: List<Proveedor>
        get() {
            val texto = estadoFormulario.value.textoBusqueda.lowercase()
            return DatosDemo.proveedores.filter {
                it.nombre.lowercase().contains(texto) || it.sector.lowercase().contains(texto)
            }
        }

    fun seleccionarTurno(turno: String) = estadoFormulario.update { it.copy(turno = turno) }

    fun cambiarBusqueda(texto: String) = estadoFormulario.update {
        it.copy(textoBusqueda = texto, mostrarBusqueda = true)
    }

    fun mostrarBusqueda(mostrar: Boolean) = estadoFormulario.update { it.copy(mostrarBusqueda = mostrar) }

    fun seleccionarProveedor(proveedor: Proveedor) = estadoFormulario.update {
        it.copy(proveedorSeleccionado = proveedor, textoBusqueda = "", mostrarBusqueda = false)
    }

    fun quitarProveedor() = estadoFormulario.update {
        it.copy(proveedorSeleccionado = null, textoBusqueda = "", mostrarBusqueda = true)
    }

    fun cambiarLitros(valor: String) = estadoFormulario.update { it.copy(litros = valor) }

    fun actualizarHora(hora: String) = estadoFormulario.update { it.copy(horaActual = hora) }

    fun guardar() {
        val estado = estadoFormulario.value
        val proveedor = estado.proveedorSeleccionado ?: return
        val litros = estado.litros.toDoubleOrNull() ?: return
        if (litros <= 0.0) return

        val entrega = Entrega(
            id = "ent-${System.currentTimeMillis()}",
            proveedorId = proveedor.id,
            acopiadorId = DatosDemo.acopiadorActual.id,
            centroAcopioId = DatosDemo.centroAcopioActual.id,
            fecha = "31 Ago 2026",
            turno = estado.turno,
            volumenLitros = litros,
            estado = EstadoEntrega.Pendiente,
        )
        RepositorioEntregasEnMemoria.registrar(
            EntregaUi(
                entrega = entrega,
                proveedorNombre = proveedor.nombre,
                estadoSincronizacion = EstadoSincronizacion.PENDIENTE,
            ),
        )
        estadoFormulario.update { it.copy(guardado = true) }
    }

    fun registrarOtra() = estadoFormulario.update {
        it.copy(proveedorSeleccionado = null, litros = "", guardado = false, textoBusqueda = "")
    }
}
