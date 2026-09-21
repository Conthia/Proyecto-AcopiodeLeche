package pe.edu.upeu.acopioleche.presentation.core

import kotlinx.coroutines.CoroutineScope

/**
 * Base común para los ViewModel de `shared`. No depende de androidx.lifecycle a propósito:
 * `shared` es una librería KMP pura (sin UI ni frameworks de plataforma), y el alcance de
 * [scope] lo decide quien construye el ViewModel — en Compose, con `rememberCoroutineScope()`,
 * cancelado automáticamente cuando la pantalla sale de composición. Ver docs/modelo-dominio.md,
 * sección "Decisiones de arquitectura".
 */
abstract class AppViewModel(
    protected val scope: CoroutineScope,
)
