package pe.edu.upeu.acopioleche.presentation.login

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.acopioleche.data.fake.FakeUsuarioRepository
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.RolUsuario
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio

class LoginViewModelTest {

    @Test
    fun loginExitosoParaAcopiadorIniciaSesionCorrectamente() = runBlocking {
        try {
            val repository = FakeUsuarioRepository()
            val viewModel = LoginViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                usuarioRepository = repository,
                reglasNegocio = ReglasNegocio(),
            )

            viewModel.onNombreUsuarioChange("jmamani")
            viewModel.onContrasenaChange("Acopio2026")
            viewModel.onIngresarClick()

            val state = viewModel.uiState.value
            val sesion = state.sesionIniciada
            assertNull(state.mensajeError)
            assertNotNull(sesion)
            assertEquals("A-01", sesion.usuarioId)
            assertEquals(RolUsuario.ACOPIADOR, sesion.rol)
            assertEquals("CA-002", sesion.centroAcopioId)
            assertEquals(sesion, SesionActivaHolder.sesion.value)
        } finally {
            SesionActivaHolder.cerrar()
        }
    }

    @Test
    fun loginExitosoParaAdministradorAsignaRolAdministrador() = runBlocking {
        try {
            val repository = FakeUsuarioRepository()
            val viewModel = LoginViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                usuarioRepository = repository,
                reglasNegocio = ReglasNegocio(),
            )

            viewModel.onNombreUsuarioChange("admin")
            viewModel.onContrasenaChange("Admin2026")
            viewModel.onIngresarClick()

            val state = viewModel.uiState.value
            val sesion = state.sesionIniciada
            assertNull(state.mensajeError)
            assertNotNull(sesion)
            assertEquals("ADM-01", sesion.usuarioId)
            assertEquals(RolUsuario.ADMINISTRADOR, sesion.rol)
            assertNull(sesion.centroAcopioId)
        } finally {
            SesionActivaHolder.cerrar()
        }
    }

    @Test
    fun loginConContrasenaIncorrectaMuestraErrorYNoIniciaSesion() = runBlocking {
        try {
            val repository = FakeUsuarioRepository()
            val viewModel = LoginViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                usuarioRepository = repository,
                reglasNegocio = ReglasNegocio(),
            )

            viewModel.onNombreUsuarioChange("jmamani")
            viewModel.onContrasenaChange("WrongPass")
            viewModel.onIngresarClick()

            val state = viewModel.uiState.value
            assertEquals("Usuario o contraseña incorrectos", state.mensajeError)
            assertNull(state.sesionIniciada)
        } finally {
            SesionActivaHolder.cerrar()
        }
    }

    @Test
    fun tresIntentosFallidosConsecutivosBloqueanLaCuentaPor10Minutos() = runBlocking {
        try {
            val repository = FakeUsuarioRepository()
            val viewModel = LoginViewModel(
                scope = CoroutineScope(Dispatchers.Unconfined),
                usuarioRepository = repository,
                reglasNegocio = ReglasNegocio(),
            )

            repeat(3) {
                viewModel.onNombreUsuarioChange("jmamani")
                viewModel.onContrasenaChange("BadPass")
                viewModel.onIngresarClick()
            }

            val state = viewModel.uiState.value
            assertNotNull(state.mensajeError)
            assertEquals(
                "Cuenta bloqueada por intentos fallidos. Espere 10 minutos e inténtelo de nuevo.",
                state.mensajeError,
            )
        } finally {
            SesionActivaHolder.cerrar()
        }
    }
}
