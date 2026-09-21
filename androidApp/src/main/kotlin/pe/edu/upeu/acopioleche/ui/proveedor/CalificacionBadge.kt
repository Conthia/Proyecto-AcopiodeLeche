package pe.edu.upeu.acopioleche.ui.proveedor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.edu.upeu.acopioleche.domain.model.CalificacionProveedor
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun CalificacionBadge(calificacion: CalificacionProveedor, modifier: Modifier = Modifier) {
    val (color, fondo) = when (calificacion) {
        CalificacionProveedor.A -> VerdeOscuro to VerdeSuaveFondo
        CalificacionProveedor.B -> AmbarTexto to AmbarFondo
        CalificacionProveedor.C -> RojoAlerta to RojoFondo
    }
    EstadoBadge(texto = "Cal. ${calificacion.name}", color = color, colorFondo = fondo, modifier = modifier)
}
