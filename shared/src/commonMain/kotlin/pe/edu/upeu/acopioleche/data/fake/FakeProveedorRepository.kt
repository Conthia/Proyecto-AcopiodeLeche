package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Sector
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.sync.CambioPendiente

class FakeProveedorRepository : ProveedorRepository {

    private val _proveedores: MutableStateFlow<List<Proveedor>> = MutableStateFlow(seed())

    private val proveedores: StateFlow<List<Proveedor>> = _proveedores.asStateFlow()

    override fun observarProveedores(): StateFlow<List<Proveedor>> = proveedores

    override fun observarProveedoresDeRuta(acopiadorId: String): Flow<List<Proveedor>> =
        proveedores.map { lista -> lista.filter { RUTAS[acopiadorId]?.contains(it.id) == true } }

    override suspend fun guardar(proveedor: Proveedor) {
        _proveedores.value = _proveedores.value + proveedor
    }

    override suspend fun actualizar(proveedor: Proveedor) {
        _proveedores.value = _proveedores.value.map { if (it.id == proveedor.id) proveedor else it }
    }

    override suspend fun eliminar(id: String) {
        _proveedores.value = _proveedores.value.filter { it.id != id }
    }

    // Implementación en memoria: no participa en sincronización, no hay nada pendiente.
    override fun observarPendientesDeSincronizar(): Flow<List<CambioPendiente<Proveedor>>> = emptyFlow()

    override suspend fun marcarSincronizado(id: String, actualizadoEn: LocalDateTime) = Unit

    override suspend fun confirmarEliminacionRemota(id: String) = Unit

    override suspend fun aplicarCambioRemoto(proveedor: Proveedor, actualizadoEn: LocalDateTime) {
        actualizar(proveedor)
    }

    override suspend fun aplicarEliminacionRemota(id: String, actualizadoEn: LocalDateTime) {
        eliminar(id)
    }

    private fun seed(): List<Proveedor> =
        listOf(
            Proveedor(
                id = "P-014",
                nombre = "Rosa Quispe Mamani",
                documento = "41028573",
                telefono = "951034221",
                sector = Sector.NORTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 9,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-027",
                nombre = "Elías Cutipa Apaza",
                documento = "42911087",
                telefono = "951034222",
                sector = Sector.ESTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 12,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-003",
                nombre = "Juana Choque Yupanqui",
                documento = "40119345",
                telefono = "951034223",
                sector = Sector.SUR,
                entregaDirectaEnPlanta = false,
                numeroVacas = 6,
                calificacion = CalificacionProveedor.B,
            ),
            Proveedor(
                id = "P-041",
                nombre = "Mario Tinta Cruz",
                documento = "43567812",
                telefono = "951034224",
                sector = Sector.OESTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 8,
                calificacion = CalificacionProveedor.B,
            ),
            Proveedor(
                id = "P-052",
                nombre = "Asoc. Ganadera Pallalla",
                documento = "20458713690",
                telefono = "951034225",
                sector = Sector.ESTE,
                entregaDirectaEnPlanta = true,
                numeroVacas = 31,
                calificacion = CalificacionProveedor.A,
            ),
            Proveedor(
                id = "P-008",
                nombre = "Felipa Aguilar Vilca",
                documento = "40774213",
                telefono = "951034226",
                sector = Sector.NORTE,
                entregaDirectaEnPlanta = false,
                numeroVacas = 5,
                calificacion = CalificacionProveedor.C,
            ),
        )

    private companion object {
        val RUTAS: Map<String, Set<String>> = mapOf(
            "A-01" to setOf("P-027", "P-052", "P-041"),
            "A-02" to setOf("P-014", "P-003", "P-008"),
        )
    }
}
