package pe.edu.upeu.acopioleche.domain.model

/**
 * Resultado de [pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository.obtenerPrecioVigenteEn]:
 * distingue un precio de temporada real ([esRespaldo] = false) de la constante de respaldo
 * ([pe.edu.upeu.acopioleche.domain.service.CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO]),
 * usada cuando ninguna [PrecioTemporada] cubre la fecha consultada — para que el llamador pueda
 * registrar ese caso con `AppLogger.warn` en vez de aplicar el respaldo en silencio.
 */
data class PrecioVigente(
    val precioPorLitro: Double,
    val esRespaldo: Boolean,
)
