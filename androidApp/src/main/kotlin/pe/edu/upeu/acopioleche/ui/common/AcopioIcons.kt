package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Iconos reconstruidos a partir de los mismos paths SVG (viewBox 24x24) del prototipo de
 * Figma, para no depender de una libreria de iconos que no esta en el catalogo de versiones.
 * El color se define en el sitio de uso via el parametro `tint` de Icon(), asi que el fill
 * de estos vectores es solo un valor de relleno inicial.
 */
private fun iconoDesdePath(nombre: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = nombre,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(pathData).toNodes(),
            fill = SolidColor(Color.Black),
        )
    }.build()

object AcopioIcons {
    val Volver: ImageVector by lazy {
        iconoDesdePath("Volver", "M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z")
    }
    val Check: ImageVector by lazy {
        iconoDesdePath(
            "Check",
            "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z",
        )
    }
    val Sincronizando: ImageVector by lazy {
        iconoDesdePath(
            "Sincronizando",
            "M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.03 20 13.57 20 12c0-4.42-3.58-8-8-8zm0 14c-3.31 0-6-2.69-6-6 0-1.01.25-1.97.7-2.8L5.24 7.74C4.46 8.97 4 10.43 4 12c0 4.42 3.58 8 8 8v3l4-4-4-4v3z",
        )
    }
    val Alerta: ImageVector by lazy {
        iconoDesdePath("Alerta", "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z")
    }
    val Bloqueado: ImageVector by lazy {
        iconoDesdePath(
            "Bloqueado",
            "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z",
        )
    }
    val Gota: ImageVector by lazy {
        iconoDesdePath("Gota", "M12 2c0 0-7 8.59-7 13a7 7 0 0014 0c0-4.41-7-13-7-13z")
    }
    val Calendario: ImageVector by lazy {
        iconoDesdePath(
            "Calendario",
            "M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z",
        )
    }
    val Personas: ImageVector by lazy {
        iconoDesdePath(
            "Personas",
            "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z",
        )
    }
    val ChevronDerecha: ImageVector by lazy {
        iconoDesdePath("ChevronDerecha", "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6z")
    }
    val Buscar: ImageVector by lazy {
        iconoDesdePath(
            "Buscar",
            "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z",
        )
    }
    val Cerrar: ImageVector by lazy {
        iconoDesdePath(
            "Cerrar",
            "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z",
        )
    }
    val CerrarSesion: ImageVector by lazy {
        iconoDesdePath(
            "CerrarSesion",
            "M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z",
        )
    }
    val Mas: ImageVector by lazy {
        iconoDesdePath("Mas", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z")
    }
    val Turbon: ImageVector by lazy {
        iconoDesdePath(
            "Turbon",
            "M3 13h2v3H3zM1 9h4v2H1zM20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4z",
        )
    }
    val Motocarga: ImageVector by lazy {
        iconoDesdePath(
            "Motocarga",
            "M19 7c0-1.1-.9-2-2-2h-3L12 3H7v2H4C2.9 5 2 5.9 2 7v9h2c0 1.66 1.34 3 3 3s3-1.34 3-3h5c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5h-3zm-9 10c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm10-4h-1v-2h1v2zm-2 4c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1z",
        )
    }
    val Reloj: ImageVector by lazy {
        iconoDesdePath(
            "Reloj",
            "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67V7z",
        )
    }
    val Recibo: ImageVector by lazy {
        iconoDesdePath(
            "Recibo",
            "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z",
        )
    }
    val PanelAdmin: ImageVector by lazy {
        iconoDesdePath(
            "PanelAdmin",
            "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z",
        )
    }
}
