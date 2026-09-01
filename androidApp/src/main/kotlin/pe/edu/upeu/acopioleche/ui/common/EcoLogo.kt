package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Version simplificada (con Canvas) del logo original en SVG del prototipo: circulo azul,
 * colinas verdes, sol dorado y una silueta blanca de vaca. No es un calco pixel a pixel del
 * SVG (eso requeriria una libreria de parseo de SVG que no esta en el catalogo), pero
 * conserva la misma composicion y paleta.
 */
@Composable
fun EcoLogo(modifier: Modifier = Modifier, tamano: Dp = 80.dp) {
    Canvas(modifier = modifier.size(tamano)) {
        val r = size.minDimension / 2f
        val centro = Offset(size.width / 2f, size.height / 2f)

        drawCircle(color = Color(0xFF1B4F8C), radius = r, center = centro)

        clipRect(left = 0f, top = 0f, right = size.width, bottom = size.height) {
            drawCircle(
                color = Color(0xFF3A8A50),
                radius = r * 0.62f,
                center = Offset(centro.x, size.height * 0.92f),
            )
            drawCircle(
                color = Color(0xFF2A6B3F),
                radius = r * 0.5f,
                center = Offset(centro.x * 0.65f, size.height * 0.98f),
            )
        }

        drawCircle(
            color = Color(0xFFF5A623),
            radius = r * 0.18f,
            center = Offset(size.width * 0.76f, size.height * 0.27f),
            style = Fill,
        )
        drawCircle(
            color = Color(0xFFF5A623).copy(alpha = 0.2f),
            radius = r * 0.24f,
            center = Offset(size.width * 0.76f, size.height * 0.27f),
        )

        // Cuerpo de la vaca
        drawOval(
            color = Color.White,
            topLeft = Offset(centro.x - r * 0.42f, centro.y - r * 0.06f),
            size = Size(r * 0.7f, r * 0.46f),
        )
        // Cabeza
        drawCircle(
            color = Color.White,
            radius = r * 0.2f,
            center = Offset(centro.x + r * 0.26f, centro.y - r * 0.16f),
        )
        // Manchas
        drawCircle(
            color = Color(0xFF1B3A5C).copy(alpha = 0.25f),
            radius = r * 0.09f,
            center = Offset(centro.x - r * 0.2f, centro.y - r * 0.1f),
        )
    }
}
