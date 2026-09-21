package pe.edu.upeu.acopioleche.domain.model

/**
 * Resultado de aplicar [pe.edu.upeu.acopioleche.domain.service.PasswordHasher] a una contraseña:
 * nunca se guarda ni se compara la contraseña en texto plano (RNF-02). [sal] es aleatoria por
 * usuario para que dos usuarios con la misma contraseña no produzcan el mismo [hash].
 */
data class ContrasenaHash(
    val hash: String,
    val sal: String,
)
