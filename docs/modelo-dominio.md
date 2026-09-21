# Modelo de dominio — AcopioLeche v2 (exploratoria)

Este documento registra las decisiones de modelado del proyecto `acopioleche-v2`, separando
con claridad qué viene confirmado por la Municipalidad de Huata (Parte A del encargo) de qué
es una extensión propuesta por el equipo, todavía sin validar con el interesado (Parte B).

## 1. Dominio confirmado (Parte A) — se reconstruyó tal cual, sin reinterpretar

Todas las entidades de esta sección están en
`shared/src/commonMain/kotlin/pe/edu/upeu/acopioleche/domain/model/`, un archivo por
concepto. Donde el encargo no especificaba un tipo Kotlin concreto, se documenta aquí la
decisión de implementación (no es una extensión de negocio, es un detalle técnico):

| Campo / concepto | Tipo elegido | Razón |
|---|---|---|
| `Entrega.turno` | `enum class Turno { MANANA, TARDE }` | El encargo pide el campo `turno` sin tipo; un enum evita valores libres inconsistentes ("mañana", "Mañana", "AM"...). |
| `Entrega.fecha`, `Reunion.fecha` | `kotlinx.datetime.LocalDate` | Fecha sin hora ni zona horaria: el dominio solo necesita el día calendario. |
| `EstadoEntrega.EnTransitoAPlanta.horaSalida`, `AnalisisCalidad.fecha` | `kotlinx.datetime.LocalDateTime` | Estos sí necesitan instante dentro del día. |
| `Proveedor.sector`, `Acopiador.vehiculo` | `String`, con constantes centralizadas | Ver `Sector.kt` y `Vehiculo.kt`: objetos con los 4 sectores y los 2 vehículos reales como constantes, para no "inventar" otros valores por accidente en el código (el tipo sigue siendo `String`, igual que `Acopiador.sectoresAsignados: List<String>`, que el encargo ya tipó explícitamente así). |
| `ResultadoAnalisis.FueraDeRango.rangoPermitido` | `ClosedRange<Double>` | Rango nativo de Kotlin, sin reinventar un par (min, max). |
| Rol de inicio de sesión (RF-01) | `enum class RolUsuario { ACOPIADOR, ADMINISTRADOR, ENCARGADO_PAGOS, PRODUCTOR_LACTEOS, PRODUCTOR }` | Cinco roles de inicio de sesión según la matriz de requerimientos actualizada. Es un enum **distinto** de `TipoActor`: `TipoActor` clasifica asistentes de una `Reunion` (proveedor/acopiador), un concepto de negocio no relacionado con quién inició sesión. |
| Entidad `Usuario` (RF-01) | `data class Usuario(id, nombreUsuario, contrasena, rol, nombreCompleto, centroAcopioId, ...)` | Invariantes validados con `require()`: `nombreUsuario` y hash de contraseña no vacíos, acopiador exige centro de acopio asignado, administrador prohíbe atado a un solo centro. |
| Bloqueo tras intentos fallidos (RF-01) | Contador por **cuenta** (`Usuario.intentosFallidos` + `ultimoIntentoFallidoEn`), no por dispositivo/IP | Al 3er intento fallido consecutivo se bloquea exactamente 10 minutos (`PoliticaBloqueoLogin`). |
| Hash de contraseña (RNF-02) | SHA-256 + sal aleatoria, implementado en Kotlin puro (`PasswordHasher`, sin `java.security` ni otra API de plataforma) | RNF-02 exige que las credenciales no se guarden en texto plano. Kotlin puro compila igual en Android y Desktop/JVM desde `commonMain`. |

Sealed interfaces (no sealed class, por convención del proyecto):

- **`EstadoEntrega`**: `Pendiente`, `Aceptada`, `Rechazada(motivo)`, `EnTransitoAPlanta(transportistaId, horaSalida)`, `Liquidada(liquidacionId)`.
- **`ResultadoAnalisis`**: `Normal(densidad, acidez, grasa, proteina, lactosa, temperatura, ph)`, `FueraDeRango(motivo, valorMedido, rangoPermitido)`, `Adulterada(indicio, porcentajeAgua)`.

Enums simples: `TipoActor`, `MotivoRechazo`, `TipoNotificacion`, `TipoEvento` — reconstruidos
con los valores exactos pedidos, sin agregar ni quitar ninguno.

Entidades de datos (`data class`): `Proveedor`, `Acopiador`, `CentroAcopio`, `Entrega`,
`AnalisisCalidad`, `Notificacion`, `Reunion`, `Asistencia`, `Usuario`.

## 2. Extensiones y enriquecimiento de UI respaldados por Requerimientos

| Extensión / Componente | Dónde | Detalle / Respaldo |
|---|---|---|
| Logo institucional | `androidApp/.../res/drawable/logo_ecolacteos.png` (`docs/logo.png`) | Logo de EcoLácteos Huata como imagen estática (PNG), no como composable dibujado en Canvas. |
| `PerfilScreen` | `androidApp/.../ui/perfil/PerfilScreen.kt` | Pantalla de perfil accesible desde la barra superior. Muestra datos del usuario logueado y el botón para cerrar sesión. |
| Eliminación de `RoleSwitcherStrip` | `androidApp/.../ui/nav/` | Eliminado completamente. La navegación y barras inferiores responden exclusivamente al rol de la `SesionActiva`. |
| Progreso de Turno + Ruta Pendiente | `AcopiadorHomeScreen` / `ViewModel` | RF-03: Muestra litros y entregas de hoy, acceso directo a nueva entrega y lista de proveedores de la ruta que aún no entregan hoy. |
| Distribución por Sectores + Alertas | `PanelControlScreen` / `ViewModel` | RF-13: Distribución del volumen de hoy por los 4 sectores oficiales (Norte, Sur, Este, Oeste), estado de atención de centros y alertas de calidad de hoy. |

## 3. Decisiones de arquitectura y Persistencia (Fase 2 — SQLDelight)

- **Clean Architecture + MVVM**: `domain/model` (entidades), `domain/repository` (interfaces), `data/sql` (implementaciones reales con SQLDelight), `presentation/<pantalla>` (ViewModel + UiState por pantalla con `StateFlow`).
- **Persistencia Local con SQLDelight (Fase 2, RNF-03)**:
  - Base de datos relacional SQLite gestionada a través de SQLDelight (`AcopioLecheDatabase`).
  - Implementaciones concretas de repositorio en `data/sql/` (`SqlUsuarioRepository`, `SqlEntregaRepository`, `SqlProveedorRepository`, `SqlCentroAcopioRepository`, etc.).
  - Las credenciales de usuario, contadores de intentos fallidos y bloqueos de seguridad se persisten en la tabla `UsuarioEntity`, sobreviviendo a cierres y reinicios de la aplicación.
  - Inyección mediante `ServiceLocator.init(...)` con `AndroidDatabaseDriverFactory` en Android e `DesktopDatabaseDriverFactory` en Desktop.
- **Autenticación real (RF-01, RNF-02, RN-07)**: La app arranca en `LoginScreen`. Al ingresar usuario y contraseña correctos, la sesión activa queda registrada en `SesionActivaHolder` y direcciona al dashboard correspondiente al rol.
- **Sin selector de rol manual en Login**: El rol se deriva de la cuenta autenticada.
- **Navegación filtrada por rol**: El Acopiador sólo accede a Inicio, Entregas del Día y Cola de Envíos. El Administrador accede a Panel de Control, Proveedores, Centros, Reuniones, Reportes y Calidad.
- **Cerrar Sesión**: Ubicado exclusivamente en `PerfilScreen`.

## 4. Cambios y Ajustes de la Fase A

- **Turno automático**: Se eliminó el selector manual de turno de la UI. El turno se deduce automáticamente según la hora del dispositivo (`Turno.deducirDeHora(hora)`: `< 12:00` = `MANANA`, `>= 12:00` = `TARDE`) y se muestra como dato informativo no editable en el registro y vistas de resumen.
- **Teclado numérico (RNF-06)**: Configurado `KeyboardType.Decimal` en los campos de ingreso de litros y campos numéricos para facilitar la digitación con una sola mano en campo.
- **Sidebar del administrador (RNF-07)**: La barra inferior del Administrador fue reemplazada por un menú lateral desplegable (`ModalNavigationDrawer`). La aplicación abre en la pantalla de Inicio/Panel con el menú cerrado por defecto, ofreciendo un botón de hamburguesa en el `AppTopBar` de las pantallas administrativas. El Acopiador mantiene su barra de navegación inferior.
- **Visualización de contraseña en Login**: Se agregó botón de ojo en `LoginScreen` (`IconButton` con icono de visibilidad) para alternar ver u ocultar el texto de la contraseña.
- **Actualización de Roles de Usuario**: Se amplió `RolUsuario` a los 5 roles solicitados (`ACOPIADOR`, `ADMINISTRADOR`, `ENCARGADO_PAGOS`, `PRODUCTOR_LACTEOS`, `PRODUCTOR`), asegurando cobertura exhaustiva en todas las ramas `when` para prevenir `NoWhenBranchMatchedException`.
- **CRUDs completos (Proveedores RF-02, Centros de Acopio RF-12, Reuniones RF-17)**:
  - Implementación completa de métodos de creación, edición y eliminación en interfaces de repositorio y persistencia SQLDelight (`SqlProveedorRepository`, `SqlCentroAcopioRepository`, `SqlReunionRepository`).
  - **Manejo de registros dependientes**: Para proveedores y centros de acopio con entregas o datos históricos asociados, se desactiva/retira el registro (`activo = false`) en lugar de eliminarlo físicamente, preservando la integridad referencial e historial sin borrados en cascada.

## 5. Cambios de la Fase B — Registro de entrega y resultado de visita (RF-26, RN-20)

- **Flujo simplificado de registro de entrega**: Por defecto, la pantalla solo muestra el ingreso directo de litros recogidos (`KeyboardType.Decimal`), botones rápidos (`+5`, `+10`, `+20`) y porongos.
- **Diferenciación entre NoRecogida y Rechazada**:
  - `EstadoEntrega.Rechazada(motivo)`: Leche entregada por el proveedor pero rechazada en análisis de calidad (acidez, temperatura, adulteración).
  - `EstadoEntrega.NoRecogida(motivo)`: El acopiador visitó al proveedor pero no había leche / sin producción (volumen 0.0 L).
- **Opción secundaria No Recogido**: Se agregó en el formulario la opción secundaria *"Marcar como No Recogido (Visita sin acopio)"* permitiendo indicar el motivo (ej. *"No había leche / Sin producción"*).
- **Eliminación de 'Visita en proceso'**: Eliminada por completo de la aplicación al no corresponder a un estado operativo real.
- **Edición y Cancelación de Entregas (`EstadoEntrega.Cancelada`)**:
  - Se permite **editar** (corregir litros/porongos) de una entrega en estado `Pendiente`.
  - Se permite **cancelar** un registro (`EstadoEntrega.Cancelada(motivo, canceladaPor, fechaHora)`), registrando el `usuarioId` del usuario autenticado en la sesión para trazabilidad.
  - **Regla Antifraude Estricta (RN-20)**: Tanto la edición como la cancelación están **estrictamente bloqueadas** si la entrega ya cuenta con un `AnalisisCalidad` registrado, o si está `EnTransitoAPlanta` o `Liquidada`. Esto evita la eliminación o alteración de evidencias de adulteración.

## 6. Cambios de la Fase C — Rutas y dos tramos de acopio (RF-24, RF-25, RF-33, RN-17/18/19)

- **Dos tramos de acopio**:
  $$\text{Proveedor} \xrightarrow{\text{Ruta acopiador}} \text{Centro de Sector} \xrightarrow{\text{Traslado consolidado}} \text{Planta Principal}$$
- **Tipo de Centro de Acopio**: Se agregó `tipoCentro: TipoCentroAcopio` (`CENTRO_SECTOR` vs `PLANTA_PRINCIPAL`) en `CentroAcopio`. Los tres centros de sector son Huata Centro (CA-001), Coyme (CA-002) y Pallalla (CA-003). La planta principal es el destino final de la leche acumulada.
- **Entidades de Ruta y Paradas Ordenadas**:
  - `RutaAcopio(id, nombre, acopiadorId, centroSectorId, fecha, paradas, estado)`: Modela la ruta asignada a un acopiador para una jornada específica (RN-19).
  - `ParadaRuta(orden, proveedorId, estadoParada)`: Define el orden explícito de visita a los productores.
  - **Relación con `sectoresAsignados`**: `RutaAcopio` representa la asignación diaria dinámica por el Administrador (cubriendo la rotación por temporada de pastos RN-15), mientras `sectoresAsignados` en `Acopiador` se mantiene como el padrón de sectores autorizados.
- **Visualización en Panel del Acopiador (RF-33)**: La ruta asignada del día se muestra en el panel del acopiador con sus paradas en orden, permitiendo consultar los productores asignados y su avance.
- **Cierre de Ruta y Descarga Consolidada (RF-25, RN-18)**: Se realiza UNA SOLA descarga con el volumen total acumulado en el centro de sector al terminar el recorrido ("Cierre de ruta"), cambiando el estado de la ruta a `FINALIZADA`.
- **Terminología Ajustada**:
  - Sincronización offline (RNF-03) $\rightarrow$ *"Pendientes de envío"*
  - Viaje de descarga (RF-25, RN-18) $\rightarrow$ *"Cierre de ruta"*

## 7. Cambios de la Fase D — Análisis de calidad en campo (RF-04, RF-29, RN-24, RN-25, RN-26)

- **Análisis en Campo (RN-25)**: El análisis de calidad se realiza durante la recepción en campo mediante un equipo portátil LactoScan.
- **Criterio de Selección de Muestras (RN-24)**: Se modeló `criterioSeleccion: CriterioAnalisis` (`PROGRAMADO`, `ALEATORIO`, `HISTORIAL_ALERTA`, `MANUAL`) puesto que no todas las entregas requieren análisis obligatorio, priorizando productores con alertas previas.
- **Captura por OCR e Ingreso Manual (RF-29)**: Se etiquetó la opción en la UI como *"📷 Simular escaneo de ticket (DEMO)"*. La integración con un motor OCR real está pendiente y actualmente utiliza precarga de ticket impreso de ejemplo. El ingreso manual de los 7 parámetros permanece siempre visible y editable debajo.
- **Verificación de Firma del Productor (RN-26)**: Registrado la conformidad del productor mediante `firmaProductorPresente: Boolean` para el comprobante físico/digital firmado en campo.

## 8. Cambios de la Fase E — Cuenta del productor (RF-38, RF-35, RF-37, RN-28, RN-29)

- **Rol y Login del Productor (RF-38)**: Habilitadas cuentas de usuario para el rol `PRODUCTOR` (ejemplo: usuario `rquispe` para Rosa Quispe Mamani, vinculado a `proveedorId = P-014`).
- **Panel del Productor (`ProductorHomeScreen` / `ProductorHomeViewModel`)**:
  - Consulta de volumen entregado hoy y acumulado semanal (RF-37).
  - Consulta de resultados de análisis de calidad de su leche.
  - Consulta de liquidaciones y pagos semanales (RF-35).
- **Aislamiento Estricto de Datos en Capa de Repositorio (RN-29)**: Se garantiza que un productor acceda ÚNICAMENTE a sus propios registros filtrando por `proveedorId` directamente en las funciones de la capa de datos (`entregaRepository.observarEntregasDe`, `liquidacionRepository.observarLiquidacionesDe`), previniendo fugas de privacidad entre cuentas de productores.

## 9. Cambios de la Fase F — Pagos y precios (RF-27, RF-28, RN-21, RN-22, INT-11)

- **Rol ENCARGADO_PAGOS (INT-11)**: Habilitada la cuenta de Finanzas/Contabilidad (`pagos` / `Pagos2026`).
- **Control de Liquidaciones y Registro de Pago Manual (RF-27, RN-21)**: Panel de control exclusivo (`PagosHomeScreen` / `PagosHomeViewModel`) para consultar liquidaciones semanales y registrar la entrega presencial del pago en efectivo o depósito, indicando `encargadoId`, fecha/hora y notas de constancia.
- **Precio por Temporada (RF-28, RN-14)**:
  - Modela `PrecioTemporada(id, nombreTemporada, fechaInicio, fechaFin, precioPorLitro)`.
  - El cálculo de pago de una entrega utiliza el **precio vigente EN LA FECHA DE ESA ENTREGA** (`obtenerPrecioVigenteEn(fecha)`).
  - **Regla de Resguardo**: Si una fecha no cuenta con tarifa por temporada configurada, el repositorio recurre al **precio base del sistema** (`1.80 S/L`). Si coinciden rangos solapados, prevalece la tarifa con la `fechaInicio` más reciente.
  - **Nota de Regla de Negocio**: TODO explícito en `CalculadoraLiquidacion`: *"Bonificación por grasa alta NO CONFIRMADA con el interesado. El precio base solo se ajusta a la baja mediante sanciones aplicadas (RN-10/RN-11), nunca al alza."*
- **Omitido RF-36**: El descuento por compra de queso se mantiene pendiente de confirmación en la matriz de requerimientos.

## 10. Cambios de la Fase G — Productor de lácteos (RF-30, RF-31, INT-12)

- **Rol y Login del Productor de Lácteos**: Habilitada la cuenta de transformación (`lacteos` / `Lacteos2026`). Redirección a su panel exclusivo (`LacteosHomeScreen` / `LacteosHomeViewModel`).
- **CRUD de Producción de Derivados (RF-30)**:
  - Modela `ProduccionDerivado(id, tipoProducto, codigoLote, cantidadUnidades, fechaProduccion, responsableId)`.
  - Permite crear, editar y eliminar lotes producidos (ej. Queso Paria, Yogurt Bebible, Mantequilla) con sus tablas relacionales en SQLDelight (`ProduccionDerivadoEntity`).
- **CRUD de Registro de Insumos Lácteos (RF-31)**:
  - Modela `InsumoLacteo(id, nombreInsumo, cantidad, unidadMedida, fechaIngreso)`.
  - Permite crear, editar y eliminar insumos para el procesamiento (ej. Cuajo líquido, Sal, Cultivos lácticos) en `InsumoLacteoEntity`.
- **Alcance Mínimo Respetado**: NO se implementó control de stock automatizado, recetas de conversión, costeo ni trazabilidad lote-a-lote, respondiendo al alcance mínimo solicitado para esta etapa exploratoria.
