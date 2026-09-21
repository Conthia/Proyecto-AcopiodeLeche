package pe.edu.upeu.acopioleche.domain.model

data class CentroAcopio(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val capacidadLitrosDia: Double,
    val activo: Boolean,
    val capacidadTanqueLitros: Double,
    val tipoCentro: TipoCentroAcopio = TipoCentroAcopio.CENTRO_SECTOR,
)
