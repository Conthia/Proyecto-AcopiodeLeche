package pe.edu.upeu.acopioleche.domain.model

data class Reunion(
    val id: String,
    val tipo: TipoEvento,
    val tema: String,
    val fecha: String,
    val horaInicioMinutos: Int,
    val horaFinMinutos: Int,
) {
    init {
        require(tema.isNotBlank()) { "La reunion debe tener un tema" }
        require(horaInicioMinutos in 0..1439) { "La hora de inicio debe estar dentro de un dia (0..1439)" }
        require(horaFinMinutos in 0..1439) { "La hora de fin debe estar dentro de un dia (0..1439)" }
        require(horaFinMinutos > horaInicioMinutos) { "La hora de fin debe ser posterior a la hora de inicio" }
    }
}
