# Sincronización offline-first con el backend Laravel

Fase 1 de conexión a un backend REST en Laravel, manteniendo el modo 100% offline que ya existe
(SQLite local vía SQLDelight). Piloto: **Proveedor** — CRUD completo, sin máquina de estados.

## Arquitectura

```
presentation/*ViewModel  ──────────────────────────────────────────────┐
        │  (guardar/actualizar/eliminar: sin cambios de firma)         │
        ▼                                                              │
domain/repository/ProveedorRepository  ← interfaz, ahora con métodos   │
        │                                de sincronización             │
        ▼                                                              │
data/sql/SqlProveedorRepository        ← SQLite es la única fuente de  │
        │   verdad local; marca cada cambio con synced/pendingAction   │
        │                                                              │
        ▼                                                              ▼
data/sync/ProveedorSyncManager  ────────────►  data/remote/api/ProveedorApi
   (push pendientes, pull remoto,                  (Ktor + kotlinx.serialization)
    "el más reciente gana" en conflictos)
        ▲
        │ dispara
data/sync/SyncCoordinator  ◄──── botón "Sincronizar ahora" (ColaEnvioViewModel)
        ▲
        │ dispara al detectar conexión
data/sync/AutoSyncController ◄── data/sync/ConnectivityObserver
                                  (AndroidConnectivityObserver / DesktopConnectivityObserver)
```

- **El dominio no cambia**: `Proveedor` no tiene campos de sincronización. Esos metadatos
  (`synced`, `pendingAction`, `updatedAt`, `deleted`) viven solo en `ProveedorEntity` (SQLDelight)
  y en los DTOs de red — igual que ya pasaba con `sincronizada` en `EntregaEntity`.
- **`AccionPendiente`** (`domain/sync/CambioPendiente.kt`) es `CREAR | ACTUALIZAR | ELIMINAR`.
  La UI actual (`ProveedoresViewModel.guardarProveedor`) siempre llama a `repository.guardar(...)`
  tanto para alta como para edición; el repositorio decide la acción real mirando si la fila ya
  existe y si ya viajó alguna vez al servidor.
- **Baja offline**: si el registro nunca se sincronizó (`pendingAction == CREAR`), eliminar lo
  borra de una vez (el servidor nunca lo conoció). Si ya estaba sincronizado, se marca como
  "tumba" local (`deleted = 1`, `pendingAction = ELIMINAR`) hasta que el servidor confirme el
  `DELETE`; recién ahí se borra la fila de verdad.
- **Conflictos**: last-write-wins por `updatedAt`. Al aplicar un cambio remoto, si el registro
  local tiene ediciones sin enviar, gana quien tenga el timestamp más reciente. Si gana el local,
  no se hace nada: se subirá tal cual en el próximo push.
- **Conectividad**: `ConnectivityObserver` detecta "hay señal en la zona" (Android: callback real
  de `ConnectivityManager`; Desktop: polling de interfaces de red cada 5s), no "el backend
  responde". Un fallo real de red al sincronizar (timeout, backend caído, `ConnectException`) se
  captura en `ProveedorSyncManager` y no rompe la app: lo que ya se envió queda confirmado, el
  resto sigue pendiente para el próximo intento.
- **Autenticación futura**: `AuthTokenProvider.token` (en `data/remote/NetworkConfig.kt`) es el
  único punto que hay que tocar para inyectar un token — `HttpClientFactory` ya lo agrega a cada
  request vía `defaultRequest { }`. Hoy queda `null` porque el login sigue siendo 100% local.

## Cómo probar manualmente

1. **Modo offline puro** (sin tocar el emulador de red):
   - Registrar/editar/eliminar un proveedor en `ProveedoresScreen`. La fila aparece con la
     etiqueta "Pendiente de envío" (`ProveedorRow`).
   - Cerrar y volver a abrir la app: el proveedor sigue ahí (SQLite local), y sigue marcado como
     pendiente — nada se perdió ni se envió.
2. **Sincronización manual**: entrar a "Cola de envío" (`ColaEnvioScreen`) y tocar
   "Sincronizar ahora". Sin backend Laravel corriendo, el mensaje debe decir algo como
   "Sin conexión con el servidor todavía (...)" y el proveedor sigue pendiente (no se pierde).
   Con el backend levantado y `NetworkConfig.baseUrl` apuntando a él, el mensaje pasa a
   "N registro(s) enviados al servidor" y la etiqueta "Pendiente de envío" desaparece.
3. **Sincronización automática**: con el backend levantado, apagar el Wi-Fi/datos del
   emulador/dispositivo, registrar un proveedor (queda pendiente), y volver a activar la
   conexión. `AutoSyncController` debería disparar la sincronización solo, sin tocar el botón.
   En Desktop, como no hay callback de sistema, el chequeo es cada 5s (constante
   `DesktopConnectivityObserver.intervaloMs`), así que puede tardar hasta 5s en notarlo.
4. **Conflicto**: editar el mismo proveedor desde dos "clientes" (por ejemplo, con Postman contra
   el backend y en la app, ambos offline entre sí) y sincronizar ambos; debe quedar la versión
   con `actualizado_en` más reciente.

## Contrato REST esperado — `Proveedor` (para replicar en Laravel)

Base: `NetworkConfig.baseUrl` (por defecto `http://10.0.2.2:8000/api`, alias del emulador Android
hacia el `localhost` del host).

Convención: JSON en `snake_case`, fechas `actualizado_en` en formato ISO-8601 local
(`yyyy-MM-ddTHH:mm:ss`, sin offset — mismo formato que `kotlinx.datetime.LocalDateTime.toString()`).

### `GET /proveedores`
Lista completa (el piloto no pagina ni filtra por fecha; ver nota de "próximos pasos").

Respuesta `200`:
```json
[
  {
    "id": "P-014",
    "nombre": "Rosa Quispe Mamani",
    "documento": "41028573",
    "telefono": "951034221",
    "sector": "Sector Norte",
    "entrega_directa_en_planta": false,
    "numero_vacas": 9,
    "calificacion": "A",
    "activo": true,
    "actualizado_en": "2026-09-10T08:30:00",
    "eliminado": false
  }
]
```
`eliminado: true` le dice al cliente que borre su copia local (baja hecha desde otro dispositivo
o directamente en el backend).

### `POST /proveedores`
Body = un objeto `ProveedorDto` (mismo shape de arriba, sin `eliminado` relevante).
Respuesta `201` con el objeto creado — el cliente usa el `actualizado_en` de la respuesta como
fuente de verdad (no el que mandó), por si el servidor ajusta el reloj.

### `PUT /proveedores/{id}`
Body = `ProveedorDto` completo. Respuesta `200` con el objeto actualizado (mismo motivo que
arriba: el cliente confía en el `actualizado_en` de la respuesta).

### `DELETE /proveedores/{id}`
Sin body. Respuesta `200`/`204`. El backend debe hacer *soft delete* (no borrar la fila), para
poder seguir devolviéndola con `eliminado: true` en el próximo `GET /proveedores` a cualquier
otro cliente que todavía no se enteró de la baja.

### Campos y tipos

| Campo (JSON)                    | Tipo    | Dominio (`Proveedor`)     | Notas |
|----------------------------------|---------|---------------------------|-------|
| `id`                             | string  | `id`                      | Generado por el cliente (`P-####`); el backend lo respeta, no autogenera. |
| `nombre`                         | string  | `nombre`                  | |
| `documento`                      | string  | `documento`               | DNI/RUC, texto libre. |
| `telefono`                       | string  | `telefono`                | |
| `sector`                         | string  | `sector`                  | Uno de los 4 valores fijos en `Sector` (`"Sector Norte"`, `"Sector Sur"`, `"Sector Este"`, `"Sector Oeste"`). |
| `entrega_directa_en_planta`      | boolean | `entregaDirectaEnPlanta`  | |
| `numero_vacas`                   | integer | `numeroVacas`             | |
| `calificacion`                   | string  | `calificacion`            | `"A"` \| `"B"` \| `"C"`. |
| `activo`                         | boolean | `activo`                  | Baja lógica de negocio (proveedor retirado, RN-11/12) — no confundir con `eliminado`, que es metadato de sincronización. |
| `actualizado_en`                 | string  | *(no está en el dominio)* | ISO-8601 local, sin zona horaria. Fuente de verdad para last-write-wins. |
| `eliminado`                      | boolean | *(no está en el dominio)* | Solo en `GET`; tumba de sincronización. |

## Próximos pasos (fuera de esta fase, solo para referencia)

Mismo patrón (`*Dto` + mapper + `*Api` + `*SyncManager`, sumado a `ServiceLocator.syncCoordinator`)
para:

- **Entrega**: el discriminador de tipo de `EstadoEntrega` (sellado) necesita un campo tipo
  `"estado_tipo"` en el JSON + campos específicos por variante (motivo, transportista_id,
  hora_salida, liquidacion_id, cancelada_por, fecha_hora_cancelacion), igual que ya hace
  `EntregaEntity` en SQLite. **RN-20 debe validarse también al aplicar cambios remotos**: no
  sobrescribir localmente una entrega que ya tiene `AnalisisCalidad` o está
  `EnTransitoAPlanta`/`Liquidada`, aunque el remoto sea "más reciente".
- **AnalisisCalidad**: mismo problema de discriminador con `ResultadoAnalisis`
  (`Normal | FueraDeRango | Adulterada`).
- **RutaAcopio**: incluye `ParadaRuta` como colección anidada; decidir si el JSON la manda inline
  o como sub-recurso (`/rutas/{id}/paradas`).
- **Liquidacion**: pagos semanales; probablemente necesite un endpoint de solo lectura agregado
  además del CRUD (totales por proveedor/semana) en vez de sincronizar fila por fila.

Optimización pendiente para cuando haya volumen real: `GET /proveedores?desde=<timestamp>` en vez
de traer el listado completo en cada ciclo.
