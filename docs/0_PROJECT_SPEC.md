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

## Estado Actual de Combate (2026-02-24)

- Filas atacantes por alcance: **5ft = 1**, **10ft = 2**, **15ft = 3**.
- Ataques de unidad calculados contra frente enemigo con tope inicial **frente enemigo + 2**.
- Si una unidad completa dos filas, puede extender frente con criaturas sobrantes.
- Bono por filas ocupadas: **+2 al ataque por fila a partir de la primera**.
- Daño actualiza criaturas y se recalculan frente/filas/ataques en el mismo combate.
- Modelo de turno: **1 turno = 2 rondas** (equipo con mayor iniciativa viva primero, luego el otro).
- Moral al 50%: **Rota** con retirada obligatoria (sin tirada).
- Reagrupamiento unico con personaje aliado en trayectoria.
- Persecucion manual tras huida en CLI.

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
	- `run_tests.bat`
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

## v3.2 (Completado)

### Alcance entregado
- Moral al 50%: estado **Rota** con retirada obligatoria.
- Persecucion manual tras huida en CLI y ataque de persecucion valido.
- Reagrupamiento unico con personaje aliado en trayectoria.
- Web: log con tiradas de moral y resumen de estado por turno.

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

## Plan v3.4 (Despliegue Cloud en AKS - Azure for Students)

### Objetivo
- Publicar el modo web del proyecto en AKS usando suscripción de estudiante y coste mínimo.

### Alcance inicial (MVP cloud)
1. Containerizar la aplicación web.
2. Desplegar en AKS con 1 nodo y configuración mínima.
3. Exponer servicio vía Ingress.
4. Definir controles básicos de coste y operación.

### Restricciones
- AKS no es 100% gratis permanente: se consume crédito de estudiante.
- Prioridad a simplicidad operativa: sin microservicios en primera fase.

### Entregables por rol
- Architect: blueprint AKS + topología de despliegue mínima.
- Developer: Dockerfile, manifests y parametrización por variables de entorno.
- Test Lead: smoke tests post-deploy y checklist de validación en entorno cloud.

## Changelog Corto
- 2026-02-22: Sincronización de reglas de combate (filas por alcance, frente enemigo +2, bono por filas, turnos por 2 rondas).
- 2026-02-22: Política de iteración y suite mínima de validación consolidada.
- 2026-02-22: Plan v3.3 añadido (alta de unidades desde URL mediante agente).
- 2026-02-24: v3.2 completado (Rota al 50%, persecucion manual, reagrupamiento y mejoras web).
- 2026-03-01: añadido plan v3.4 para despliegue AKS con Azure for Students (MVP cloud + control de costes).
