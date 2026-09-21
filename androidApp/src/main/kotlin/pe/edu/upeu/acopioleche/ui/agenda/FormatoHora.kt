package pe.edu.upeu.acopioleche.ui.agenda

fun formatearRangoHoras(inicioMinutos: Int, finMinutos: Int): String =
    "${formatearMinutos(inicioMinutos)}–${formatearMinutos(finMinutos)}"

private fun formatearMinutos(minutos: Int): String {
    val horas = (minutos / 60).toString().padStart(2, '0')
    val mins = (minutos % 60).toString().padStart(2, '0')
    return "$horas:$mins"
}
