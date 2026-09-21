package pe.edu.upeu.acopioleche.presentation.dashboard.lacteos

import pe.edu.upeu.acopioleche.domain.model.InsumoLacteo
import pe.edu.upeu.acopioleche.domain.model.ProduccionDerivado

data class LacteosHomeUiState(
    val nombreResponsable: String = "",
    val producciones: List<ProduccionDerivado> = emptyList(),
    val insumos: List<InsumoLacteo> = emptyList(),
    val guardando: Boolean = false,
    val mensajeNotificacion: String? = null,
    val mensajeError: String? = null,
)
