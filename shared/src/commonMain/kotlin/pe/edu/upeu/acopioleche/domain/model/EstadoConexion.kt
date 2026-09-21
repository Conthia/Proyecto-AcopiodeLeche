package pe.edu.upeu.acopioleche.domain.model

// EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos
/**
 * Estado de sincronización de un [EquipoCampo] con el servidor central. Jerarquía cerrada
 * porque "pendiente" y "sin conexión" necesitan un conteo de días para que el panel de
 * administración pueda priorizar qué equipo revisar primero.
 */
sealed interface EstadoConexion {
    data object AlDia : EstadoConexion

    data class Pendiente(
        val diasSinSincronizar: Int,
    ) : EstadoConexion

    data class SinConexion(
        val dias: Int,
    ) : EstadoConexion
}
