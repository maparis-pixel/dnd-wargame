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
└── PROJECT_ROADMAP.md             (Roadmap del proyecto)
```

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
- Estado: Activo y en evolución

## Links de Referencia
- README general: [../README.md](../README.md)
- Código fuente: `src/com/dnd/wargames/`
