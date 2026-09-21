package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository

class FakeProduccionDerivadoRepository : ProduccionDerivadoRepository {

    private val _producciones: MutableStateFlow<List<ProduccionDerivado>> = MutableStateFlow(seed())
    private val producciones: StateFlow<List<ProduccionDerivado>> = _producciones.asStateFlow()

    override fun observarProduccion(): StateFlow<List<ProduccionDerivado>> = producciones

    override suspend fun guardar(produccion: ProduccionDerivado) {
        _producciones.value = _producciones.value.filterNot { it.id == produccion.id } + produccion
    }

    override suspend fun eliminar(id: String) {
        _producciones.value = _producciones.value.filterNot { it.id == id }
    }

    private fun seed(): List<ProduccionDerivado> =
        listOf(
            ProduccionDerivado(
                id = "PROD-DER-01",
                tipoProducto = "Queso Paria Fresco",
                codigoLote = "LOTE-2026-001",
                cantidadUnidades = 45.0,
                fechaProduccion = LocalDate(2026, 9, 10),
                responsableId = "PROD-LACT-01",
            ),
            ProduccionDerivado(
                id = "PROD-DER-02",
                tipoProducto = "Yogurt Bebible Frutado",
                codigoLote = "LOTE-2026-002",
                cantidadUnidades = 80.0,
                fechaProduccion = LocalDate(2026, 9, 12),
                responsableId = "PROD-LACT-01",
            ),
        )
}
