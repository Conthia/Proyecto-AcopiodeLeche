package pe.edu.upeu.acopioleche.domain.model

import kotlinx.datetime.LocalDateTime
import pe.edu.upeu.acopioleche.domain.service.EvaluadorCalidad
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AnalisisCampoTest {

    private val fechaPrueba = LocalDateTime(2026, 9, 15, 6, 30)

    @Test
    fun analisisEnCampoAlmacenaCriterioDeSeleccion() {
        val analisis = AnalisisCalidad(
            id = "AC-100",
            entregaId = "E-100",
            tecnicoId = "A-01",
            fecha = fechaPrueba,
            resultado = ResultadoAnalisis.Normal(
                densidad = 1.030,
                acidez = 0.0,
                grasa = 3.5,
                proteina = 3.2,
                lactosa = 4.5,
                temperatura = 4.0,
                ph = 6.6,
            ),
            criterioSeleccion = CriterioAnalisis.HISTORIAL_ALERTA,
        )

        assertEquals(CriterioAnalisis.HISTORIAL_ALERTA, analisis.criterioSeleccion)
    }

    @Test
    fun analisisEnCampoRegistraOrigenDatoOcrOManual() {
        val analisisOcr = AnalisisCalidad(
            id = "AC-101",
            entregaId = "E-101",
            tecnicoId = "A-01",
            fecha = fechaPrueba,
            resultado = ResultadoAnalisis.Normal(
                densidad = 1.031,
                acidez = 0.0,
                grasa = 3.6,
                proteina = 3.3,
                lactosa = 4.6,
                temperatura = 4.2,
                ph = 6.7,
            ),
            origenDato = OrigenDatoAnalisis.OCR_COMPROBANTE,
        )

        assertEquals(OrigenDatoAnalisis.OCR_COMPROBANTE, analisisOcr.origenDato)
    }

    @Test
    fun analisisEnCampoVerificaFirmaDelProductor() {
        val analisisConFirma = AnalisisCalidad(
            id = "AC-102",
            entregaId = "E-102",
            tecnicoId = "A-01",
            fecha = fechaPrueba,
            resultado = ResultadoAnalisis.Normal(
                densidad = 1.030,
                acidez = 0.0,
                grasa = 3.4,
                proteina = 3.1,
                lactosa = 4.5,
                temperatura = 4.1,
                ph = 6.6,
            ),
            firmaProductorPresente = true,
        )

        assertTrue(analisisConFirma.firmaProductorPresente)
    }

    @Test
    fun evaluacionDeLactoescanNormalYAdulteradoEnCampo() {
        val lecturaNormal = LecturaLactoescan(
            densidad = 1.030,
            grasa = 3.5,
            proteina = 3.2,
            lactosa = 4.5,
            temperatura = 4.0,
            ph = 6.6,
            porcentajeAguaAnadida = 0.0,
        )
        val resultadoNormal = EvaluadorCalidad.evaluar(lecturaNormal)
        assertIs<ResultadoAnalisis.Normal>(resultadoNormal)

        val lecturaAgua = LecturaLactoescan(
            densidad = 1.025,
            grasa = 2.8,
            proteina = 2.5,
            lactosa = 3.8,
            temperatura = 4.0,
            ph = 6.6,
            porcentajeAguaAnadida = 8.5,
        )
        val resultadoAdulterada = EvaluadorCalidad.evaluar(lecturaAgua)
        assertIs<ResultadoAnalisis.Adulterada>(resultadoAdulterada)
        assertEquals(8.5, resultadoAdulterada.porcentajeAgua)
    }
}
