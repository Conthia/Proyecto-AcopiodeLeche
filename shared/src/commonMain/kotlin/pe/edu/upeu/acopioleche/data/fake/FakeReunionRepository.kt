package pe.edu.upeu.acopioleche.data.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository

class FakeReunionRepository : ReunionRepository {

    private val _reuniones: MutableStateFlow<List<Reunion>> = MutableStateFlow(seed())
    private val reuniones: StateFlow<List<Reunion>> = _reuniones.asStateFlow()

    override fun observarReuniones(): StateFlow<List<Reunion>> = reuniones

    override suspend fun guardar(reunion: Reunion) {
        _reuniones.value = _reuniones.value + reunion
    }

    override suspend fun actualizar(reunion: Reunion) {
        _reuniones.value = _reuniones.value.map { if (it.id == reunion.id) reunion else it }
    }

    override suspend fun eliminar(id: String) {
        _reuniones.value = _reuniones.value.filter { it.id != id }
    }

    private fun seed(): List<Reunion> =
        listOf(
            Reunion(
                id = "R-08",
                tipo = TipoEvento.REUNION,
                tema = "Reunión mensual de productores",
                fecha = LocalDate(2026, 9, 12),
                horaInicioMinutos = 9 * 60,
                horaFinMinutos = 11 * 60,
                lugar = "Local comunal Huata Centro",
            ),
            Reunion(
                id = "R-07",
                tipo = TipoEvento.CAPACITACION,
                tema = "Ordeño higiénico y cadena de frío",
                fecha = LocalDate(2026, 9, 10),
                horaInicioMinutos = 15 * 60,
                horaFinMinutos = 17 * 60,
                lugar = "Centro de acopio Coyme",
            ),
            Reunion(
                id = "R-06",
                tipo = TipoEvento.CAPACITACION,
                tema = "Prevención de mastitis",
                fecha = LocalDate(2026, 8, 29),
                horaInicioMinutos = 15 * 60,
                horaFinMinutos = 17 * 60,
                lugar = "Local comunal Huata Centro",
            ),
        )
}
