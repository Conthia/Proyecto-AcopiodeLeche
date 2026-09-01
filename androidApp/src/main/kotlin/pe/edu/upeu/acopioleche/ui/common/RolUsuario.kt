package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.ui.graphics.Color
import pe.edu.upeu.acopioleche.ui.theme.Dorado
import pe.edu.upeu.acopioleche.ui.theme.MoradoAdministrador
import pe.edu.upeu.acopioleche.ui.theme.VerdeTecnico

enum class RolUsuario(val etiqueta: String, val inicial: String, val color: Color) {
    ACOPIADOR("Acopiador", "AC", Dorado),
    TECNICO("Técnico", "TC", VerdeTecnico),
    ADMINISTRADOR("Administrador", "AD", MoradoAdministrador),
}
