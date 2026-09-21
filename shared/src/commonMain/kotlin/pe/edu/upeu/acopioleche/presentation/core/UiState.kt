package pe.edu.upeu.acopioleche.presentation.core

/**
 * Estado genérico de una pantalla que carga datos desde un `Flow` reactivo (repositorio SQLDelight
 * u otro). [Vacio] es un caso distinto de `Exito` con datos vacíos: cada ViewModel decide qué
 * significa "vacío" para su propio [T] (normalmente, una lista principal sin elementos).
 *
 * Regla: [Vacio] solo aplica si la pantalla no tiene nada más que mostrar aparte de la lista. Si
 * el [T] incluye datos propios independientes de la lista (cabecera de una entidad específica,
 * totales, contadores que no sean un simple recuento de la propia lista), una lista vacía sigue
 * siendo [Exito] con un mensaje inline dentro de la pantalla, para no perder esa información.
 */
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>

    data class Exito<T>(val datos: T) : UiState<T>

    data object Vacio : UiState<Nothing>

    data class Error(val mensaje: String) : UiState<Nothing>
}
