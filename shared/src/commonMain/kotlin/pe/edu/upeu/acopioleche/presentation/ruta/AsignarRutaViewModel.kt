package pe.edu.upeu.acopioleche.presentation.ruta

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.domain.model.EstadoParada
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.ParadaRuta
import pe.edu.upeu.acopioleche.domain.model.ResultadoEliminacionRuta
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.domain.model.RutaAcopio
import pe.edu.upeu.acopioleche.domain.model.TipoCentroAcopio
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository
import pe.edu.upeu.acopioleche.domain.repository.UsuarioRepository
import pe.edu.upeu.acopioleche.presentation.core.AppViewModel

class AsignarRutaViewModel(
    scope: CoroutineScope,
    private val rutaRepository: RutaRepository,
    usuarioRepository: UsuarioRepository,
    centroAcopioRepository: CentroAcopioRepository,
    proveedorRepository: ProveedorRepository,
) : AppViewModel(scope = scope) {

    private val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _uiState = MutableStateFlow(AsignarRutaUiState(fecha = hoy))
    val uiState: StateFlow<AsignarRutaUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                rutaRepository.observarTodasRutas(),
                usuarioRepository.observarUsuarios(),
                centroAcopioRepository.observarCentros(),
                proveedorRepository.observarProveedores(),
            ) { rutas, usuarios, centros, proveedores ->
                val acopiadores = usuarios.filter { it.rol == RolUsuario.ACOPIADOR }
                val centrosSector = centros.filter { it.activo && it.tipoCentro == TipoCentroAcopio.CENTRO_SECTOR }
                val provsActivos = proveedores.filter { it.activo }

                val estadoPrevio = _uiState.value
                estadoPrevio.copy(
                    acopiadores = acopiadores,
                    centrosSector = centrosSector,
                    proveedoresDisponibles = provsActivos,
                    rutasExistentes = rutas,
                    acopiadorSeleccionadoId = estadoPrevio.acopiadorSeleccionadoId ?: acopiadores.firstOrNull()?.id,
                    centroSectorSeleccionadoId = estadoPrevio.centroSectorSeleccionadoId ?: centrosSector.firstOrNull()?.id,
                )
            }.collect { estado -> _uiState.value = estado }
        }
    }

    fun onAcopiadorChange(id: String) {
        _uiState.value = _uiState.value.copy(acopiadorSeleccionadoId = id)
    }

    fun onCentroSectorChange(id: String) {
        _uiState.value = _uiState.value.copy(centroSectorSeleccionadoId = id)
    }

    fun onNombreRutaChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombreRuta = nombre)
    }

    fun onAgregarParada(proveedorId: String) {
        val actuales = _uiState.value.paradasOrdenadasProveedorIds
        if (!actuales.contains(proveedorId)) {
            _uiState.value = _uiState.value.copy(paradasOrdenadasProveedorIds = actuales + proveedorId)
        }
    }

    fun onQuitarParada(proveedorId: String) {
        _uiState.value = _uiState.value.copy(
            paradasOrdenadasProveedorIds = _uiState.value.paradasOrdenadasProveedorIds.filter { it != proveedorId },
        )
    }

    fun onMoverParada(desdeIndice: Int, haciaIndice: Int) {
        val lista = _uiState.value.paradasOrdenadasProveedorIds.toMutableList()
        if (desdeIndice in lista.indices && haciaIndice in lista.indices) {
            val elemento = lista.removeAt(desdeIndice)
            lista.add(haciaIndice, elemento)
            _uiState.value = _uiState.value.copy(paradasOrdenadasProveedorIds = lista)
        }
    }

    fun onAsignarRutaClick() {
        val estado = _uiState.value
        val acopiadorId = estado.acopiadorSeleccionadoId
        val centroSectorId = estado.centroSectorSeleccionadoId
        val fecha = estado.fecha ?: hoy

        if (acopiadorId == null || centroSectorId == null) {
            _uiState.value = estado.copy(mensajeError = "Selecciona un acopiador y un centro de sector")
            return
        }
        if (estado.paradasOrdenadasProveedorIds.isEmpty()) {
            _uiState.value = estado.copy(mensajeError = "Agrega al menos un proveedor a la ruta")
            return
        }

        val editando = estado.rutaEnEdicionId
        val idRuta = editando ?: "R-${Clock.System.now().toEpochMilliseconds()}"
        val nombre = estado.nombreRuta.ifBlank { "Ruta ${estado.centrosSector.find { it.id == centroSectorId }?.nombre ?: "Sector"}" }
        val rutaOriginal = if (editando != null) estado.rutasExistentes.find { it.id == editando } else null

        // Al editar, una parada que ya existía conserva su estadoParada (visitado/omitido): el
        // formulario solo maneja la lista de proveedorIds, así que si aquí siempre se creara con
        // PENDIENTE se borraría el avance real del acopiador con solo tocar "Editar" + "Guardar",
        // dejando la ruta eliminable cuando en realidad ya tenía entregas registradas.
        val estadoParadaOriginalPorProveedor = rutaOriginal?.paradas?.associateBy { it.proveedorId }.orEmpty()
        val paradas = estado.paradasOrdenadasProveedorIds.mapIndexed { index, provId ->
            ParadaRuta(
                orden = index + 1,
                proveedorId = provId,
                estadoParada = estadoParadaOriginalPorProveedor[provId]?.estadoParada ?: EstadoParada.PENDIENTE,
            )
        }

        val ruta = RutaAcopio(
            id = idRuta,
            nombre = nombre,
            acopiadorId = acopiadorId,
            centroSectorId = centroSectorId,
            fecha = fecha,
            paradas = paradas,
            estado = rutaOriginal?.estado ?: EstadoRuta.EN_CURSO,
            volumenDescargadoLitros = rutaOriginal?.volumenDescargadoLitros,
            fechaHoraCierre = rutaOriginal?.fechaHoraCierre,
        )

        scope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, mensajeError = null)
            if (editando != null) {
                rutaRepository.actualizarRuta(ruta)
            } else {
                rutaRepository.asignarRuta(ruta)
            }
            val mensaje = if (editando != null) {
                "Ruta '$nombre' actualizada correctamente para el $fecha"
            } else {
                "Ruta '$nombre' asignada correctamente al acopiador para el $fecha"
            }
            _uiState.value = _uiState.value.copy(
                guardando = false,
                paradasOrdenadasProveedorIds = emptyList(),
                nombreRuta = "",
                rutaEnEdicionId = null,
                mensajeNotificacion = mensaje,
            )
        }
    }

    /** Carga [ruta] en el formulario para editarla: "Guardar y Asignar Ruta" pasa a actualizarla
     * en vez de crear una nueva. */
    fun onEditarRuta(ruta: RutaAcopio) {
        _uiState.value = _uiState.value.copy(
            rutaEnEdicionId = ruta.id,
            acopiadorSeleccionadoId = ruta.acopiadorId,
            centroSectorSeleccionadoId = ruta.centroSectorId,
            fecha = ruta.fecha,
            nombreRuta = ruta.nombre,
            paradasOrdenadasProveedorIds = ruta.paradas.sortedBy { it.orden }.map { it.proveedorId },
            mensajeError = null,
            mensajeNotificacion = null,
        )
    }

    fun onCancelarEdicion() {
        _uiState.value = _uiState.value.copy(
            rutaEnEdicionId = null,
            nombreRuta = "",
            paradasOrdenadasProveedorIds = emptyList(),
        )
    }

    fun onEliminarRuta(ruta: RutaAcopio) {
        scope.launch {
            when (val resultado = rutaRepository.eliminarRuta(ruta.id)) {
                is ResultadoEliminacionRuta.Eliminada -> {
                    val siSeEstabaEditando = _uiState.value.rutaEnEdicionId == ruta.id
                    _uiState.value = _uiState.value.copy(
                        mensajeNotificacion = "Ruta '${ruta.nombre}' eliminada",
                        mensajeError = null,
                        rutaEnEdicionId = if (siSeEstabaEditando) null else _uiState.value.rutaEnEdicionId,
                        paradasOrdenadasProveedorIds = if (siSeEstabaEditando) emptyList() else _uiState.value.paradasOrdenadasProveedorIds,
                        nombreRuta = if (siSeEstabaEditando) "" else _uiState.value.nombreRuta,
                    )
                }
                is ResultadoEliminacionRuta.TieneAvance -> {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "No se puede eliminar: tiene ${resultado.paradasConAvance} parada(s) con avance " +
                            "(visitada u omitida). Edítala para corregirla o ciérrala si el día ya terminó.",
                        mensajeNotificacion = null,
                    )
                }
                is ResultadoEliminacionRuta.NoEncontrada -> {
                    _uiState.value = _uiState.value.copy(mensajeError = "La ruta ya no existe", mensajeNotificacion = null)
                }
            }
        }
    }
}
