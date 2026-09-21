package pe.edu.upeu.acopioleche.presentation.ruta

import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.domain.model.Usuario

data class AsignarRutaUiState(
    val acopiadores: List<Usuario> = emptyList(),
    val centrosSector: List<CentroAcopio> = emptyList(),
    val proveedoresDisponibles: List<Proveedor> = emptyList(),
    val rutasExistentes: List<RutaAcopio> = emptyList(),
    val acopiadorSeleccionadoId: String? = null,
    val centroSectorSeleccionadoId: String? = null,
    val fecha: LocalDate? = null,
    val nombreRuta: String = "",
    val paradasOrdenadasProveedorIds: List<String> = emptyList(),
    /** Id de la ruta que se está editando, o null si "Guardar y Asignar Ruta" va a crear una nueva. */
    val rutaEnEdicionId: String? = null,
    val guardando: Boolean = false,
    val mensajeNotificacion: String? = null,
    val mensajeError: String? = null,
) {
    val enModoEdicion: Boolean get() = rutaEnEdicionId != null
}
