package pe.edu.upeu.acopioleche.ui.dashboard.tecnico

data class VisitaResumen(
    val id: String,
    val proveedorNombre: String,
    val fecha: String,
    val tipo: String,
    val resultado: String,
)
