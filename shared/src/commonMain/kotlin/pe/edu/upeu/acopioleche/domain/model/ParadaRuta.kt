package pe.edu.upeu.acopioleche.domain.model

data class ParadaRuta(
    val orden: Int,
    val proveedorId: String,
    val estadoParada: EstadoParada = EstadoParada.PENDIENTE,
)
