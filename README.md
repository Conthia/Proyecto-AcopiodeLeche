# AcopioLeche
> Registra el acopio diario de leche por productor y calcula su pago según calidad, incluso sin conexión — en alianza con la Municipalidad de Huata.

## Problema que resuelve
En la cuenca lechera de Huata (Puno) el acopio se registra tradicionalmente en cuadernos y por lote mezclado de varios productores, sin diferenciar la calidad individual de cada uno. Esto genera errores en el cálculo del pago, pérdida de historial y demoras en la conciliación, agravado por zonas sin cobertura de internet estable. El proyecto nace del programa de Proyección Social 2026-I, en alianza con la Municipalidad de Huata (~500 productores ganaderos).

## Público objetivo
- **Proveedores**: entregan su leche directamente en planta.
- **Acopiadores**: recorren sectores rurales recogiendo leche de varios proveedores (camionetas/motocargas), en zonas de conectividad limitada.

## Funcionalidades previstas
- Registrar la recepción diaria de leche por proveedor (volumen, turno, centro de acopio).
- Registrar el análisis de calidad (densidad, acidez, grasa) y detectar adulteración.
- Calcular el pago según volumen, grasa y calidad higiénica; generar liquidación quincenal.
- Notificar al proveedor (resumen de entrega, alerta de adulteración, resultado de densidad, citaciones) con sonido distintivo.
- Agendar reuniones/capacitaciones y registrar asistencia.
- Iniciar sesión (acopiador, técnico, administrador).
- Trabajar sin conexión en campo y sincronizar al recuperar señal.

## Entidad principal del CRUDA
**Entrega**: id, proveedor, acopiador (opcional), centro_acopio, fecha, turno, volumen_litros, estado (pendiente/aceptada/rechazada/en_transito/liquidada).
> Modelo completo (8 entidades, 4 enums, 2 sealed interface) documentado en [docs/modelo-dominio.md](./docs/modelo-dominio.md).

## Capacidad nativa prevista
- **Notificación** — ya contemplada en el modelo de dominio (`Notificacion.kt`).
- **Cámara** (foto del envase) — pendiente.
- **QR** (identificar productor) — pendiente.
- **GPS** (ubicación del acopio) — pendiente.

## Equipo
| Integrante                | Código       | Rol semana 1       |
|---------------------------|--------------|--------------------|
| Alicia Vizcarra Ramos     | 202411765    | Coordinación       |
| Dayron Apaza Rodriguez    | 202413201    | Lógica y datos     |
| Jorge Luis Riveros Larico | 202411740    | UI                 |
| Deysi Yaneth Mamani Jinez | 202410812    | QA y documentación |

## Tecnologías
Kotlin Multiplatform · targets Android y Desktop (JVM) · paquete `pe.edu.upeu.acopioleche` · Clean Architecture (dominio) + MVVM
> ⚠️ iOS estaba previsto en el plan original ("requiere macOS para compilar") pero fue removido del proyecto en la sesión de hoy, siguiendo la guía del curso que lo trata como solo conceptual. **Pendiente de confirmar con el equipo si esto se mantiene así.**

## Ejecutar la app
- Android: `./gradlew :androidApp:assembleDebug`
- Desktop: `./gradlew :desktopApp:run`

## Correr los tests
- Todos (common + jvm): `./gradlew allTests`
- Solo Desktop (feedback más rápido): `./gradlew :shared:desktopTest`
- Solo Android: `./gradlew :shared:testAndroidHostTest`

---
Más sobre [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).