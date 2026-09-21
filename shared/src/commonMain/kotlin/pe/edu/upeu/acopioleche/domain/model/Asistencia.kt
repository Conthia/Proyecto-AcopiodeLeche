package pe.edu.upeu.acopioleche.domain.model

data class Asistencia(
    val id: String,
    val reunionId: String,
    val actorId: String,
    val tipoActor: TipoActor,
    val presente: Boolean,
)
