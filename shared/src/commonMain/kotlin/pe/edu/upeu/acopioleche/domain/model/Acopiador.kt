package pe.edu.upeu.acopioleche.domain.model

data class Acopiador(
    val id: String,
    val nombre: String,
    val vehiculo: String,
    val sectoresAsignados: List<String>,
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del acopiador no puede estar vacio" }
        require(sectoresAsignados.isNotEmpty()) { "El acopiador debe tener al menos un sector asignado" }
    }
}
