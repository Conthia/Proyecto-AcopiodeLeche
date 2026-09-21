@file:OptIn(ExperimentalUnsignedTypes::class)

package pe.edu.upeu.acopioleche.domain.service

import kotlin.random.Random
import pe.edu.upeu.acopioleche.domain.model.ContrasenaHash

/**
 * Hashing de contraseñas en Kotlin puro, sin dependencias de plataforma (`java.security` u
 * otro), para que compile igual en Android y Desktop/JVM desde `commonMain`. Cumple RNF-02
 * ("las credenciales no se guardan en texto plano") aplicando SHA-256 con una sal aleatoria de
 * 16 bytes por usuario.
 *
 * NOTA: esto resuelve el requisito de no-texto-plano dentro del dominio de esta versión
 * exploratoria, pero NO es un reemplazo de un mecanismo de autenticación de producción
 * (bcrypt/Argon2 detrás de un backend real, con throttling y almacenamiento seguro). Mientras
 * no exista backend, el hash generado aquí vive solo en el repositorio en memoria
 * (`FakeUsuarioRepository`) — ver docs/modelo-dominio.md, sección "Decisiones de arquitectura".
 */
object PasswordHasher {

    fun hash(contrasenaPlano: String, sal: String = generarSal()): ContrasenaHash =
        ContrasenaHash(hash = sha256Hex(texto = sal + contrasenaPlano), sal = sal)

    fun verificar(contrasenaPlano: String, contra: ContrasenaHash): Boolean =
        sha256Hex(texto = contra.sal + contrasenaPlano) == contra.hash

    private fun generarSal(): String =
        Random.nextBytes(16).joinToString(separator = "") { byte -> (byte.toInt() and 0xFF).toString(16).padStart(2, '0') }

    // --- SHA-256 puro Kotlin (FIPS 180-4), expuesto `internal` solo para poder verificarlo con
    // vectores de prueba conocidos en commonTest sin exponerlo como API pública del dominio. ---

    private val CONSTANTES_K: UIntArray = uintArrayOf(
        0x428a2f98u, 0x71374491u, 0xb5c0fbcfu, 0xe9b5dba5u, 0x3956c25bu, 0x59f111f1u, 0x923f82a4u, 0xab1c5ed5u,
        0xd807aa98u, 0x12835b01u, 0x243185beu, 0x550c7dc3u, 0x72be5d74u, 0x80deb1feu, 0x9bdc06a7u, 0xc19bf174u,
        0xe49b69c1u, 0xefbe4786u, 0x0fc19dc6u, 0x240ca1ccu, 0x2de92c6fu, 0x4a7484aau, 0x5cb0a9dcu, 0x76f988dau,
        0x983e5152u, 0xa831c66du, 0xb00327c8u, 0xbf597fc7u, 0xc6e00bf3u, 0xd5a79147u, 0x06ca6351u, 0x14292967u,
        0x27b70a85u, 0x2e1b2138u, 0x4d2c6dfcu, 0x53380d13u, 0x650a7354u, 0x766a0abbu, 0x81c2c92eu, 0x92722c85u,
        0xa2bfe8a1u, 0xa81a664bu, 0xc24b8b70u, 0xc76c51a3u, 0xd192e819u, 0xd6990624u, 0xf40e3585u, 0x106aa070u,
        0x19a4c116u, 0x1e376c08u, 0x2748774cu, 0x34b0bcb5u, 0x391c0cb3u, 0x4ed8aa4au, 0x5b9cca4fu, 0x682e6ff3u,
        0x748f82eeu, 0x78a5636fu, 0x84c87814u, 0x8cc70208u, 0x90befffau, 0xa4506cebu, 0xbef9a3f7u, 0xc67178f2u,
    )

    private fun UInt.rotr(bits: Int): UInt = (this shr bits) or (this shl (32 - bits))

    internal fun sha256Hex(texto: String): String {
        val mensaje = texto.encodeToByteArray()
        val h = uintArrayOf(
            0x6a09e667u, 0xbb67ae85u, 0x3c6ef372u, 0xa54ff53au,
            0x510e527fu, 0x9b05688cu, 0x1f83d9abu, 0x5be0cd19u,
        )

        val longitudBits = mensaje.size.toULong() * 8u
        val longitudRelleno = ((56 - (mensaje.size + 1) % 64) + 64) % 64
        val longitudBitsBytes = ByteArray(8) { i -> ((longitudBits shr ((7 - i) * 8)) and 0xFFu).toByte() }
        val relleno = mensaje + byteArrayOf(0x80.toByte()) + ByteArray(longitudRelleno) + longitudBitsBytes

        var offset = 0
        val w = UIntArray(64)
        while (offset < relleno.size) {
            for (i in 0 until 16) {
                val base = offset + i * 4
                w[i] = ((relleno[base].toUInt() and 0xFFu) shl 24) or
                    ((relleno[base + 1].toUInt() and 0xFFu) shl 16) or
                    ((relleno[base + 2].toUInt() and 0xFFu) shl 8) or
                    (relleno[base + 3].toUInt() and 0xFFu)
            }
            for (i in 16 until 64) {
                val s0 = w[i - 15].rotr(7) xor w[i - 15].rotr(18) xor (w[i - 15] shr 3)
                val s1 = w[i - 2].rotr(17) xor w[i - 2].rotr(19) xor (w[i - 2] shr 10)
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }

            var a = h[0]; var b = h[1]; var c = h[2]; var d = h[3]
            var e = h[4]; var f = h[5]; var g = h[6]; var hh = h[7]

            for (i in 0 until 64) {
                val s1 = e.rotr(6) xor e.rotr(11) xor e.rotr(25)
                val ch = (e and f) xor (e.inv() and g)
                val temp1 = hh + s1 + ch + CONSTANTES_K[i] + w[i]
                val s0 = a.rotr(2) xor a.rotr(13) xor a.rotr(22)
                val maj = (a and b) xor (a and c) xor (b and c)
                val temp2 = s0 + maj

                hh = g; g = f; f = e; e = d + temp1
                d = c; c = b; b = a; a = temp1 + temp2
            }

            h[0] += a; h[1] += b; h[2] += c; h[3] += d
            h[4] += e; h[5] += f; h[6] += g; h[7] += hh

            offset += 64
        }

        return h.joinToString(separator = "") { valor -> valor.toString(16).padStart(8, '0') }
    }
}
