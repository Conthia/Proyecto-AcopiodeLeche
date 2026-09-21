package pe.edu.upeu.acopioleche.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.domain.model.SesionActiva
import pe.edu.upeu.acopioleche.ui.agenda.AsistenciaScreen
import pe.edu.upeu.acopioleche.ui.agenda.ReunionesScreen
import pe.edu.upeu.acopioleche.ui.analisis.AnalisisCalidadScreen
import pe.edu.upeu.acopioleche.ui.analisis.RegistrarAnalisisScreen
import pe.edu.upeu.acopioleche.ui.centro.CentrosAcopioScreen
import pe.edu.upeu.acopioleche.ui.cola.ColaEnvioScreen
import pe.edu.upeu.acopioleche.ui.conciliacion.ConciliacionScreen
import pe.edu.upeu.acopioleche.ui.conciliacion.RegistrarConciliacionScreen
import pe.edu.upeu.acopioleche.ui.dashboard.acopiador.AcopiadorHomeScreen
import pe.edu.upeu.acopioleche.ui.dashboard.admin.PanelControlScreen
import pe.edu.upeu.acopioleche.ui.dashboard.lacteos.LacteosHomeScreen
import pe.edu.upeu.acopioleche.ui.dashboard.pagos.PagosHomeScreen
import pe.edu.upeu.acopioleche.ui.dashboard.productor.ProductorHomeScreen
import pe.edu.upeu.acopioleche.ui.entrega.EntregasDelDiaScreen
import pe.edu.upeu.acopioleche.ui.entrega.RegistrarEntregaScreen
import pe.edu.upeu.acopioleche.ui.entrega.SeleccionarProveedorScreen
import pe.edu.upeu.acopioleche.ui.liquidacion.LiquidacionesScreen
import pe.edu.upeu.acopioleche.ui.login.LoginScreen
import pe.edu.upeu.acopioleche.ui.notificacion.NotificacionesScreen
import pe.edu.upeu.acopioleche.ui.perfil.PerfilScreen
import pe.edu.upeu.acopioleche.ui.proveedor.ProveedoresScreen
import pe.edu.upeu.acopioleche.ui.reportes.ReportesScreen
import pe.edu.upeu.acopioleche.ui.ruta.AsignarRutaScreen

@Composable
fun AcopioLecheNavHost(navController: NavHostController, sesionActual: SesionActiva?) {
    NavHost(navController = navController, startDestination = Ruta.Login.ruta) {
        composable(Ruta.Login.ruta) {
            LoginScreen(
                alIngresoExitoso = { sesion ->
                    val destino = when (sesion.rol) {
                        RolUsuario.ACOPIADOR -> Ruta.AcopiadorHome.ruta
                        RolUsuario.PRODUCTOR -> Ruta.ProductorHome.ruta
                        RolUsuario.ENCARGADO_PAGOS -> Ruta.PagosHome.ruta
                        RolUsuario.PRODUCTOR_LACTEOS -> Ruta.LacteosHome.ruta
                        RolUsuario.ADMINISTRADOR -> Ruta.PanelControl.ruta
                    }
                    navController.navigate(destino) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
            )
        }
        composable(Ruta.Perfil.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR -> {
                    PerfilScreen(
                        alVolver = { navController.popBackStack() },
                        alCerrarSesion = {
                            navController.navigate(Ruta.Login.ruta) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    )
                }
                null -> Unit
            }
        }
        composable(Ruta.AcopiadorHome.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR -> {
                    AcopiadorHomeScreen(
                        alRegistrarEntrega = { navController.navigate(Ruta.SeleccionarProveedor.ruta) },
                        alVerEntregasDelDia = { navController.navigate(Ruta.EntregasDelDia.ruta) },
                        alVerColaDeEnvio = { navController.navigate(Ruta.ColaEnvio.ruta) },
                        alAbrirPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                    )
                }
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.ProductorHome.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.PRODUCTOR -> {
                    ProductorHomeScreen(
                        alAbrirPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.PagosHome.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ENCARGADO_PAGOS -> {
                    PagosHomeScreen(
                        alAbrirPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.LacteosHome.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.PRODUCTOR_LACTEOS -> {
                    LacteosHomeScreen(
                        alAbrirPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.EntregasDelDia.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR -> EntregasDelDiaScreen(alVolver = { navController.popBackStack() })
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.ColaEnvio.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR -> ColaEnvioScreen(alVolver = { navController.popBackStack() })
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.SeleccionarProveedor.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR -> {
                    SeleccionarProveedorScreen(
                        alVolver = { navController.popBackStack() },
                        alSeleccionar = { proveedorId -> navController.navigate(Ruta.RegistrarEntrega.con(proveedorId)) },
                    )
                }
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }
        composable(Ruta.RegistrarEntrega.ruta) { entry ->
            when (sesionActual?.rol) {
                RolUsuario.ACOPIADOR -> {
                    val proveedorId = entry.arguments?.getString(Ruta.RegistrarEntrega.ARG_PROVEEDOR_ID).orEmpty()
                    RegistrarEntregaScreen(
                        proveedorId = proveedorId,
                        alVolver = { navController.popBackStack() },
                        alTerminar = {
                            navController.popBackStack(route = Ruta.AcopiadorHome.ruta, inclusive = false)
                        },
                    )
                }
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                RolUsuario.ADMINISTRADOR,
                null -> Unit
            }
        }

        composable(Ruta.PanelControl.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    PanelControlScreen(
                        alVerLiquidaciones = { navController.navigate(Ruta.Liquidaciones.ruta) },
                        alVerConciliacion = { navController.navigate(Ruta.Conciliacion.ruta) },
                        alVerNotificaciones = { navController.navigate(Ruta.Notificaciones.ruta) },
                        alAbrirPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Proveedores.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> ProveedoresScreen()
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.CentrosAcopio.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> CentrosAcopioScreen()
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.AsignarRuta.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> AsignarRutaScreen(alVolver = { navController.popBackStack() })
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Reuniones.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    ReunionesScreen(alAbrirAsistencia = { reunionId -> navController.navigate(Ruta.Asistencia.con(reunionId)) })
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Asistencia.ruta) { entry ->
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    val reunionId = entry.arguments?.getString(Ruta.Asistencia.ARG_REUNION_ID).orEmpty()
                    AsistenciaScreen(reunionId = reunionId, alVolver = { navController.popBackStack() })
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Reportes.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> ReportesScreen()
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.AnalisisCalidad.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    AnalisisCalidadScreen(
                        alSeleccionar = { entregaId -> navController.navigate(Ruta.RegistrarAnalisis.con(entregaId)) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.RegistrarAnalisis.ruta) { entry ->
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    val entregaId = entry.arguments?.getString(Ruta.RegistrarAnalisis.ARG_ENTREGA_ID).orEmpty()
                    RegistrarAnalisisScreen(entregaId = entregaId, alVolver = { navController.popBackStack() })
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Liquidaciones.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> LiquidacionesScreen(alVolver = { navController.popBackStack() })
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Conciliacion.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    ConciliacionScreen(
                        alVolver = { navController.popBackStack() },
                        alSeleccionar = { entregaId -> navController.navigate(Ruta.RegistrarConciliacion.con(entregaId)) },
                    )
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.RegistrarConciliacion.ruta) { entry ->
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> {
                    val entregaId = entry.arguments?.getString(Ruta.RegistrarConciliacion.ARG_ENTREGA_ID).orEmpty()
                    RegistrarConciliacionScreen(entregaId = entregaId, alVolver = { navController.popBackStack() })
                }
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
        composable(Ruta.Notificaciones.ruta) {
            when (sesionActual?.rol) {
                RolUsuario.ADMINISTRADOR -> NotificacionesScreen(alVolver = { navController.popBackStack() })
                RolUsuario.ACOPIADOR,
                RolUsuario.PRODUCTOR,
                RolUsuario.ENCARGADO_PAGOS,
                RolUsuario.PRODUCTOR_LACTEOS,
                null -> Unit
            }
        }
    }
}

object AutorizadorNav {
    fun esPermitido(rol: RolUsuario, ruta: String?): Boolean {
        if (ruta == null) return false
        val rutaBase = when {
            ruta.startsWith("acopiador/nueva-entrega/formulario") -> "acopiador/nueva-entrega/formulario/{proveedorId}"
            ruta.startsWith("admin/reuniones/") && ruta.contains("/asistencia") -> "admin/reuniones/{reunionId}/asistencia"
            ruta.startsWith("admin/analisis") -> "admin/analisis/{entregaId}"
            ruta.startsWith("admin/conciliacion") -> "admin/conciliacion/{entregaId}"
            else -> ruta
        }

        return when (rol) {
            RolUsuario.ACOPIADOR -> {
                rutaBase == "acopiador/inicio" ||
                rutaBase == "acopiador/entregas" ||
                rutaBase == "acopiador/cola" ||
                rutaBase == "acopiador/nueva-entrega/proveedor" ||
                rutaBase == "acopiador/nueva-entrega/formulario/{proveedorId}" ||
                rutaBase == "perfil"
            }
            RolUsuario.PRODUCTOR -> {
                rutaBase == "productor/inicio" ||
                rutaBase == "perfil"
            }
            RolUsuario.ENCARGADO_PAGOS -> {
                rutaBase == "pagos/inicio" ||
                rutaBase == "perfil"
            }
            RolUsuario.PRODUCTOR_LACTEOS -> {
                rutaBase == "lacteos/inicio" ||
                rutaBase == "perfil"
            }
            RolUsuario.ADMINISTRADOR -> {
                rutaBase == "admin/panel" ||
                rutaBase == "admin/proveedores" ||
                rutaBase == "admin/centros" ||
                rutaBase == "admin/rutas" ||
                rutaBase == "admin/reuniones" ||
                rutaBase == "admin/reuniones/{reunionId}/asistencia" ||
                rutaBase == "admin/reportes" ||
                rutaBase == "admin/analisis" ||
                rutaBase == "admin/analisis/{entregaId}" ||
                rutaBase == "admin/liquidaciones" ||
                rutaBase == "admin/conciliacion" ||
                rutaBase == "admin/conciliacion/{entregaId}" ||
                rutaBase == "admin/notificaciones" ||
                rutaBase == "perfil"
            }
        }
    }
}
