package pe.edu.upeu.acopioleche.ui.common

fun horaAMinutosDelDia(hora: String): Int {
    val (horas, minutos) = hora.split(":").map { it.toInt() }
    return horas * 60 + minutos
}

fun minutosDelDiaAHora(minutos: Int): String {
    val horas = minutos / 60
    val mins = minutos % 60
    return "%02d:%02d".format(horas, mins)
}
