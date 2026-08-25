# Modelo de dominio — Sesión 2

Este documento explica las decisiones de diseño del modelo de dominio de `acopioleche`,
ubicado en `shared/src/commonMain/kotlin/pe/edu/upeu/acopioleche/domain/model/`, y qué se
dejó fuera deliberadamente de esta capa.

## 1. Por qué `Entrega` es la entidad principal

`Entrega` es el hecho de negocio alrededor del cual gira todo el sistema: una entrega de
leche de un proveedor a un centro de acopio es el evento que dispara el resto del flujo
(análisis de calidad, aceptación o rechazo, transporte a planta y, finalmente, liquidación
de pago). Todas las demás entidades existen para describir el contexto de una entrega o
las consecuencias de ella:

- `Proveedor`, `Acopiador` y `CentroAcopio` responden **quién** y **dónde**.
- `AnalisisCalidad` responde **si la leche entregada es apta**.
- `Notificacion` comunica eventos derivados de una entrega (o de una reunión).
- `Reunion` y `Asistencia` son procesos organizativos independientes, pero relacionados con
  los mismos actores (proveedores y acopiadores).

Por eso `Entrega` concentra el ciclo de vida más rico del dominio, modelado explícitamente
con el sealed interface `EstadoEntrega` (pendiente → aceptada/rechazada → en tránsito →
liquidada), y es la entidad de la que dependen la mayoría de casos de uso futuros
(registrar entrega, registrar análisis, liquidar pago, etc.).

## 2. Por qué `Proveedor` y `Acopiador` están separados

Aunque ambos son "actores de campo" y comparten el enum `TipoActor` para escenarios donde
solo importa el rol (por ejemplo, `Asistencia` a una reunión), representan roles operativos
distintos y no intercambiables:

- El **proveedor** es dueño del ganado y entrega leche cruda; puede entregar directamente en
  planta (`entregaDirectaEnPlanta`) o en un punto de acopio de su sector.
- El **acopiador** es la persona que recorre un conjunto de sectores (`sectoresAsignados`)
  recogiendo la leche de varios proveedores y trasladándola al centro de acopio; no produce
  leche, opera un vehículo.

Esta separación no es una decisión de diseño abstracta: nace de una reunión de campo con la
Municipalidad de Huata, donde se confirmó que ambos roles tienen atributos, reglas y
pantallas distintas en la práctica (un proveedor nunca tiene "sectores asignados"; un
acopiador nunca tiene "entrega directa en planta"). Modelarlos como una sola entidad con
campos opcionales habría obligado a usar nulls para representar reglas que en realidad son
mutuamente excluyentes, debilitando las validaciones estructurales de cada una.

`Entrega` referencia a ambos por id (`proveedorId` obligatorio, `acopiadorId` opcional, ya
que un proveedor puede entregar directamente sin pasar por un acopiador), en vez de modelar
una jerarquía o interfaz común entre ellos, porque en el dominio no comparten comportamiento,
solo un rol nominal.

## 3. Por qué `sealed interface` para `EstadoEntrega` y `ResultadoAnalisis`

Ambos representan un conjunto **cerrado y finito** de posibilidades mutuamente excluyentes,
donde cada variante puede llevar datos distintos:

- `EstadoEntrega`: `Pendiente` y `Aceptada` no necesitan datos adicionales (`data object`);
  `Rechazada` necesita el motivo; `EnTransitoAPlanta` necesita transportista y hora de
  salida; `Liquidada` necesita el id de liquidación. Una sola `data class` con campos
  nullable para cada caso habría permitido estados inválidos (p. ej. una entrega "Pendiente"
  con `motivo` seteado por error).
- `ResultadoAnalisis`: `Normal` lleva los tres valores medidos, `FueraDeRango` lleva el
  motivo y el rango permitido, `Adulterada` lleva el indicio detectado. Son resultados que
  no coexisten: un análisis no puede ser "Normal" y "Adulterada" a la vez.

Se usa `sealed interface` (no `sealed class`) siguiendo la convención del curso: las
variantes no necesitan estado ni comportamiento heredado de una superclase, así que una
interfaz sellada es suficiente y evita atar las variantes a una jerarquía de clases. El
compilador exige exhaustividad en cualquier `when` sobre estos tipos (ver
`EntregaTest.when exhaustivo sobre estado entrega cubre todos los casos`), lo que hace que
agregar un nuevo estado o resultado en el futuro sea un error de compilación en cualquier
lugar donde falte manejarlo, no un bug en tiempo de ejecución.

## 4. Qué se dejó fuera a propósito de esta capa

`shared/.../domain/model` solo contiene `data class`, `sealed interface` y `enum class`,
con la única validación estructural mínima en `init { require(...) }` (campos no vacíos,
números positivos, rangos horarios coherentes). Deliberadamente **no** contiene:

- **Cálculo de pago o liquidación**: el precio por litro, bonificaciones por calidad,
  descuentos por adulteración, etc. Esa es lógica de negocio que depende de reglas
  comerciales cambiantes (tarifario vigente, convenios por sector) y pertenece a la capa de
  casos de uso (`domain/usecase` o equivalente), no al modelo.
- **Transiciones de estado** (por ejemplo, un método `Entrega.aceptar()` o
  `Entrega.rechazar(motivo)` que decida si la transición es válida según el estado actual).
  Esa orquestación es responsabilidad de un caso de uso, que puede además necesitar acceso a
  repositorios o servicios externos.
- **Persistencia, formateo o presentación**: no hay anotaciones de base de datos, de
  serialización ni de UI en estas clases. `fecha`, `horaSalida` u `horaInicioMinutos` se
  modelan con tipos simples (`String`, `Int`) para no acoplar el dominio a una librería de
  fecha/hora específica en esta sesión; ese acoplamiento se decide en una capa de
  infraestructura o de datos, no aquí.
- **Validaciones de negocio complejas** (por ejemplo, si el rango de densidad/acidez/grasa
  "normal" es correcto según normativa sanitaria). El `init` de las entidades solo garantiza
  que los datos sean estructuralmente coherentes (no vacíos, positivos, rangos horarios
  válidos); decidir si un valor de densidad es aceptable es una regla de negocio que vive en
  un caso de uso (`RegistrarAnalisisCalidadUseCase` o similar), que es quien construye el
  `ResultadoAnalisis.FueraDeRango` correspondiente.

Mantener el modelo "tonto" (sin lógica de negocio) permite testearlo de forma trivial y
determinista —como se hace en `commonTest`— y evita que cambios en reglas de negocio
obliguen a tocar el modelo de dominio.

## Evidencia de ejecución

Los tests del paquete `pe.edu.upeu.acopioleche.domain.model` (18 tests, repartidos en
`EntregaTest`, `ProveedorAcopiadorTest`, `AnalisisCalidadTest` y `ReunionAsistenciaTest`) se
ejecutaron en verde en ambos targets configurados para `shared`:

```
./gradlew allTests

> Task :shared:desktopTest
> Task :shared:testAndroidHostTest
> Task :shared:allTests

BUILD SUCCESSFUL
```

Reportes JUnit generados en:
- `shared/build/test-results/desktopTest/` (target Desktop/JVM, 18 tests, 0 fallas)
- `shared/build/test-results/testAndroidHostTest/` (target Android, 18 tests, 0 fallas)
