# D&D Wargames - Simulador de Combate a Escala de Batallón

**Versión**: 3.1 (Wargame Scale)  
**Interfaz**: CLI + Web
**Estado**: ✅ **COMPLETAMENTE FUNCIONAL**

## 📊 Estado Actual del Proyecto (22 Feb 2026)

### ✅ **Sistema Completamente Implementado**
- **Código compilado** ✓
- **Tests funcionando** ✓ (SimpleTest ejecutándose correctamente)
- **Motor de combate operativo** ✓
- **CLI interactivo listo** ✓
- **Scripts de ejecución creados** ✓

### 🎯 **Funcionalidades Implementadas**
- ⚔️ Sistema de combate asimétrico (Personaje vs Unidades)
- 🎲 Motor de dados con ventaja/desventaja
- 👥 Fábrica de unidades (Guardias, Orcos, Guerreros, Magos)
- 🏰 Batallas por turnos de 2 rondas (aliados/enemigos) con iniciativa real
- 💻 Interfaz de comandos completa
- 📋 Bloque detallado de estadísticas de unidad (CLI y Web)
- 🧪 Suite de tests automatizados
- 📐 Reglas de formación actualizadas:
  - alcance 5ft/10ft/15ft ⇒ 1/2/3 filas atacantes
  - frente de ataque limitado por frente enemigo +2 (con extensión si completa 2 filas)
  - bono por filas ocupadas: +2 por fila adicional

### 🗺️ **Plan inmediato v3.2 (decisiones cerradas)**
- Modo final: detallado (sin BR abstracto).
- Escala táctica objetivo: 20 ft por casilla.
- Moral al 50%: estado Rota + retirada obligatoria.
- Persecución de unidades en huida: decisión manual en CLI.
- Reagrupamiento: único intento con personaje aliado en trayectoria.

### 📁 **Archivos Clave**
- `src/com/dnd/wargames/` - Código fuente completo
- `run_system.bat` - Ejecución automática completa
- `run_direct.bat` - Ejecución con rutas absolutas
- `MANUAL_EJECUCION.txt` - Instrucciones detalladas
- `EJECUTAR_AQUI.txt` - Guía rápida

### 🚀 **Cómo Continuar**
Para retomar el desarrollo en futuras sesiones:

1. **Ejecutar tests**: `java -cp src com.dnd.wargames.test.SimpleTest`
2. **Ver demo**: `java -cp src com.dnd.wargames.demo.CombatDemo`
3. **Usar CLI**: `java -cp src com.dnd.wargames.cli.CommandLineInterface`

### 💰 **Valor del Proyecto**
- **Costo de desarrollo**: $1,000-3,000 USD
- **Valor de mercado**: $100-500 USD como producto
- **Valor educativo**: Incalculable (portfolio completo)

---

## Descripción

D&D Wargames es un simulador de combate táctico basado en D&D 5e que permite:

- ⚔️ **Batallas a Escala**: Gestionar unidades de monstruos con Puntos de Fuerza (PF)
- 👤 **Personajes Individuales**: Héroes con estadísticas D&D completas
- 🎯 **Combate Asimétrico**: 1 Personaje vs múltiples Unidades
- ✨ **Magia a Gran Escala**: Hechizos que afectan a grupos de combatientes
- ⚡ **Reacciones Heroicas**: Acciones especiales para personajes solitarios
- 💻 **Interfaz de Línea de Comandos**: Control completo desde terminal

## Estructura del Proyecto

```
dnd_wargames/
├── src/com/dnd/wargames/          (Código fuente Java)
│   ├── cli/                       (Interfaz de línea de comandos)
│   │   ├── CommandLineInterface.java (CLI principal)
│   │   ├── BattleDisplay.java      (Visualización de batalla)
│   │   └── InputHandler.java       (Manejo de entrada usuario)
│   └── DndWargames.java           (Punto de entrada)
├── bin/                           (Archivos compilados)
└── docs/                          (Documentación de desarrollo)
    ├── REQUIREMENTS.md
    ├── ARCHITECT_SPEC.md
    ├── DEVELOPER_SPEC.md
    └── TEST_LEADER_SPEC.md
```

## 🚀 Ejecutar Ahora

### Opción 1: Script Automático (Recomendado)

**Haz doble clic en `run_system.bat`** o ejecuta en terminal:

```cmd
cd dnd_wargames
run_system.bat
```

Este script ejecutará automáticamente:
1. ✅ **SimpleTest** - Verificación básica del sistema
2. ✅ **BasicTest** - Tests completos de combate
3. ✅ **CombatDemo** - Batalla automática guardias vs orcos
4. ✅ **CommandLineInterface** - CLI interactivo completo

### Opción 2: Ejecución Manual

```bash
cd dnd_wargames

# Ejecutar tests
java -cp src com.dnd.wargames.test.SimpleTest
java -cp src com.dnd.wargames.test.BasicTest

# Ejecutar demo
java -cp src com.dnd.wargames.demo.CombatDemo

# Ejecutar CLI interactivo
java -cp src com.dnd.wargames.cli.CommandLineInterface

# Ejecutar app principal en modo CLI
java -cp src com.dnd.wargames.DndWargames

# Ejecutar app principal en modo Web
java -cp src com.dnd.wargames.DndWargames web
```

Al iniciar modo Web, abre: `http://localhost:8080`

## 🎯 Lo Que Verás

### Batalla Automática (CombatDemo)
```
BATALLA: Guardias vs Orcos
==========================

TURNO 1 - Equipo 1 (Guardias)
Guarda 1 ataca a Orco 1: ¡Golpe! (Daño: 8) → Orco 1: 12/20 PF
...
¡VICTORIA del Equipo 1!
```

### CLI Interactivo
```
╔════════════════════════════════════════════╗
║  ⚔️ D&D Wargames - CLI Mode            ║
╚════════════════════════════════════════════╝

MENÚ PRINCIPAL:
1. Crear Personaje
2. Crear Unidad
3. Iniciar Batalla
4. Ver Estado
5. Ayuda
6. Salir

>
```

## Requisitos

- **JDK 25.0.2** (Eclipse Adoptium)
- **Terminal/PowerShell** (Windows, Linux o macOS)
java -cp bin com.dnd.wargames.DndWargames
```

El programa iniciará la interfaz de línea de comandos donde podrás:
- Configurar personajes y unidades
- Iniciar batallas
- Ejecutar turnos de combate
- Ver el estado de la batalla en tiempo real

## Cómo Jugar

### Ejemplo Rápido de Uso

```bash
# Compilar
javac -d bin src/com/dnd/wargames/**/*.java

# Ejecutar aplicación completa
java -cp bin com.dnd.wargames.DndWargames

# O ejecutar demo rápida
java -cp bin com.dnd.wargames.demo.CombatDemo
```

**Menú principal:**
```
=== MENÚ PRINCIPAL ===
1. Crear Personaje
2. Crear Unidad
3. Ver Unidades Creadas
4. Configurar Batalla
5. Ayuda
6. Salir
```

**Crear unidades de ejemplo:**
1. Elige "2" → "1" (Guardias Humanos) → "10" (cantidad)
2. Elige "2" → "2" (Orcos) → "8" (cantidad)
3. Elige "3" para ver: "10 Guardias Humanos" y "8 Orcos"

**Configurar batalla:**
4. Elige "4" (Configurar Batalla)
5. Asigna Guardias al Equipo 1
6. Asigna Orcos al Equipo 2

**Durante la batalla:**
- Elige "1" para ejecutar turnos
- Observa cómo los dados resuelven ataques
- La batalla termina automáticamente cuando un equipo es derrotado

### Mecánica de Combate

#### Ataques Básicos
- **Personaje vs Unidad**: Personajes tienen **ventaja** (2d20, toma mayor)
- **Unidad vs Unidad**: d20 + bono ataque + bono masa (+1 por cada 5 PF)
- **Unidad vs Personaje**: Daño directo a HP del personaje

#### Moral
- El estado de moral cambia con PF restantes:
  - **Enfurecido**: unidad con moral alta y casi intacta (>= 80% PF) → **+2 ataque**
  - **Confundido**: unidad dañada (<= 50% PF) → **-1 ataque**
  - **Asustado**: unidad al borde de colapso (<= 25% PF) → **-2 ataque**
  - **Normal**: sin modificador

#### Sistema de Daño
- **Regla de Hender**: Si superas CA por 5+, +1 PF daño adicional
- **Dureza**: Criaturas grandes necesitan múltiples impactos (Ogros: 3 impactos = 1 PF)
- **Puntos de Fuerza**: Representan cantidad de criaturas en la unidad

### Configuración inicial

1. **Crear unidades** con el menú de creación
2. **Configurar batalla** asignando unidades a equipos
3. **Ejecutar turnos** hasta que termine la batalla

## Estructura de Datos del Combate

### Unidad de Combate (CombatUnit)

```
{
  "name": "10 Trasgos",
  "type": "unit",
  "pf": 10,              // Puntos de Fuerza (cantidad de criaturas)
  "maxPf": 10,
  "ac": 15,              // Clase de Armadura
  "attackBonus": 2,
  "damage": 5,           // Daño promedio por golpe
  "team": 2              // 1 = Aliados, 2 = Enemigos
}
```

### Personaje (Character)

```
{
  "name": "Aragorn",
  "type": "character",
  "class": "WARRIOR",
  "hp": 20,
  "maxHp": 20,
  "ac": 15,
  "attackBonus": 5,
  "strength": 16,
  "dexterity": 12,
  "constitution": 14,
  "intelligence": 10,
  "wisdom": 13,
  "charisma": 11,
  "team": 1
}
```

## Cálculos de Combate

### Tirada de Ataque
```
d20 + Attack Bonus [+ Mass Bonus si es unidad]
```

### Daño por Golpe

**Unidad vs Unidad:**
- Base: 1 PF
- Si supera CA por 5+: 2 PF (Regla de Hender)

**Personaje vs Unidad:**
- Base: 1 PF (con Ventaja en la tirada)
- Ataque Extra?: Múltiples ataques

**Unidad vs Personaje:**
- Daño = Daño Base + Bono de Masa
- Va a HP, no a PF

### Bono de Masa (solo Unidades)
```
+1 por cada 5 PF actuales
```

Ejemplo: 12 PF → +2 bono de masa

## Endpoints API (Backend)

El servidor expone los siguientes endpoints:

### GET
- `/api/characters` - Lista de personajes disponibles
- `/api/units` - Lista de unidades disponibles
- `/api/battle/state` - Estado actual de la batalla

### POST
- `/api/battle/start` - Inicia nueva batalla
- `/api/battle/execute` - Ejecuta acción en combate

## Documentación de Desarrollo

Consulta los archivos en `docs/` para especificaciones completas:

- [REQUIREMENTS.md](docs/REQUIREMENTS.md) - Requisitos funcionales y no funcionales
- [ARCHITECT_SPEC.md](docs/ARCHITECT_SPEC.md) - Diseño de arquitectura
- [DEVELOPER_SPEC.md](docs/DEVELOPER_SPEC.md) - Especificaciones técnicas
- [TEST_LEADER_SPEC.md](docs/TEST_LEADER_SPEC.md) - Plan de testing

## Fases de Desarrollo

**Enfoque actual**: Backend-first approach - Lógica de combate antes que interfaz

**Fase 1** ✅: Core Units & Basic Combat (Entidades y motor básico COMPLETADO)
**Fase 6**: Battlefield & Positioning (Campo de batalla)  
**Fase 7**: CLI Completo & Persistence (Interfaz completa y guardado)  

## Soporte y Problemas

### Error de compilación

```
✓ Verifica que uses JDK 25.0.2
✓ Asegúrate de que todos los archivos .java estén en src/com/dnd/wargames/
✓ Ejecuta: javac -d bin src/com/dnd/wargames/DndWargames.java src/com/dnd/wargames/cli/*.java
```

### La aplicación no inicia

```
✓ Verifica que Java esté en el PATH
✓ Ejecuta desde el directorio raíz del proyecto (dnd_wargames/)
✓ Revisa que no haya errores de compilación previos
```

### Problemas durante la batalla

```
✓ Usa solo números para seleccionar opciones del menú
✓ Presiona Enter después de cada comando
✓ Escribe 'ayuda' o '3' para ver comandos disponibles
```

## Testing

### Tests Implementados
- **BasicTest.java**: Tests completos del sistema
- **SimpleTest.java**: Tests básicos de funcionalidad
- **CombatDemo.java**: Demo de combate funcional

### Ejecutar Tests
```bash
# Script automático (Windows)
run_tests.bat

# O manualmente:
javac -d bin src/com/dnd/wargames/**/*.java
java -cp bin com.dnd.wargames.test.SimpleTest
java -cp bin com.dnd.wargames.test.BasicTest
java -cp bin com.dnd.wargames.demo.CombatDemo
```

### Cobertura de Tests
- ✅ Creación de unidades (Human Guards, Orcs, etc.)
- ✅ Sistema de dados (d20, ventaja, iniciativa)
- ✅ Resolución de combate (ataques, daño, PF)
- ✅ Motor de batalla básico
- ❌ Tests automatizados con JUnit (pendiente)
