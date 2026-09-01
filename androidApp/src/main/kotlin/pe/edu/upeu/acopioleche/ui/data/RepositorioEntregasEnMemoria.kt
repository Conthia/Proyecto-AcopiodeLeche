package pe.edu.upeu.acopioleche.ui.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upeu.acopioleche.domain.model.Entrega
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.ui.common.EstadoSincronizacion
import pe.edu.upeu.acopioleche.ui.entrega.EntregaUi

/**
 * Fuente de verdad en memoria para las entregas del turno, compartida entre el Dashboard de
 * Acopiador y la pantalla de Registrar Entrega. Sustituye a un repositorio real (persistencia
 * local + sincronizacion remota), que todavia no existe en esta sesion.
 */
object RepositorioEntregasEnMemoria {

    private val _entregas = MutableStateFlow(
        listOf(
            EntregaUi(
                entrega = Entrega(
                    id = "ent-1",
                    proveedorId = "p1",
                    acopiadorId = DatosDemo.acopiadorActual.id,
                    centroAcopioId = DatosDemo.centroAcopioActual.id,
                    fecha = "31 Ago 2026",
                    turno = "Mañana",
                    volumenLitros = 28.5,
                    estado = EstadoEntrega.Pendiente,
                ),
                proveedorNombre = "Juan Quispe Mamani",
                estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
            ),
            EntregaUi(
                entrega = Entrega(
                    id = "ent-2",
                    proveedorId = "p2",
                    acopiadorId = DatosDemo.acopiadorActual.id,
                    centroAcopioId = DatosDemo.centroAcopioActual.id,
                    fecha = "31 Ago 2026",
                    turno = "Mañana",
                    volumenLitros = 15.0,
                    estado = EstadoEntrega.Pendiente,
                ),
                proveedorNombre = "Rosa Flores Ccapa",
                estadoSincronizacion = EstadoSincronizacion.PENDIENTE,
            ),
        ),
    )
    val entregas: StateFlow<List<EntregaUi>> = _entregas.asStateFlow()

    fun registrar(entregaUi: EntregaUi) {
        _entregas.update { listOf(entregaUi) + it }
    }
}
