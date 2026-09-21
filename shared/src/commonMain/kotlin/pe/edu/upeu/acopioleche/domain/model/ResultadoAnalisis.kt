package pe.edu.upeu.acopioleche.domain.model

/**
 * Resultado del análisis fisicoquímico de un lote de leche. Jerarquía cerrada porque cada
 * desenlace exige datos distintos: un resultado normal registra los tres parámetros medidos,
 * uno fuera de rango debe señalar cuál parámetro y contra qué rango se comparó, y una
 * adulteración registra el indicio detectado en campo o laboratorio.
 */
sealed interface ResultadoAnalisis {
    data class Normal(
        val densidad: Double,
        val acidez: Double,
        val grasa: Double,
        // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos.
        // El "lactoescan" real de planta (confirmado en reunión del 30/08/2026) mide estos 4
        // parámetros además de densidad/grasa. `acidez` se conserva porque la Parte A la exige,
        // pero el dispositivo real no la reporta: queda oculta en el formulario de registro y se
        // guarda en 0.0 (ver RegistrarAnalisisScreen y docs/modelo-dominio.md, sección 3).
        val proteina: Double,
        val lactosa: Double,
        val temperatura: Double,
        val ph: Double,
    ) : ResultadoAnalisis

    data class FueraDeRango(
        val motivo: MotivoRechazo,
        val valorMedido: Double,
        val rangoPermitido: ClosedRange<Double>,
    ) : ResultadoAnalisis

    data class Adulterada(
        val indicio: String,
        // EXTENSIÓN PROPUESTA — pendiente de validar con el interesado, no confirmado en la Matriz de Requerimientos.
        // El lactoescan mide directamente el % de agua añadida (antes solo había texto libre en
        // `indicio`). La Fase 4 (RN-10/11/12) necesita este número para compararlo contra el
        // umbral de 5%, no solo describirlo.
        val porcentajeAgua: Double,
    ) : ResultadoAnalisis
}
