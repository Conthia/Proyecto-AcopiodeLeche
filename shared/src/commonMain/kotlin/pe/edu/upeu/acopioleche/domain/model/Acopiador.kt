package pe.edu.upeu.acopioleche.domain.model

data class Acopiador(
    val id: String,
    val nombre: String,
    val vehiculo: String,
    val sectoresAsignados: List<String>,
)
