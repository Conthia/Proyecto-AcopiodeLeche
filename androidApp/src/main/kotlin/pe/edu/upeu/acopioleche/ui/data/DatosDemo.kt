package pe.edu.upeu.acopioleche.ui.data

import pe.edu.upeu.acopioleche.domain.model.Acopiador
import pe.edu.upeu.acopioleche.domain.model.CentroAcopio
import pe.edu.upeu.acopioleche.domain.model.Proveedor
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.ui.asistencia.AsistenteUi
import pe.edu.upeu.acopioleche.ui.common.horaAMinutosDelDia

/**
 * Datos de ejemplo en memoria, equivalentes a los arrays hardcodeados del prototipo de
 * Figma. Sustituyen a un repositorio real, que todavia no existe en esta sesion.
 */
object DatosDemo {

    val sectores = listOf("Sector Norte", "Sector Sur", "Sector Este", "Sector Oeste")

    val centroAcopioActual = CentroAcopio(
        id = "centro-1",
        nombre = "Centro de Acopio Huata",
        ubicacion = "Huata, Puno",
        capacidadLitrosDia = 2000.0,
        activo = true,
    )

    val proveedores = listOf(
        Proveedor(id = "p1", nombre = "Juan Quispe Mamani", documento = "42356789", telefono = "951 234 567", sector = "Sector Norte", entregaDirectaEnPlanta = false),
        Proveedor(id = "p2", nombre = "Rosa Flores Ccapa", documento = "45123456", telefono = "951 345 678", sector = "Sector Sur", entregaDirectaEnPlanta = true),
        Proveedor(id = "p3", nombre = "Timoteo Lipa Condori", documento = "41987654", telefono = "951 456 789", sector = "Sector Este", entregaDirectaEnPlanta = false),
        Proveedor(id = "p4", nombre = "María Calla Huanca", documento = "43765432", telefono = "951 567 890", sector = "Sector Norte", entregaDirectaEnPlanta = false),
        Proveedor(id = "p5", nombre = "Pedro Apaza Ticona", documento = "44678901", telefono = "951 678 901", sector = "Sector Oeste", entregaDirectaEnPlanta = true),
    )

    val acopiadorActual = Acopiador(
        id = "a1",
        nombre = "Carlos Mamani Pari",
        vehiculo = "Turbón",
        sectoresAsignados = listOf("Sector Norte", "Sector Sur"),
    )

    val eventosIniciales = listOf(
        Reunion(id = "e1", tipo = TipoEvento.CAPACITACION, tema = "Capacitación: Higiene en el Ordeño", fecha = "05 Sep 2026", horaInicioMinutos = horaAMinutosDelDia("09:00"), horaFinMinutos = horaAMinutosDelDia("11:00")),
        Reunion(id = "e2", tipo = TipoEvento.REUNION, tema = "Reunión: Precios de temporada 2026", fecha = "10 Sep 2026", horaInicioMinutos = horaAMinutosDelDia("14:00"), horaFinMinutos = horaAMinutosDelDia("15:30")),
        Reunion(id = "e3", tipo = TipoEvento.CAPACITACION, tema = "Capacitación: Control de acidez en leche", fecha = "18 Sep 2026", horaInicioMinutos = horaAMinutosDelDia("10:00"), horaFinMinutos = horaAMinutosDelDia("12:00")),
    )

    val convocadosPorEvento = mapOf("e1" to 12, "e2" to 8, "e3" to 15)

    val asistentes = listOf(
        AsistenteUi(id = "as1", nombre = "Juan Quispe Mamani", tipoActor = TipoActor.PROVEEDOR),
        AsistenteUi(id = "as2", nombre = "Rosa Flores Ccapa", tipoActor = TipoActor.PROVEEDOR),
        AsistenteUi(id = "as3", nombre = "Carlos Mamani Pari", tipoActor = TipoActor.ACOPIADOR),
        AsistenteUi(id = "as4", nombre = "Timoteo Lipa Condori", tipoActor = TipoActor.PROVEEDOR),
        AsistenteUi(id = "as5", nombre = "Lucía Ticona Quispe", tipoActor = TipoActor.PROVEEDOR),
        AsistenteUi(id = "as6", nombre = "Bernardo Chura Apaza", tipoActor = TipoActor.PROVEEDOR),
    )
}
