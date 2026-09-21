package pe.edu.upeu.acopioleche.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pe.edu.upeu.acopioleche.data.remote.HttpClientFactory
import pe.edu.upeu.acopioleche.data.remote.api.ProveedorApi
import pe.edu.upeu.acopioleche.data.sql.SqlAnalisisCalidadRepository
import pe.edu.upeu.acopioleche.data.sql.SqlAsistenciaRepository
import pe.edu.upeu.acopioleche.data.sql.SqlCapacitacionCorrectivaRepository
import pe.edu.upeu.acopioleche.data.sql.SqlCentroAcopioRepository
import pe.edu.upeu.acopioleche.data.sql.SqlEntregaRepository
import pe.edu.upeu.acopioleche.data.sql.SqlEquipoCampoRepository
import pe.edu.upeu.acopioleche.data.sql.SqlInsumoRepository
import pe.edu.upeu.acopioleche.data.sql.SqlLiquidacionRepository
import pe.edu.upeu.acopioleche.data.sql.SqlNotificacionRepository
import pe.edu.upeu.acopioleche.data.sql.SqlPagoRepository
import pe.edu.upeu.acopioleche.data.sql.SqlPrecioTemporadaRepository
import pe.edu.upeu.acopioleche.data.sql.SqlProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.data.sql.SqlProveedorRepository
import pe.edu.upeu.acopioleche.data.sql.SqlReunionRepository
import pe.edu.upeu.acopioleche.data.sql.SqlRutaRepository
import pe.edu.upeu.acopioleche.data.sql.SqlSancionRepository
import pe.edu.upeu.acopioleche.data.sql.SqlUsuarioRepository
import pe.edu.upeu.acopioleche.data.sqldelight.AcopioLecheDatabase
import pe.edu.upeu.acopioleche.data.sqldelight.DatabaseDriverFactory
import pe.edu.upeu.acopioleche.data.sync.AutoSyncController
import pe.edu.upeu.acopioleche.data.sync.ConnectivityObserver
import pe.edu.upeu.acopioleche.data.sync.ProveedorSyncManager
import pe.edu.upeu.acopioleche.data.sync.SincronizadorDeEntidad
import pe.edu.upeu.acopioleche.data.sync.SyncCoordinator
import pe.edu.upeu.acopioleche.domain.repository.AnalisisCalidadRepository
import pe.edu.upeu.acopioleche.domain.repository.AsistenciaRepository
import pe.edu.upeu.acopioleche.domain.repository.CapacitacionCorrectivaRepository
import pe.edu.upeu.acopioleche.domain.repository.CentroAcopioRepository
import pe.edu.upeu.acopioleche.domain.repository.EntregaRepository
import pe.edu.upeu.acopioleche.domain.repository.EquipoCampoRepository
import pe.edu.upeu.acopioleche.domain.repository.InsumoRepository
import pe.edu.upeu.acopioleche.domain.repository.LiquidacionRepository
import pe.edu.upeu.acopioleche.domain.repository.NotificacionRepository
import pe.edu.upeu.acopioleche.domain.repository.PagoRepository
import pe.edu.upeu.acopioleche.domain.repository.PrecioTemporadaRepository
import pe.edu.upeu.acopioleche.domain.repository.ProduccionDerivadoRepository
import pe.edu.upeu.acopioleche.domain.repository.ProveedorRepository
import pe.edu.upeu.acopioleche.domain.repository.ReunionRepository
import pe.edu.upeu.acopioleche.domain.repository.RutaRepository
import pe.edu.upeu.acopioleche.domain.repository.SancionRepository
import pe.edu.upeu.acopioleche.domain.repository.UsuarioRepository
import pe.edu.upeu.acopioleche.domain.service.ReglasNegocio

/**
 * Localizador de servicios deliberadamente simple para esta versión exploratoria: no hay
 * inyección de dependencias con frameworks (Koin/Hilt) todavía. Cada plataforma llama a [init]
 * una sola vez al arrancar (`AcopioLecheApplication.onCreate()` en Android, `main()` en
 * Desktop) con el [DatabaseDriverFactory] que le corresponde, antes de que cualquier pantalla
 * toque `ServiceLocator.*Repository`. Ver docs/modelo-dominio.md, sección "Decisiones de
 * arquitectura".
 *
 * Antes de la Fase 2, esto exponía implementaciones en memoria (`data/fake`) que se reiniciaban
 * en cada arranque. Ahora expone implementaciones reales con SQLDelight (RNF-03): los mismos
 * datos sobreviven al cierre de la app. Las `Fake*` se conservan como implementación alternativa
 * para tests, no se usan aquí.
 */
object ServiceLocator {
    private lateinit var database: AcopioLecheDatabase
    private lateinit var connectivityObserver: ConnectivityObserver

    /** Vive mientras viva el proceso: la usan la sincronización automática y el HttpClient. */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun init(driverFactory: DatabaseDriverFactory, connectivityObserver: ConnectivityObserver) {
        if (::database.isInitialized) return
        database = AcopioLecheDatabase(driverFactory.createDriver())
        this.connectivityObserver = connectivityObserver
        autoSyncController.iniciar(applicationScope)
    }

    val entregaRepository: EntregaRepository by lazy { SqlEntregaRepository(database) }
    val proveedorRepository: ProveedorRepository by lazy { SqlProveedorRepository(database) }
    val centroAcopioRepository: CentroAcopioRepository by lazy { SqlCentroAcopioRepository(database) }
    val analisisCalidadRepository: AnalisisCalidadRepository by lazy { SqlAnalisisCalidadRepository(database) }
    val equipoCampoRepository: EquipoCampoRepository by lazy { SqlEquipoCampoRepository(database) }
    val reunionRepository: ReunionRepository by lazy { SqlReunionRepository(database) }
    val asistenciaRepository: AsistenciaRepository by lazy { SqlAsistenciaRepository(database) }
    val usuarioRepository: UsuarioRepository by lazy { SqlUsuarioRepository(database) }
    val sancionRepository: SancionRepository by lazy { SqlSancionRepository(database) }
    val capacitacionCorrectivaRepository: CapacitacionCorrectivaRepository by lazy { SqlCapacitacionCorrectivaRepository(database) }
    val liquidacionRepository: LiquidacionRepository by lazy { SqlLiquidacionRepository(database) }
    val notificacionRepository: NotificacionRepository by lazy { SqlNotificacionRepository(database) }
    val rutaRepository: RutaRepository by lazy { SqlRutaRepository(database) }
    val precioTemporadaRepository: PrecioTemporadaRepository by lazy { SqlPrecioTemporadaRepository(database) }
    val pagoRepository: PagoRepository by lazy { SqlPagoRepository(database) }
    val produccionDerivadoRepository: ProduccionDerivadoRepository by lazy { SqlProduccionDerivadoRepository(database) }
    val insumoRepository: InsumoRepository by lazy { SqlInsumoRepository(database) }

    /** Reglas de negocio parametrizables (ver `ReglasNegocio`), únicas para toda la app. */
    val reglasNegocio: ReglasNegocio by lazy { ReglasNegocio() }

    // --- Red y sincronización offline-first (piloto Fase 1: Proveedor) ---
    // Ver docs/sincronizacion.md. Para replicar el patrón con Entrega/AnalisisCalidad/
    // RutaAcopio/Liquidacion: agregar su `*Api` + `*SyncManager` acá y sumarlo a la lista de
    // `syncCoordinator`; nada más de esta capa necesita cambiar.
    private val httpClient by lazy { HttpClientFactory.create() }
    private val proveedorApi by lazy { ProveedorApi(httpClient) }
    val proveedorSyncManager: SincronizadorDeEntidad by lazy { ProveedorSyncManager(proveedorRepository, proveedorApi) }
    val syncCoordinator: SyncCoordinator by lazy { SyncCoordinator(sincronizadores = listOf(proveedorSyncManager)) }
    private val autoSyncController by lazy { AutoSyncController(connectivityObserver, syncCoordinator) }
}
