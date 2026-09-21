# Auditoría técnica — AcopioLeche v2 (exploratorio)

**Fecha del análisis:** 2026-09-20
**Alcance:** solo lectura, sin modificaciones al código. Fuente de requisitos: `docs/modelo-dominio.md` y `docs/sincronizacion.md` (no existe una matriz de requisitos formal RF/RN en el repositorio; los códigos usados abajo son los que el propio equipo referenció en documentación y comentarios de código).

---

## 1. Resumen general

**Estado aproximado: ~65-70% de avance** sobre el alcance que la propia documentación declara para esta fase exploratoria (no sobre un alcance de producción completo, que sería menor).

Por qué:
- **Compila y pasa todos los tests** (`./gradlew build` → `BUILD SUCCESSFUL`, `androidApp:test` y `shared:allTests` en verde). Esto es una base sólida: no hay deuda de "código roto".
- El **dominio, la persistencia (SQLDelight) y la mayoría de los CRUDs** (Proveedores, Centros, Reuniones, Producción de derivados, Insumos) están completos y coinciden con lo documentado.
- El **login, bloqueo de cuenta y hashing de contraseñas** están implementados y persistidos correctamente.
- Sin embargo, hay **brechas funcionales concretas** que limitan qué tan usable es la app en un escenario real: no hay gestión de usuarios desde la UI, la sincronización con el backend Laravel solo cubre 1 de ~5 entidades, el segundo tramo de la ruta de acopio (Centro de Sector → Planta Principal) no tiene ninguna pantalla, y **prácticamente ninguna pantalla de lista tiene manejo de error/carga/vacío** (0 `CircularProgressIndicator` en toda la app).
- El **desktopApp es intencionalmente un stub** (una sola pantalla de demostración, documentado así) — no es una app de escritorio funcional, solo prueba que `shared` compila fuera de Android.
- **No hay control de versiones** (el directorio no es un repositorio git) — riesgo operativo importante independiente del código.

---

## 2. Stack y arquitectura

| Aspecto | Detalle |
|---|---|
| Lenguaje | Kotlin Multiplatform (Kotlin 2.4.10) |
| UI | Jetpack Compose (Android) + Compose Multiplatform (Desktop, stub) |
| Arquitectura | Clean Architecture + MVVM: `domain/model`, `domain/repository`, `data/sql` (SQLDelight), `presentation/<pantalla>` (ViewModel + UiState + StateFlow) |
| Persistencia local | SQLDelight / SQLite (`AcopioLecheDatabase`), un archivo `.sq` por entidad en `shared/src/commonMain/sqldelight/` |
| Red / sync | Ktor Client + kotlinx.serialization, offline-first con cola de pendientes y resolución de conflictos "el más reciente gana" |
| DI | Sin framework — `ServiceLocator` manual (`shared/.../di/ServiceLocator.kt`), documentado como decisión deliberada de esta fase |
| Backend | Laravel REST, **no está en este repositorio** — solo el contrato cliente (DTOs/mappers/Api) |
| Tests | kotlin.test en `commonTest` (modelo/servicio/ViewModel) y `desktopTest` (repos SQL reales) |
| Control de versiones | **Ninguno** — no es un repositorio git |

---

## 3. Cobertura de requisitos (según lo documentado en `docs/`)

| Requisito | Estado | Evidencia |
|---|---|---|
| RF-01 — Login con 5 roles + bloqueo tras 3 intentos | ✅ | `presentation/login/LoginViewModel.kt`, `domain/service/PoliticaBloqueoLogin.kt`, `data/sql/SqlUsuarioRepository.kt` |
| RNF-02 — Contraseñas no en texto plano | 🟡 | `domain/service/PasswordHasher.kt`: SHA-256 + sal, pero sin *stretching* (bcrypt/Argon2). El propio comentario del archivo lo reconoce como no apto para producción |
| RNF-03 — Persistencia offline que sobrevive reinicios | ✅ | SQLDelight en las 17 entidades vía `ServiceLocator.kt` |
| RF-02 — CRUD Proveedores | ✅ | `presentation/proveedor/ProveedoresViewModel.kt` + `data/sql/SqlProveedorRepository.kt`, baja lógica si tiene entregas |
| RF-12 — CRUD Centros de Acopio | ✅ | `presentation/centro/CentrosAcopioViewModel.kt` + `SqlCentroAcopioRepository.kt` |
| RF-17 — CRUD Reuniones | ✅ | `presentation/agenda/ReunionesViewModel.kt` + `SqlReunionRepository.kt` |
| RF-03 — Progreso de turno + ruta pendiente (panel acopiador) | ✅ | `presentation/dashboard/acopiador/AcopiadorHomeViewModel.kt` |
| RF-13 — Distribución por sectores + alertas (panel admin) | 🟡 | `presentation/dashboard/admin/PanelControlViewModel.kt` calcula distribución real, pero el mismo código (`EvaluadorCalidad.kt`) referencia "RF-13" para *adulteración* — numeración inconsistente entre comentarios, conviene aclarar con el interesado cuál es el RF-13 real |
| RF-26 / RN-20 — Registro, edición y cancelación de entrega con regla antifraude | ✅ | `presentation/entrega/RegistrarEntregaViewModel.kt`, `data/sql/SqlEntregaRepository.kt`, cubierto por `EntregaEstadoTest.kt` |
| RF-24/RF-25/RF-33, RN-17/18/19 — Ruta de acopio, tramo 1 (Proveedor→Centro Sector) | ✅ | `presentation/ruta/AsignarRutaViewModel.kt`, `ui/ruta/AsignarRutaScreen.kt`, `data/sql/SqlRutaRepository.kt` |
| RF-25 / RN-18 — Tramo 2 (Centro de Sector → Planta Principal, "traslado consolidado") | ❌ | Solo existe el enum `TipoCentroAcopio.PLANTA_PRINCIPAL` (`domain/model/TipoCentroAcopio.kt`); no hay ViewModel, pantalla ni repositorio para este tramo |
| RN-15 — Rotación de sectores por temporada de pastos | ❌ | No encontrado ningún mecanismo automático; la reasignación de `RutaAcopio` es 100% manual por el admin, sin lógica de "temporada" |
| RF-04/RF-29, RN-24/25/26 — Análisis de calidad en campo, OCR, firma | 🟡 | Ingreso manual y firma (`firmaProductorPresente`) implementados; OCR real **no existe** — botón etiquetado "📷 Simular escaneo de ticket (DEMO)" (`RegistrarAnalisisViewModel.simularEscaneoOCRTicket()`); rangos de `EvaluadorCalidad.kt` son valores de ejemplo sin confirmar (TODO explícito) |
| RF-08 — Historial completo de entregas por proveedor | 🟡 | El método existe (`EntregaRepository.observarEntregasDe`) y lo usa el propio productor y el cálculo de liquidaciones, pero **no hay pantalla para que Admin/Acopiador consulte el historial de un proveedor específico** desde `ProveedoresScreen` |
| RF-38/RF-35/RF-37, RN-28/29 — Cuenta y panel del Productor, aislamiento de datos | ✅ | `presentation/dashboard/productor/ProductorHomeViewModel.kt` filtra estrictamente por `proveedorId` |
| RF-27/RN-21, INT-11 — Panel de Pagos, registro manual, rol ENCARGADO_PAGOS | ✅ | `presentation/dashboard/pagos/PagosHomeViewModel.kt`, `ui/dashboard/pagos/PagosHomeScreen.kt` |
| RF-28/RN-14 — Precio por temporada | 🟡 | Modelo y `obtenerPrecioVigenteEn()` (ahora devuelve `PrecioVigente(precioPorLitro, esRespaldo)`, un único respaldo `CalculadoraLiquidacion.PRECIO_REFERENCIA_POR_LITRO`) completos y con test (`FakePrecioTemporadaRepositoryTest.kt`, `SqlPrecioTemporadaRepositoryTest.kt`). Pendiente: el % de reducción por sanción (RN-10, ligado a RN-14) es un valor **no confirmado con el cliente** y ya está codificado como si fuera definitivo (`CalculadoraLiquidacion.kt:18`); la bonificación por grasa alta (RN-14) sigue sin implementar, también no confirmada — no se implementa aquí. Los precios de temporada sembrados (S/1.60 Ene-Jun, S/1.90 Jul-Dic) son **datos de ejemplo no confirmados por el cliente** (`FakePrecioTemporadaRepository.kt`, `SqlPrecioTemporadaRepository.kt`, comentario en `seed()`); tampoco hay edición ni eliminación de precios de temporada por el Administrador — `PrecioTemporadaRepository` solo tiene `guardar` (alta), y `PagosHomeScreen.kt` solo permite crear uno nuevo y listarlos (líneas 114-119, 122, 282-306) |
| RN-10/11/12 — Motor de sanciones por adulteración | ✅ | `domain/service/MotorSanciones` (implícito por tests), muy bien cubierto por `MotorSancionesTest.kt` (5 casos, incluida precedencia RN-11 sobre RN-12) |
| RF-36 — Descuento por compra de queso | ❌ | Explícitamente omitido y documentado como "pendiente de confirmación en la matriz" (`docs/modelo-dominio.md`, sección 9) |
| RF-30/RF-31, INT-12 — CRUD Producción de derivados / Insumos lácteos | ✅ | `presentation/dashboard/lacteos/LacteosHomeViewModel.kt`, `SqlProduccionDerivadoRepository.kt`, `SqlInsumoRepository.kt` (crear/editar vía upsert `INSERT OR REPLACE`, eliminar directo) |
| Gestión de cuentas de usuario (crear/editar acopiadores, productores, etc.) | ❌ | `UsuarioRepository` **no tiene** `guardar`/`actualizar`/`eliminar`; las únicas 5 cuentas existen porque están *hardcodeadas* en `SqlUsuarioRepository.seed()`. No hay pantalla "Usuarios" en `androidApp/ui/` |
| Sincronización offline↔Laravel para Entrega/AnálisisCalidad/Ruta/Liquidación | ❌ | Documentado como "próximos pasos" en `docs/sincronizacion.md`; solo `Proveedor` tiene `*Api`/`*SyncManager`/`*Dto` reales (`ServiceLocator.kt:99-102`) |

---

## 4. Código incompleto / marcadores explícitos

| Ubicación | Hallazgo |
|---|---|
| `domain/service/CalculadoraLiquidacion.kt:14,18,31` | 2 TODOs: % de reducción por adulteración leve (RN-10) y bonificación por grasa alta (RN-14) **no confirmados**, pero el primero ya se aplica en runtime con valor `0.15` |
| `domain/model/SancionAplicada.kt:20` | TODO: monto exacto de multa de RN-10 no especificado por el cliente |
| `domain/service/EvaluadorCalidad.kt:22-26` | TODO: rangos de calidad (densidad, grasa, proteína, etc.) son valores de ejemplo, no confirmados con laboratorio |
| `presentation/analisis/RegistrarAnalisisViewModel.kt` (`simularEscaneoOCRTicket`) | OCR real no implementado, solo precarga de datos de ejemplo etiquetada "DEMO" |
| `presentation/ruta/AsignarRutaViewModel.kt:118` | Comentario propio advierte de un bug potencial: editar y guardar una ruta podría borrar el avance real del acopiador (paradas ya visitadas) — revisar lógica de merge al reasignar |
| ~~`data/sql/SqlEntregaRepository.kt:44-45` (`observarVolumenUltimaSemana`)~~ | ✅ **Resuelto** (rama `fix/grafico-ciclo-acopio`): calcula la suma real de `volumenLitros` agrupada por día vía `sumaVolumenPorFecha` (SQLDelight), ahora anclada al **ciclo de acopio jueves→miércoles** (`CicloSemanal.inicioDeSemana`, no la semana calendario lunes-domingo que usaba antes), con `0.0` en los días sin entregas. `observarVolumenDeSemana(hoy: LocalDate = ...)` acepta la fecha como parámetro inyectable para tests. `VolumenSemanalChart.kt` resalta el día real usando el nuevo `CicloSemanal.indiceEnCiclo()`. Cubierto por tests en `SqlEntregaRepositoryTest.kt` (jueves/miércoles/día intermedio del ciclo, exclusión del ciclo anterior, ambos bordes del ciclo, suma de dos entregas del mismo día) y `CicloSemanalTest.kt` (`indiceEnCiclo`). **Nota:** el gráfico muestra el ciclo EN CURSO; si hoy es jueves, solo hay un día con datos (el resto del ciclo todavía no ocurrió). Definir si se prefiere mostrar el ciclo anterior completo. |
| `data/sql/SqlEntregaRepository.kt` (`observarVolumenDeSemana`) / `presentation/dashboard/admin/PanelControlViewModel.kt` | **Decisión de negocio pendiente, no implementada todavía**: la suma no filtra por estado de la entrega. Se verificó que `EstadoEntrega.Rechazada` y `EstadoEntrega.Cancelada` conservan el `volumenLitros` con el que se registró originalmente (el `copy(estado = ...)` en `RegistrarAnalisisViewModel.kt` y `EntregasDelDiaViewModel.kt` no lo pone en 0), así que hoy el "volumen acopiado" del gráfico semanal y de la distribución por sectores de `PanelControlViewModel` **incluye leche que terminó rechazada o cancelada**. Falta confirmar con el interesado si el volumen acopiado/reportado debe excluir esos estados. |
| `domain/service/PasswordHasher.kt:14-18` | Comentario desactualizado: dice que el hash "vive solo en `FakeUsuarioRepository`", pero desde la Fase 2 se persiste en SQLDelight — documentación interna no actualizada |
| `data/fake/*` (17 archivos) | Repositorios completos en memoria, ya no usados en producción (confirmado en `ServiceLocator.kt`), solo quedan para tests — sin riesgo pero es código muerto en la app real que vale la pena mover explícitamente a `test/` o documentarlo mejor |

---

## 5. Calidad y robustez

**Validaciones:** existen en las pantallas de "formulario" (Login, RegistrarEntrega, RegistrarAnálisis, RegistrarConciliación, AsignarRuta — sus `UiState` tienen campo de error). El resto de formularios de creación (Proveedor, Centro, Reunión, Producción, Insumo) **no exponen validación de campos vacíos/negativos** en el `ViewModel`; si existe, está solo en el Composable de la pantalla (no verificado archivo por archivo, pero no hay `require`/`isBlank` a nivel de dominio para estos casos).

**Manejo de errores:** no se encontró **ningún** `try/catch` en los 23 ViewModels de `presentation/`. Cualquier excepción de SQLDelight (constraint, fila corrupta) se propaga sin control y probablemente termina en un crash de la corrutina, sin mensaje al usuario.

**Estados de UI (carga/error/vacío):** de 23 `UiState`, solo 5 (formularios) tienen campo de error; **ninguno** tiene campo de "cargando". Verificado con grep: **0 usos de `CircularProgressIndicator`** en toda `androidApp/ui/`, y solo **1 pantalla** (`RegistrarEntregaScreen.kt`) tiene texto explícito de estado vacío. Las listas (Proveedores, Centros, Reuniones, Notificaciones, Liquidaciones, Conciliación, Reportes, etc.) no comunican "cargando" ni "sin conexión / error al leer BD", solo listas vacías silenciosas.

**Seguridad:**
- Hashing de contraseñas: SHA-256 + sal por usuario, pero de una sola pasada (sin PBKDF2/bcrypt/Argon2) — débil frente a ataques offline si se filtra la BD; aceptable solo para esta fase exploratoria (ya reconocido en el propio código).
- **Credenciales de demo en texto plano dentro del código fuente** (`SqlUsuarioRepository.seed()`: `"Admin2026"`, `"Pagos2026"`, `"Lacteos2026"`, `"Productor2026"`, `"Acopio2026"`) — normal para semillas de desarrollo, pero deben eliminarse o rotarse antes de cualquier entrega/demo pública del código fuente.
- `AndroidManifest.xml`: `android:allowBackup="true"` sin restricciones — la base SQLite completa (incluyendo hashes de contraseña) es incluida en backups de Android (ADB backup / Auto Backup) por defecto.
- `NetworkConfig.baseUrl` apunta a `http://` (no HTTPS) por defecto; con `targetSdk = 37` (ver `gradle/libs.versions.toml`), **el tráfico en texto plano hacia cualquier host que no sea `10.0.2.2`/`localhost` está bloqueado por la política de cleartext de Android 9+** salvo que se agregue un `network_security_config.xml` — bloqueante para conectar a un servidor real sin HTTPS o sin este archivo.
- No hay control de permisos/roles a nivel de repositorio para la mayoría de entidades (solo `RN-29` para Productor está explícitamente aislado); la separación de roles depende enteramente de qué pantallas expone la navegación (`AcopioLecheNavHost.kt`), no de una verificación en la capa de datos — si un rol llegara a invocar un ViewModel que no le corresponde, no hay una segunda barrera.

**Tests:** buena cobertura en `domain/model` y `domain/service` (14 clases de test, incluye reglas de negocio complejas como `MotorSancionesTest`, `CalculadoraLiquidacionTest`, `PoliticaBloqueoLoginTest`). Cobertura débil en las otras capas:
- Solo **2 de 23 ViewModels** tienen test (`LoginViewModelTest`, `RegistrarAnalisisViewModelTest`).
- Solo **5 de 17 repositorios SQL** tienen test contra base de datos real (`SqlEntregaRepositoryTest`, `SqlRutaRepositoryTest`, `SqlUsuarioRepositoryTest`, `SqlPrecioTemporadaRepositoryTest`, `SqlLiquidacionRepositoryTest`, todos en `desktopTest`) — los otros 12 (Proveedor, CentroAcopio, Notificacion, Pago, ProduccionDerivado, Insumo, Sancion, CapacitacionCorrectiva, Asistencia, AnalisisCalidad, EquipoCampo, Reunion) solo se prueban indirectamente vía `Fake*Repository` (in-memory), que no valida el SQL real (mapeos de columnas, tipos, migraciones).
- **0 tests de UI de Compose** (solo `AutorizadorNavTest.kt`, que prueba navegación/routing, no renderizado).

---

## 6. Integración end-to-end

- **App móvil ↔ SQLite local**: coherente, nombres de campos y tipos verificados en varios repositorios (Entrega, ProduccionDerivado, Usuario) — sin discrepancias.
- **App móvil ↔ backend Laravel**: **el backend no existe en este repositorio**, por lo que solo se pudo auditar el lado cliente contra el contrato documentado en `docs/sincronizacion.md`. El contrato para `Proveedor` está completo y consistente (`data/remote/dto/ProveedorDto.kt`, `data/remote/mapper/ProveedorMapper.kt`, `data/remote/api/ProveedorApi.kt`). **No se puede verificar** que un backend Laravel real cumpla ese contrato porque no está disponible para pruebas.
- **Compilación real**: `./gradlew build` ejecutado durante esta auditoría → **BUILD SUCCESSFUL** (1m 41s), incluye `androidApp:assembleDebug`, `assembleRelease`, `desktopApp:build`, y toda la suite de tests (`shared:allTests`, `androidApp:test`) en verde. Única advertencia: un ícono de Compose deprecado (`Icons.Filled.List` en `AcopiadorBottomBar.kt:28`) y advertencias de compatibilidad de Gradle 10 (no bloqueantes).

---

## 7. Tabla de pendientes

| Módulo | Qué falta | Archivo(s) | Prioridad | Esfuerzo |
|---|---|---|---|---|
| Usuarios | No hay UI ni repositorio para crear/editar/desactivar cuentas; todo hardcodeado en `seed()` | `domain/repository/UsuarioRepository.kt`, `data/sql/SqlUsuarioRepository.kt`, falta `ui/usuarios/` | **Alta** | Alto (3-5 días: interfaz + repo + pantalla + validaciones) |
| ~~Reportes / Panel Admin~~ | ✅ **Resuelto** — volumen semanal ahora se calcula real desde SQLite (rama `fix/grafico-volumen`) | `data/sql/SqlEntregaRepository.kt` | — | — |
| Reportes / Panel Admin (decisión de negocio) | Definir si el volumen acopiado debe excluir entregas `Rechazada`/`Cancelada` (hoy se suman igual que las `Aceptada`) — afecta el gráfico semanal y `PanelControlViewModel` | `data/sql/SqlEntregaRepository.kt`, `presentation/dashboard/admin/PanelControlViewModel.kt` | **Media** | Bajo (una vez confirmado el criterio, es un filtro adicional en la query/agregación) |
| Rutas (tramo 2) | Sin pantalla ni lógica para el traslado Centro de Sector → Planta Principal | falta `presentation/ruta/` y `ui/ruta/` para este tramo | **Alta** | Alto (2-4 días) |
| Sincronización | Solo `Proveedor` sincroniza con Laravel; Entrega/AnálisisCalidad/Ruta/Liquidación no | `data/sync/`, `di/ServiceLocator.kt:95-103` | **Alta** | Alto (repetir el patrón del piloto por cada entidad, días por entidad) |
| Robustez UI | 🟡 **En progreso, 4 lotes planeados en `feature/uistate-plantilla`** — `UiState<T>` genérico (Cargando/Éxito/Vacío/Error) implementado en `presentation/core/UiState.kt`. ✅ Piloto: `Proveedores`. ✅ **Lote 1/4 migrado**: `CentrosAcopioViewModel`, `ReunionesViewModel` (`.catch` al final de toda la cadena, incluido el `flatMapLatest`, para atrapar también fallos de los flujos internos de asistencia), `AsistenciaViewModel` (mutadores `onToggleConvocado`/`onEscanearQr` adaptados para operar solo sobre `UiState.Exito`; ajustado además para que 0 convocados sea `Exito` con mensaje inline, no `Vacio`, porque la tarjeta de la reunión es información propia de la pantalla — ver regla general documentada en `presentation/core/UiState.kt`; se eliminó `actaCerrada`/`onCerrarActa` por ser estado local sin persistir y sin ningún lector en la UI — ver fila siguiente). ✅ **Lote 2/4 migrado**: `EntregasDelDiaViewModel`, `AnalisisCalidadListaViewModel`, `ConciliacionListaViewModel` (los tres se quedan en `Vacio` con lista vacía: sus totales/contadores son un recuento derivado de la misma lista, no datos propios independientes). ✅ **Lote 3/4 migrado (reducido)**: `NotificacionesViewModel` (queda en `Vacio` con lista vacía: `numeroNoLeidas` es recuento derivado de la misma lista). `SeleccionarProveedorViewModel` y `ColaEnvioViewModel` se excluyeron del lote por ser híbridos formulario+lista (ver fila de "UiState: 9 pantallas excluidas" y "UiState: rediseñar híbridos formulario+lista"). ✅ **Lote 4/4 migrado (revisión más cuidadosa)**: `ReportesViewModel` y `LacteosHomeViewModel` NO eran híbridos irreducibles (revisión anterior incorrecta) y se migraron: en `ReportesViewModel` el campo local `mensaje` se movió al `Screen` (`remember`, mismo patrón que Proveedores/Centros) porque no dependía de ningún repositorio; queda siempre en `Exito` (nunca `Vacio`) porque `reportes` es el catálogo estático de `ReportesCatalogo`, información propia de la pantalla, igual que la tarjeta de reunión en Asistencia. En `LacteosHomeViewModel`, `guardando`/`mensajeError` eran estado muerto (sin ningún lector en `LacteosHomeScreen.kt`, verificado por grep) y se eliminaron, y `mensajeNotificacion` se movió al `Screen`; queda `Vacio` solo si `producciones` E `insumos` están vacíos a la vez. `LiquidacionesViewModel` se migró en un commit propio, con un diseño distinto porque `cargar()` no es reactiva (es una carga imperativa invocada desde `init` y desde `onGenerarClick`, no un `combine()`): `try/catch` alrededor de `cargar()` que relanza `CancellationException` (nunca la absorbe) y en cualquier otra excepción registra el error con `AppLogger` — en la carga inicial pasa a `UiState.Error`, pero en `onGenerarClick` NO reemplaza `uiState` (para no perder la lista ya cargada), solo actualiza un `mensaje: StateFlow<String?>` separado; `generando` también vive en su propio `StateFlow` fuera de `UiState<T>`; `semanaInicio`/`fechaPago` son `val` simples (no dependen de ningún repositorio, se conocen de inmediato); el botón "Generar" vive fuera del `when` en el `Screen`, siempre visible y habilitado (solo se deshabilita mientras `generando`), incluido en `Vacio`. Con esto el plan de 4 lotes queda completo: piloto (Proveedores) + lote 1 (Centros/Reuniones/Asistencia) + lote 2 (EntregasDelDia/AnálisisCalidadLista/ConciliaciónLista) + lote 3 reducido (Notificaciones) + lote 4 (Reportes/LacteosHome/Liquidaciones) migrados. Quedan pendientes de un diseño propio (no del patrón mecánico): 3 híbridos formulario+lista (`AsignarRuta`, `SeleccionarProveedor`, `ColaEnvio`) + 4 dashboards + 4 formularios puros — ver filas siguientes | Todas las `*ViewModel.kt` de lista + sus `Screen.kt`, salvo `AsignarRutaViewModel.kt`, `SeleccionarProveedorViewModel.kt`, `ColaEnvioViewModel.kt` y los dashboards/formularios de la fila "9 pantallas excluidas" | **Alta** | — (completado) |
| Asistencia: cerrar acta no persiste ningún estado | El botón "Cerrar acta de asistencia" solo navega (`alVolver()`); no queda ningún registro de que el acta se cerró, ni en `UiState` (se eliminó `actaCerrada` por ser estado local sin persistir) ni en BD. Si el negocio requiere "acta cerrada" (p. ej. bloquear cambios de asistencia posteriores a esa reunión), hay que guardarla en BD vía repositorio, no en memoria | `presentation/agenda/AsistenciaViewModel.kt`, `data/sql/SqlAsistenciaRepository.kt` (o un campo nuevo en `Reunion`) | **Baja** hasta confirmar si el negocio lo requiere | Bajo-Medio (una vez definido dónde vive el dato) |
| UiState: estrategia de reintento tras Error | Hoy el operador `.catch` en el `Flow` combinado termina la recolección al capturar la excepción: si el usuario ve `UiState.Error`, no hay forma de "reintentar" sin recrear el ViewModel (recompilar/salir y volver a entrar a la pantalla). Falta definir un mecanismo explícito de reintento (relanzar la suscripción, botón "Reintentar", etc.) | `presentation/proveedor/ProveedoresViewModel.kt` (y cualquier pantalla que replique el patrón) | **Media** | Bajo-Medio (una vez se decida el mecanismo) |
| UiState: 9 pantallas excluidas de la replicación | No encajan en el patrón combine→lista y quedan fuera de los 4 lotes: **Dashboards por rol** — `PanelControlViewModel` (combine anidado de 5 repos en métricas heterogéneas), `ProductorHomeViewModel` (litros hoy/semana + 3 listas distintas en un solo estado), `PagosHomeViewModel` (combine de 5 flows con lógica de negocio pesada, liquidaciones en vivo), `AcopiadorHomeViewModel` (stats + ruta + últimas entregas + pendientes) — ninguno tiene una lista principal única, son paneles de métricas mixtas. **Híbrido formulario+lista** — `AsignarRutaViewModel`: el `combine` hace `_uiState.value.copy(...)` preservando campos de formulario (`nombreRuta`, `paradasOrdenadasProveedorIds`, `mensajeError`) entre emisiones; envolver esto en `UiState<T>` requiere rediseñar ese merge de estado primero, no es una migración directa. `SeleccionarProveedorViewModel` (mezcla `textoBusqueda` local con `proveedores` reactivo vía `_uiState.value.copy(...)`) y `ColaEnvioViewModel` (mezcla `enLinea`/`sincronizando`/`ultimoMensaje` locales con `outbox`/`proveedoresPendientes` reactivos, también vía `.copy(...)`) tienen el mismo problema — se excluyeron del lote 3 por el mismo motivo. **Formularios** — `LoginViewModel`, `RegistrarEntregaViewModel`, `RegistrarAnalisisViewModel`, `RegistrarConciliacionViewModel`: sin `combine()` de repositorios, el estado lo dirige el usuario (inputs), no una fuente de datos reactiva | `presentation/dashboard/admin/PanelControlViewModel.kt`, `presentation/dashboard/productor/ProductorHomeViewModel.kt`, `presentation/dashboard/pagos/PagosHomeViewModel.kt`, `presentation/dashboard/acopiador/AcopiadorHomeViewModel.kt`, `presentation/ruta/AsignarRutaViewModel.kt`, `presentation/entrega/SeleccionarProveedorViewModel.kt`, `presentation/cola/ColaEnvioViewModel.kt`, `presentation/login/LoginViewModel.kt`, `presentation/entrega/RegistrarEntregaViewModel.kt`, `presentation/analisis/RegistrarAnalisisViewModel.kt`, `presentation/conciliacion/RegistrarConciliacionViewModel.kt` | **Baja** (evaluar caso por caso, no es una réplica directa del patrón) | Alto (cada uno necesita diseño propio, no el patrón mecánico del piloto) |
| UiState: rediseñar híbridos formulario+lista | rediseñar híbridos formulario+lista (AsignarRuta, SeleccionarProveedor, ColaEnvio): estado local en `MutableStateFlow` propios combinados con los flujos del repo para producir `UiState<Payload>`; los mutadores no tocan `_uiState`. (`Reportes`, `LacteosHome` y `Liquidaciones` se evaluaron con esta misma sospecha y resultaron migrables sin este rediseño — ver fila "Robustez UI") | `presentation/ruta/AsignarRutaViewModel.kt`, `presentation/entrega/SeleccionarProveedorViewModel.kt`, `presentation/cola/ColaEnvioViewModel.kt` | **Media** | Medio (un diseño por ViewModel, no es el patrón mecánico del piloto) |
| Manejo de errores | 0 `try/catch` en 23 ViewModels; excepciones de BD no controladas | `presentation/**/*ViewModel.kt` | **Alta** | Medio |
| Seguridad | Hash sin *stretching*; backup habilitado; cleartext HTTP bloqueado por Android en release | `PasswordHasher.kt`, `AndroidManifest.xml`, `NetworkConfig.kt` | **Media** | Medio |
| Reglas de negocio no confirmadas | % de sanción RN-10 y bonificación RN-14 son valores inventados ya en producción | `CalculadoraLiquidacion.kt:14-18,31` | **Media** | Bajo (una vez el cliente confirme el valor) |
| Calidad de campo | Rangos de `EvaluadorCalidad` son de ejemplo; OCR es simulado | `EvaluadorCalidad.kt`, `RegistrarAnalisisViewModel.kt` | **Media** | Alto si se integra OCR real; bajo si solo se confirman rangos |
| Historial de proveedor | No hay pantalla para que Admin/Acopiador vea historial de un proveedor puntual | falta en `ui/proveedor/` | **Media** | Bajo-Medio |
| Rotación de sectores (RN-15) | No hay automatización por temporada, todo manual | `presentation/ruta/AsignarRutaViewModel.kt` | **Baja** | Medio |
| Tests de ViewModel/repos SQL | 21/23 ViewModels y 14/17 repos SQL sin test directo | `commonTest/`, `desktopTest/` | **Media** | Alto (cobertura amplia) |
| Tests de UI Compose | 0 tests de renderizado/interacción de pantallas | `androidApp/src/test` | **Baja** | Medio |
| Control de versiones | El proyecto no está bajo git | raíz del proyecto | **Alta** (proceso, no código) | Bajo (`git init` + primer commit) |
| Desktop app | Es un stub de una sola pantalla, documentado como fuera de alcance | `desktopApp/src/main/kotlin/.../Main.kt` | **Baja** (si el alcance real es solo Android) | Alto si se requiere paridad completa |
| RF-36 (descuento queso) | Pendiente de confirmación con el cliente, no implementado | — | **Baja** hasta confirmar | — |

---

## 8. Próximos 5 pasos recomendados

1. **Inicializar control de versiones** (`git init` + commit inicial) antes de seguir tocando el código — es el riesgo más fácil de eliminar y el más costoso si se materializa (pérdida de historial/trabajo).
2. ~~**Corregir el volumen semanal hardcodeado** en `SqlEntregaRepository.observarVolumenUltimaSemana()`~~ — ✅ resuelto en `fix/grafico-volumen`.
3. **Agregar estados de carga/error a los `UiState` y manejo de excepciones en los `ViewModel`** — es el hueco de robustez más transversal (afecta las ~18 pantallas de lista) y probablemente el que más se nota en una demo con datos reales o falta de conexión.
4. **Confirmar con el interesado (Municipalidad de Huata) los valores de negocio marcados como TODO**: % de reducción RN-10, bonificación por grasa RN-14, rangos de calidad de `EvaluadorCalidad`, y el RF-36 pendiente — son decisiones de negocio, no técnicas, y ya están corriendo en el sistema con valores supuestos.
5. **Definir el alcance real de la gestión de usuarios y del segundo tramo de ruta (Centro de Sector → Planta Principal)** antes de seguir invirtiendo en otras áreas — ambos son huecos funcionales grandes (no solo de robustez) que probablemente bloqueen un piloto real con usuarios administradores reales dando de alta cuentas.
