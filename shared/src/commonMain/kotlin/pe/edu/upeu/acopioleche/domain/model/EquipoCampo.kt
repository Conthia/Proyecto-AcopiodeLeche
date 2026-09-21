package pe.edu.upeu.acopioleche.domain.model

// EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
data class EquipoCampo(
    val id: String,
    val nombre: String,
    val tipo: String,
    val centroAcopioId: String,
    val estadoConexion: EstadoConexion,
)
