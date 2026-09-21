package pe.edu.upeu.acopioleche.presentation.core

/**
 * Estado genérico de una pantalla que carga datos desde un `Flow` reactivo (repositorio SQLDelight
 * u otro). [Vacio] es un caso distinto de `Exito` con datos vacíos: cada ViewModel decide qué
 * significa "vacío" para su propio [T] (normalmente, una lista principal sin elementos).
 */
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>

    data class Exito<T>(val datos: T) : UiState<T>

    data object Vacio : UiState<Nothing>

    data class Error(val mensaje: String) : UiState<Nothing>
}
