package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.repository.InsumoRepository

class FakeInsumoRepository : InsumoRepository {

    private val _insumos: MutableStateFlow<List<InsumoLacteo>> = MutableStateFlow(seed())
    private val insumos: StateFlow<List<InsumoLacteo>> = _insumos.asStateFlow()

    override fun observarInsumos(): StateFlow<List<InsumoLacteo>> = insumos

    override suspend fun guardar(insumo: InsumoLacteo) {
        _insumos.value = _insumos.value.filterNot { it.id == insumo.id } + insumo
    }

    override suspend fun eliminar(id: String) {
        _insumos.value = _insumos.value.filterNot { it.id == id }
    }

    private fun seed(): List<InsumoLacteo> =
        listOf(
            InsumoLacteo(
                id = "INS-01",
                nombreInsumo = "Cuajo Líquido Grado A",
                cantidad = 5.0,
                unidadMedida = "Litros",
                fechaIngreso = LocalDate(2026, 9, 1),
            ),
            InsumoLacteo(
                id = "INS-02",
                nombreInsumo = "Sal de Mar Granulada",
                cantidad = 50.0,
                unidadMedida = "Kg",
                fechaIngreso = LocalDate(2026, 9, 5),
            ),
            InsumoLacteo(
                id = "INS-03",
                nombreInsumo = "Cultivo Láctico para Yogurt",
                cantidad = 20.0,
                unidadMedida = "Sobres",
                fechaIngreso = LocalDate(2026, 9, 8),
            ),
        )
}
