package pe.edu.upeu.acopioleche.domain.model

/**
 * Los únicos 4 sectores reales del distrito de Huata validados con la Municipalidad.
 * [Proveedor.sector] debe usar siempre uno de estos valores; no se deben inventar otros.
 */
object Sector {
    const val NORTE: String = "Sector Norte"
    const val SUR: String = "Sector Sur"
    const val ESTE: String = "Sector Este"
    const val OESTE: String = "Sector Oeste"

    val TODOS: List<String> = listOf(NORTE, SUR, ESTE, OESTE)
}
