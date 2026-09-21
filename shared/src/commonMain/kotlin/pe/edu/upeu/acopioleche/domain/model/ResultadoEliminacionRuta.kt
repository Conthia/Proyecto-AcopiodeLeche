package pe.edu.upeu.acopioleche.domain.model

/**
 * El admin puede eliminar una ruta asignada (RF-15) siempre que el acopiador todavía no haya
 * avanzado nada en ella: si ya hay paradas visitadas/omitidas (equivalente a que haya entregas
 * registradas ese día para esa ruta), se bloquea el borrado para no perder ese historial — el
 * admin debe editarla en su lugar.
 */
sealed interface ResultadoEliminacionRuta {
    data object Eliminada : ResultadoEliminacionRuta

    data object NoEncontrada : ResultadoEliminacionRuta

    data class TieneAvance(val paradasConAvance: Int) : ResultadoEliminacionRuta
}
