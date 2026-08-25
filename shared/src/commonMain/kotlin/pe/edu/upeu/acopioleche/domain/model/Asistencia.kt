package pe.edu.upeu.acopioleche.domain.model

data class Asistencia(
    val id: String,
    val reunionId: String,
    val actorId: String,
    val tipoActor: TipoActor,
    val presente: Boolean,
) {
    init {
        require(reunionId.isNotBlank()) { "La asistencia debe estar asociada a una reunion" }
        require(actorId.isNotBlank()) { "La asistencia debe estar asociada a un actor" }
    }
}
