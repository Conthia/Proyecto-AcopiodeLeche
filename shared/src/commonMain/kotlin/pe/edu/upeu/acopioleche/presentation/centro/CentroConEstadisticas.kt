package pe.edu.upeu.acopioleche.presentation.centro

data class CentroConEstadisticas(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val capacidadTanqueLitros: Double,
    val capacidadLitrosDia: Double = 300.0,
    val activo: Boolean = true,
    val operativo: Boolean = true,
    val numeroProveedores: Int = 0,
    val litrosHoy: Double = 0.0,
    val numeroAcopiadores: Int = 0,
)
