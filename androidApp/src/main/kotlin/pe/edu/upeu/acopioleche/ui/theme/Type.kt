package pe.edu.upeu.acopioleche.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * El prototipo usa "Nunito" (700-900, titulos/numeros/botones) e "Inter" (cuerpo/inputs).
 * No hay archivos .ttf en el proyecto todavia, asi que ambas apuntan a FontFamily.Default
 * por ahora: cuando se agreguen los .ttf reales a res/font, solo hay que reemplazar estas
 * dos constantes.
 */
val FuenteNunito = FontFamily.Default
val FuenteInter = FontFamily.Default

val AcopioTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.Black, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.Black, fontSize = 22.sp),
    headlineSmall = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
    titleLarge = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp),
    titleMedium = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.Bold, fontSize = 15.sp),
    titleSmall = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.Bold, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = FuenteNunito, fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 0.6.sp),
    labelMedium = TextStyle(fontFamily = FuenteInter, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.9.sp),
    labelSmall = TextStyle(fontFamily = FuenteInter, fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
    bodyLarge = TextStyle(fontFamily = FuenteInter, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FuenteInter, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = FuenteInter, fontWeight = FontWeight.Normal, fontSize = 12.sp),
)
