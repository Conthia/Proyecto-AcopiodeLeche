package pe.edu.upeu.acopioleche.ui.nav

/**
 * Rutas de navegación, sealed interface (no sealed class, por convención del proyecto).
 * Se usan strings simples con androidx.navigation.compose clásico, sin serialización de rutas
 * tipadas, para mantener el andamiaje simple en esta versión exploratoria.
 */
sealed interface Ruta {
    val ruta: String

    data object Login : Ruta {
        override val ruta: String = "login"
    }

    data object Perfil : Ruta {
        override val ruta: String = "perfil"
    }

    data object AcopiadorHome : Ruta {
        override val ruta: String = "acopiador/inicio"
    }

    data object ProductorHome : Ruta {
        override val ruta: String = "productor/inicio"
    }

    data object PagosHome : Ruta {
        override val ruta: String = "pagos/inicio"
    }

    data object LacteosHome : Ruta {
        override val ruta: String = "lacteos/inicio"
    }

    data object EntregasDelDia : Ruta {
        override val ruta: String = "acopiador/entregas"
    }

    data object ColaEnvio : Ruta {
        override val ruta: String = "acopiador/cola"
    }

    data object SeleccionarProveedor : Ruta {
        override val ruta: String = "acopiador/nueva-entrega/proveedor"
    }

    data object RegistrarEntrega : Ruta {
        const val ARG_PROVEEDOR_ID: String = "proveedorId"
        override val ruta: String = "acopiador/nueva-entrega/formulario/{$ARG_PROVEEDOR_ID}"
        fun con(proveedorId: String): String = "acopiador/nueva-entrega/formulario/$proveedorId"
    }

    data object PanelControl : Ruta {
        override val ruta: String = "admin/panel"
    }

    data object Proveedores : Ruta {
        override val ruta: String = "admin/proveedores"
    }

    data object CentrosAcopio : Ruta {
        override val ruta: String = "admin/centros"
    }

    data object AsignarRuta : Ruta {
        override val ruta: String = "admin/rutas"
    }

    data object Reuniones : Ruta {
        override val ruta: String = "admin/reuniones"
    }

    data object Asistencia : Ruta {
        const val ARG_REUNION_ID: String = "reunionId"
        override val ruta: String = "admin/reuniones/{$ARG_REUNION_ID}/asistencia"
        fun con(reunionId: String): String = "admin/reuniones/$reunionId/asistencia"
    }

    data object Reportes : Ruta {
        override val ruta: String = "admin/reportes"
    }

    data object AnalisisCalidad : Ruta {
        override val ruta: String = "admin/analisis"
    }

    data object RegistrarAnalisis : Ruta {
        const val ARG_ENTREGA_ID: String = "entregaId"
        override val ruta: String = "admin/analisis/{$ARG_ENTREGA_ID}"
        fun con(entregaId: String): String = "admin/analisis/$entregaId"
    }

    data object Liquidaciones : Ruta {
        override val ruta: String = "admin/liquidaciones"
    }

    data object Conciliacion : Ruta {
        override val ruta: String = "admin/conciliacion"
    }

    data object RegistrarConciliacion : Ruta {
        const val ARG_ENTREGA_ID: String = "entregaId"
        override val ruta: String = "admin/conciliacion/{$ARG_ENTREGA_ID}"
        fun con(entregaId: String): String = "admin/conciliacion/$entregaId"
    }

    data object Notificaciones : Ruta {
        override val ruta: String = "admin/notificaciones"
    }
}
