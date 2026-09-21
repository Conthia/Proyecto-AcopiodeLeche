package pe.edu.upeu.acopioleche.ui.nav

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pe.edu.upeu.acopioleche.domain.model.RolUsuario

class AutorizadorNavTest {

    @Test
    fun testAccesoAcopiador() {
        val rol = RolUsuario.ACOPIADOR
        assertTrue(AutorizadorNav.esPermitido(rol, "acopiador/inicio"))
        assertTrue(AutorizadorNav.esPermitido(rol, "acopiador/entregas"))
        assertTrue(AutorizadorNav.esPermitido(rol, "acopiador/cola"))
        assertTrue(AutorizadorNav.esPermitido(rol, "acopiador/nueva-entrega/proveedor"))
        assertTrue(AutorizadorNav.esPermitido(rol, "acopiador/nueva-entrega/formulario/123"))
        assertTrue(AutorizadorNav.esPermitido(rol, "perfil"))

        // No debe acceder a pantallas de admin ni de productor
        assertFalse(AutorizadorNav.esPermitido(rol, "admin/panel"))
        assertFalse(AutorizadorNav.esPermitido(rol, "productor/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "pagos/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "lacteos/inicio"))
    }

    @Test
    fun testAccesoProductor_NoPuedeVerDatosDeOtrosProductores() {
        val rol = RolUsuario.PRODUCTOR
        assertTrue(AutorizadorNav.esPermitido(rol, "productor/inicio"))
        assertTrue(AutorizadorNav.esPermitido(rol, "perfil"))

        // Estrictamente prohibido acceder al panel de acopiador (que muestra todas las entregas)
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/entregas"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/cola"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/nueva-entrega/proveedor"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/nueva-entrega/formulario/456"))
        
        // Tampoco admin ni otros roles
        assertFalse(AutorizadorNav.esPermitido(rol, "admin/panel"))
        assertFalse(AutorizadorNav.esPermitido(rol, "pagos/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "lacteos/inicio"))
    }

    @Test
    fun testAccesoEncargadoPagos() {
        val rol = RolUsuario.ENCARGADO_PAGOS
        assertTrue(AutorizadorNav.esPermitido(rol, "pagos/inicio"))
        assertTrue(AutorizadorNav.esPermitido(rol, "perfil"))

        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/entregas"))
        assertFalse(AutorizadorNav.esPermitido(rol, "productor/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "admin/panel"))
    }

    @Test
    fun testAccesoProductorLacteos() {
        val rol = RolUsuario.PRODUCTOR_LACTEOS
        assertTrue(AutorizadorNav.esPermitido(rol, "lacteos/inicio"))
        assertTrue(AutorizadorNav.esPermitido(rol, "perfil"))

        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/entregas"))
        assertFalse(AutorizadorNav.esPermitido(rol, "productor/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "admin/panel"))
    }

    @Test
    fun testAccesoAdministrador() {
        val rol = RolUsuario.ADMINISTRADOR
        assertTrue(AutorizadorNav.esPermitido(rol, "admin/panel"))
        assertTrue(AutorizadorNav.esPermitido(rol, "admin/proveedores"))
        assertTrue(AutorizadorNav.esPermitido(rol, "admin/centros"))
        assertTrue(AutorizadorNav.esPermitido(rol, "admin/reuniones"))
        assertTrue(AutorizadorNav.esPermitido(rol, "perfil"))

        assertFalse(AutorizadorNav.esPermitido(rol, "acopiador/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "productor/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "pagos/inicio"))
        assertFalse(AutorizadorNav.esPermitido(rol, "lacteos/inicio"))
    }
}
