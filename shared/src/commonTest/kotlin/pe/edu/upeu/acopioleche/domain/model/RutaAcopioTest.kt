package pe.edu.upeu.acopioleche.domain.model

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.data.fake.FakeRutaRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RutaAcopioTest {

    private val fechaPrueba = LocalDate(2026, 9, 15)

    @Test
    fun seCreaRutaDeAcopioConParadasOrdenadas() {
        val paradas = listOf(
            ParadaRuta(orden = 1, proveedorId = "P-001"),
            ParadaRuta(orden = 2, proveedorId = "P-002"),
            ParadaRuta(orden = 3, proveedorId = "P-003"),
        )
        val ruta = RutaAcopio(
            id = "R-100",
            nombre = "Ruta Norte 1",
            acopiadorId = "A-01",
            centroSectorId = "CA-001",
            fecha = fechaPrueba,
            paradas = paradas,
        )

        assertEquals(3, ruta.paradas.size)
        assertEquals(1, ruta.paradas[0].orden)
        assertEquals("P-001", ruta.paradas[0].proveedorId)
        assertEquals(EstadoRuta.EN_CURSO, ruta.estado)
    }

    @Test
    fun observarRutaDelDiaRetornaLaRutaAsignadaAlAcopiador() {
        runBlocking {
            val repository = FakeRutaRepository()
            val rutaPrueba = RutaAcopio(
                id = "R-200",
                nombre = "Ruta Coyme",
                acopiadorId = "A-99",
                centroSectorId = "CA-002",
                fecha = fechaPrueba,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-010")),
            )
            repository.asignarRuta(rutaPrueba)

            val encontrada = repository.observarRutaDelDia(acopiadorId = "A-99", fecha = fechaPrueba).first()
            assertNotNull(encontrada)
            assertEquals("CA-002", encontrada.centroSectorId)
        }
    }

    @Test
    fun cerrarRutaCambiaEstadoDeLaRutaAFinalizada() {
        runBlocking {
            val repository = FakeRutaRepository()
            val rutaPrueba = RutaAcopio(
                id = "R-300",
                nombre = "Ruta Cierre Test",
                acopiadorId = "A-88",
                centroSectorId = "CA-003",
                fecha = fechaPrueba,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-020")),
            )
            repository.asignarRuta(rutaPrueba)

            val exito = repository.cerrarRuta(rutaId = "R-300", volumenTotalDescargado = 150.0)
            assertEquals(true, exito)

            val actualizada = repository.observarRutaDelDia(acopiadorId = "A-88", fecha = fechaPrueba).first()
            assertNotNull(actualizada)
            assertEquals(EstadoRuta.FINALIZADA, actualizada.estado)
        }
    }

    @Test
    fun asignarRutaPermiteVariarLaRutaDelAcopiadorEntreJornadas() {
        runBlocking {
            val repository = FakeRutaRepository()
            val fecha1 = LocalDate(2026, 9, 15)
            val fecha2 = LocalDate(2026, 9, 16)

            val rutaDia1 = RutaAcopio(
                id = "R-401",
                nombre = "Ruta Dia 1",
                acopiadorId = "A-77",
                centroSectorId = "CA-001",
                fecha = fecha1,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-001")),
            )
            val rutaDia2 = RutaAcopio(
                id = "R-402",
                nombre = "Ruta Dia 2",
                acopiadorId = "A-77",
                centroSectorId = "CA-002",
                fecha = fecha2,
                paradas = listOf(ParadaRuta(orden = 1, proveedorId = "P-002")),
            )

            repository.asignarRuta(rutaDia1)
            repository.asignarRuta(rutaDia2)

            val r1 = repository.observarRutaDelDia(acopiadorId = "A-77", fecha = fecha1).first()
            val r2 = repository.observarRutaDelDia(acopiadorId = "A-77", fecha = fecha2).first()

            assertNotNull(r1)
            assertNotNull(r2)
            assertEquals("CA-001", r1.centroSectorId)
            assertEquals("CA-002", r2.centroSectorId)
        }
    }
}
