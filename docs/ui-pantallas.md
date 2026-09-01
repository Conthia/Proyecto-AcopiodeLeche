# Pantallas del MVP — Sesión 3 (Compose)

Documento de QA: relaciona cada pantalla implementada con el requerimiento de la Matriz de
Trazabilidad que cubre, y deja constancia de un hallazgo corregido durante la revisión.

## Mapeo pantalla → requerimiento

| Pantalla | Archivo | Requerimiento(s) |
|---|---|---|
| Login | `login/LoginScreen.kt` | RF-01 (autenticar usuario), RNF-02 (protección de credenciales) |
| Dashboard Acopiador | `dashboard/acopiador/DashboardAcopiadorScreen.kt` | Punto de entrada del rol Acopiador, sin RF propio — agrupa accesos a RF-03, RF-17 |
| Dashboard Técnico | `dashboard/tecnico/DashboardTecnicoScreen.kt` | Punto de entrada del rol Técnico, sin RF propio — agrupa accesos a RF-04, RF-13 |
| Dashboard Administrador | `dashboard/admin/DashboardAdminScreen.kt` | Punto de entrada del rol Administrador, sin RF propio — agrupa accesos a RF-02, RF-12, RF-23 |
| Nuevo Proveedor | `proveedor/NuevoProveedorScreen.kt` | RF-02 (registrar productor) |
| Nuevo Acopiador | `acopiador/NuevoAcopiadorScreen.kt` | **RF-23** (registrar acopiador) — *ver hallazgo abajo* |
| Registrar Entrega | `entrega/RegistrarEntregaScreen.kt` | RF-03 (registrar recepción de leche) |
| Agenda | `agenda/AgendaScreen.kt` | RF-17 (agendar reuniones y capacitaciones) |
| Control de Asistencia | `asistencia/ControlAsistenciaScreen.kt` | RF-18 (registrar asistencia a capacitaciones) |
| Nueva Visita Técnica | `visita/NuevaVisitaScreen.kt` | RF-04 (registrar prueba de calidad) — extendida con observación de sanidad animal para INT-10 (veterinario articulado) |

## Hallazgo de esta revisión

**"Nuevo Acopiador" no tenía requerimiento asociado.** La pantalla ya estaba programada
(commit `af0c127`), pero la Matriz de Requerimientos solo tenía RF-02 para registrar
*productores*, no un equivalente para *acopiadores* — a pesar de que el modelo de dominio
(sesión S2) ya los trata como entidades separadas por decisión explícita del equipo.

**Corrección aplicada:** se agregó **RF-23 — Registrar acopiador** (Must) y su caso de prueba
**CP-22** a la matriz, antes de este commit de documentación. La trazabilidad queda completa:
0 requerimientos huérfanos, 0 pantallas sin respaldo.

## Estado de las pantallas del MVP

Las 10 pantallas listadas arriba cubren el alcance que el interesado confirmó como prototipo
inicial (Módulo 1: proveedores, acopiadores, registro de entrega; Módulo 2: agenda con control
de asistencia), más 2 pantallas del rol Técnico (prueba de calidad, visita técnica) que
adelantan alcance de fase 2 pero ya estaban resueltas en el prototipo de Figma.

**Pendiente para próximas sesiones** (no cubierto aún por ninguna pantalla):
- RF-06/RF-07: cálculo de pago y liquidación semanal — sin pantalla todavía.
- RF-19/RF-20: sanción escalonada por adulteración y capacitación correctiva por acidez.
- RF-21: conciliación campo-planta.
- RF-08/RF-09: historial y reportes.
- RF-11/RF-14/RF-15/RF-16: pantalla de notificaciones (el sistema ya modela `Notificacion`
  en el dominio, falta la UI).