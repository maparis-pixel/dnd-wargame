# D&D Wargames - Project Specification Master

## Descripción General
Simulador de combate para juegos de mesa tipo Dungeons & Dragons. Este documento es el índice maestro de todas las especificaciones del proyecto, organizadas por rol.

## Roles del Equipo

### 1. **Arquitecto (System Architect)**
Responsable de la arquitectura del sistema, diseño de componentes, patrones y estructura general.
- Documento: [ARCHITECT_SPEC.md](./ARCHITECT_SPEC.md)
- Tareas: Diseño de arquitectura, componentes, dependencias, patrones

### 2. **Desarrollador (Lead Developer)**  
Responsable de la implementación, estándares de código y convenciones.
- Documento: [DEVELOPER_SPEC.md](./DEVELOPER_SPEC.md)
- Tareas: Implementación, coding standards, APIs, métodos

### 3. **Test Lead (QA/Testing)**
Responsable de estrategia de testing, casos de prueba y cobertura.
- Documento: [TEST_LEADER_SPEC.md](./TEST_LEADER_SPEC.md)
- Tareas: Test cases, coverage, validation, edge cases

## Estructura de Documentación

```
docs/
├── 0_PROJECT_SPEC.md              (Este archivo - Índice maestro)
├── ARCHITECT_SPEC.md              (Especificaciones de Arquitectura)
├── DEVELOPER_SPEC.md              (Especificaciones de Desarrollo)
├── TEST_LEADER_SPEC.md            (Especificaciones de Testing)
├── REQUIREMENTS.md                (Requisitos funcionales generales)
└── ../RESUMEN_SESION.md            (Avances y estado de sesión)
```

## Estado Actual de Combate (2026-02-22)

- Filas atacantes por alcance: **5ft = 1**, **10ft = 2**, **15ft = 3**.
- Ataques de unidad calculados contra frente enemigo con tope inicial **frente enemigo + 2**.
- Si una unidad completa dos filas, puede extender frente con criaturas sobrantes.
- Bono por filas ocupadas: **+2 al ataque por fila a partir de la primera**.
- Daño actualiza criaturas y se recalculan frente/filas/ataques en el mismo combate.
- Modelo de turno: **1 turno = 2 rondas** (equipo con mayor iniciativa viva primero, luego el otro).

## Cómo Usar Este Sistema

1. **Para repetir el proyecto**: Leer este archivo y los specs de cada rol
2. **Para agregar features**: Actualizar los specs correspondientes según el rol
3. **Para validar**: Verificar que todas las especificaciones se cumplen

## Política de Iteración Obligatoria

Cada cambio de código debe cumplir este flujo en el mismo ciclo de trabajo:

1. Actualizar documentación de plan y de perfiles afectados:
	- `ARCHITECT_SPEC.md`
	- `DEVELOPER_SPEC.md`
	- `TEST_LEADER_SPEC.md`
	- `REQUIREMENTS.md` (si aplica)
2. Compilar el proyecto completo.
3. Ejecutar la suite mínima de validación funcional:
	- `com.dnd.wargames.test.SimpleTest`
	- `com.dnd.wargames.test.BasicTest`
	- `com.dnd.wargames.test.MoraleAndWebSmokeTest`
4. Si falla compilación o tests, corregir y repetir.

## Versión
- v1.0 - Creado: 2026-02-08
- v2.1 - Actualizado: 2026-02-22 (Web UI + política obligatoria compile/tests)
- v2.2 - Actualizado: 2026-02-22 (Estadísticas detalladas de unidades en CLI/Web)
- v2.3 - Actualizado: 2026-02-22 (Reglas de frente/filas/iniciativa sincronizadas)
- Estado: Activo y en evolución

## Links de Referencia
- README general: [../README.md](../README.md)
- Código fuente: `src/com/dnd/wargames/`

## Plan v3.2 (Decisiones Cerradas)

### Decisiones Finales
- Modelo final: **detallado** (sin BR abstracto de UA 2017).
- Escala base: **20 ft por casilla** y **1 minuto por ronda**.
- Moral por 50% de bajas: estado **Rota** con **retirada obligatoria**.
- Persecución tras huida: **decisión manual** del jugador en CLI.

### Plan de Implementación
1. Formalizar estados de unidad: `Normal`, `Rota`, `Huyendo`, `Reagrupada`.
2. Restringir acciones de unidad rota a retirada/reagrupamiento.
3. Consolidar flujo de persecución o retarget manual tras huida.
4. Limitar reagrupamiento a un intento, asistido por personaje aliado.
5. Añadir pruebas de aceptación de moral, retirada y persecución.

## Plan v3.3 (Nuevas Unidades desde URL)

### Objetivo
- Añadir una página web para registrar **nuevos tipos de unidad** usando datos de ficha D&D.

### Flujo objetivo
1. El usuario introduce una URL de la ficha de criatura.
2. Un agente transforma el contenido a una **interfaz de entrada estándar**.
3. El usuario revisa/edita los campos normalizados.
4. El sistema crea un nuevo tipo de unidad reutilizable en batalla.

### Entregables
- Endpoint y pantalla web para “Alta de tipo de unidad”.
- Esquema de datos normalizado para ficha D&D.
- Adaptador de agente URL -> esquema de unidad.
- Persistencia de tipos personalizados.

## Changelog Corto
- 2026-02-22: Sincronización de reglas de combate (filas por alcance, frente enemigo +2, bono por filas, turnos por 2 rondas).
- 2026-02-22: Política de iteración y suite mínima de validación consolidada.
- 2026-02-22: Plan v3.3 añadido (alta de unidades desde URL mediante agente).
