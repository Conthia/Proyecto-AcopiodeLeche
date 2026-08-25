package pe.edu.upeu.acopioleche.domain.model

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ReunionAsistenciaTest {

    @Test
    fun `reunion con horaFin antes de horaInicio lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Reunion(
                id = "r-1",
                tipo = TipoEvento.REUNION,
                tema = "Coordinacion de entregas",
                fecha = "2026-08-24",
                horaInicioMinutos = 600,
                horaFinMinutos = 500,
            )
        }
    }

    @Test
    fun `reunion con horario valido se crea correctamente`() {
        val reunion = Reunion(
            id = "r-1",
            tipo = TipoEvento.CAPACITACION,
            tema = "Buenas practicas de ordeño",
            fecha = "2026-08-24",
            horaInicioMinutos = 480,
            horaFinMinutos = 600,
        )

        assert(reunion.horaFinMinutos > reunion.horaInicioMinutos)
    }

    @Test
    fun `asistencia sin actor asociado lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Asistencia(
                id = "as-1",
                reunionId = "r-1",
                actorId = "",
                tipoActor = TipoActor.PROVEEDOR,
                presente = true,
            )
        }
    }

    @Test
    fun `asistencia sin reunion asociada lanza excepcion`() {
        assertFailsWith<IllegalArgumentException> {
            Asistencia(
                id = "as-1",
                reunionId = "",
                actorId = "p-1",
                tipoActor = TipoActor.ACOPIADOR,
                presente = true,
            )
        }
    }
}
