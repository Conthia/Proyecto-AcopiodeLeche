package pe.edu.upeu.acopioleche.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.acopioleche.domain.model.SesionActiva

/**
 * Tenedor en memoria de la sesión activa, en el mismo espíritu simple que [ServiceLocator]: sin
 * un backend real todavía, no hay dónde persistir un token de sesión entre procesos, así que
 * vive como estado de la app mientras el proceso sigue corriendo. Lo llena [LoginViewModel] al
 * autenticar con éxito y lo consumen las pantallas que antes leían la identidad fija
 * `SesionDemo`. Ver docs/modelo-dominio.md, sección "Decisiones de arquitectura".
 */
object SesionActivaHolder {
    private val _sesion: MutableStateFlow<SesionActiva?> = MutableStateFlow(null)
    val sesion: StateFlow<SesionActiva?> = _sesion.asStateFlow()

    fun iniciar(sesion: SesionActiva) {
        _sesion.value = sesion
    }

    fun cerrar() {
        _sesion.value = null
    }
}
