# AcopioLeche
> Registra el acopio diario de leche por productor y calcula su pago según calidad, incluso sin conexión.

## Problema que resuelve
En las cuencas lecheras de Puno (Taraco, Mañazo, Ayaviri) el acopio se registra tradicionalmente en papel y por lote mezclado de varios productores, sin diferenciar la calidad individual de cada uno. Esto genera errores en el cálculo del pago, pérdida de historial y demoras en la conciliación, especialmente en zonas rurales sin cobertura de internet.

## Público objetivo
Acopiadores independientes o de pequeñas plantas/asociaciones lecheras que recorren varios productores en zonas con conectividad limitada.

## Funcionalidades previstas
- Registrar el acopio diario por productor (litros, indicador de calidad, fecha/hora).
- Consultar historial de acopios y pagos pendientes por productor.
- Iniciar sesión (acopiador).
- Trabajar sin conexión en campo y sincronizar al recuperar señal.

## Entidad principal del CRUDA
**Acopio**: fecha_hora, productor, litros, indicador_calidad, precio_litro, monto_total, estado_pago (pendiente/pagado), sincronizado.

## Capacidad nativa prevista
Cámara (foto del envase) / QR (identificar productor) / GPS (ubicación del acopio) / Notificación (recordatorio)]

## Equipo [nombre del equipo]
| Integrante                | Código       | Rol semana 1       |
|---------------------------|--------------|--------------------|
| Alicia Vizcarra Ramos     | 202411765    | Coordinación       |
| Dayron Apaza Rodriguez    | 202413201    | Lógica y datos     |
| Jorge Luis Riveros Larico | 202411740    | UI                 |
| Deysi Yaneth Mamani Jinez | 202410812    | QA y documentación |

## Tecnologías
Kotlin Multiplatform · Compose Multiplatform · targets Android y Desktop
(iOS preparado: requiere macOS para compilar)
