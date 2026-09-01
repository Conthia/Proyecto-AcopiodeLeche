package pe.edu.upeu.acopioleche.ui.asistencia

import pe.edu.upeu.acopioleche.domain.model.TipoActor

data class AsistenteUi(
    val id: String,
    val nombre: String,
    val tipoActor: TipoActor,
)
