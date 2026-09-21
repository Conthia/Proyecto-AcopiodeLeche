package pe.edu.upeu.acopioleche.domain.model

/**
 * Los únicos 2 tipos de vehículo reales usados por los acopiadores del distrito de Huata.
 * [Acopiador.vehiculo] debe usar siempre uno de estos valores; no se deben inventar otros.
 */
object Vehiculo {
    const val TURBON: String = "Turbón"
    const val MOTOCARGA: String = "Motocarga"

    val TODOS: List<String> = listOf(TURBON, MOTOCARGA)
}
