package pe.edu.upeu.acopioleche.presentation.cola

import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen

data class ColaEnvioUiState(
    val enLinea: Boolean = true,
    val sincronizando: Boolean = false,
    val outbox: List<EntregaResumen> = emptyList(),
    /** Proveedores con alta/edición/baja local sin confirmar por el backend (piloto Fase 1). */
    val proveedoresPendientes: Int = 0,
    val ultimoMensaje: String? = null,
) {
    val numeroPendientes: Int get() = outbox.count { !it.sincronizada } + proveedoresPendientes
}
