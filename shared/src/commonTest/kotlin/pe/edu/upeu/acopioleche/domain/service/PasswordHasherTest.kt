package pe.edu.upeu.acopioleche.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PasswordHasherTest {

    @Test
    fun `sha256Hex reproduce el vector de prueba conocido para la cadena vacia`() {
        assertEquals(
            expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            actual = PasswordHasher.sha256Hex(texto = ""),
        )
    }

    @Test
    fun `sha256Hex reproduce el vector de prueba conocido para abc`() {
        assertEquals(
            expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            actual = PasswordHasher.sha256Hex(texto = "abc"),
        )
    }

    @Test
    fun `hash nunca guarda la contrasena en texto plano`() {
        val resultado = PasswordHasher.hash(contrasenaPlano = "Acopio2026")

        assertNotEquals(illegal = "Acopio2026", actual = resultado.hash)
    }

    @Test
    fun `hash con la misma sal es determinista`() {
        val salFija = "0011223344556677"

        val primero = PasswordHasher.hash(contrasenaPlano = "Acopio2026", sal = salFija)
        val segundo = PasswordHasher.hash(contrasenaPlano = "Acopio2026", sal = salFija)

        assertEquals(expected = primero.hash, actual = segundo.hash)
    }

    @Test
    fun `sales distintas producen hashes distintos para la misma contrasena`() {
        val conSalA = PasswordHasher.hash(contrasenaPlano = "Acopio2026", sal = "sal-a")
        val conSalB = PasswordHasher.hash(contrasenaPlano = "Acopio2026", sal = "sal-b")

        assertNotEquals(illegal = conSalA.hash, actual = conSalB.hash)
    }

    @Test
    fun `verificar acepta la contrasena correcta y rechaza una incorrecta`() {
        val contra = PasswordHasher.hash(contrasenaPlano = "Acopio2026")

        assertEquals(expected = true, actual = PasswordHasher.verificar(contrasenaPlano = "Acopio2026", contra = contra))
        assertEquals(expected = false, actual = PasswordHasher.verificar(contrasenaPlano = "otraClave", contra = contra))
    }
}
