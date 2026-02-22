# D&D Wargames - Architect Specification

**Rol:** System Architect  
**Responsabilidad:** Diseño de arquitectura, componentes y estructura del sistema  
**Versión:** 3.1 (Warhammer HP System + Front/Rows Initiative Update)

---

## 1. Visión Arquitectónica

### 1.1 Principios de Diseño
- [x] Modularidad: Componentes independientes y reutilizables
- [x] Separación de responsabilidades (SoC)
- [x] Extensibilidad: Fácil agregar nuevas mecánicas
- [x] Mantenibilidad: Código limpio y bien documentado

### 1.2 Patrones a Utilizar
- **MVC (Model-View-Controller)**: Separación entre lógica de juego y presentación
- **Factory Pattern**: Crear entidades (Personajes, Unidades, Items) ✅ UnitFactory
- **Strategy Pattern**: Diferentes estilos de combate
- **Observer Pattern**: Sistema de eventos durante el combate

### 1.3 Cambios Arquitectónicos v3.0
- **HP por Compañía**: `CombatUnit` elimina PF, usa `hitPoints` (HP total) + `creaturesCount`
- **Formación Warhammer**: `CombatUnit` añade `frontWidth`, `flankExposure`, `rowsAttacking`
- **Moral 2d6**: `DiceRoller.roll2D6()` + chequeo en `CombatUnit.checkMorale(2d6)`
- **Multi-ataques**: `CombatResolver` usa `getAttacksAvailable()` (frontWidth × rows)
- **Web Stats**: `WebBattleServer.renderHomePage()` muestra stats debajo del nombre de unidad

### 1.4 Cambios Arquitectónicos v3.1 (2026-02-22)
- **Filas por alcance** en `CombatUnit`: 5ft=1, 10ft=2, 15ft=3.
- **Frente efectivo contra enemigo**: tope inicial `enemyFront + 2`, con extensión si completa dos filas.
- **Bono por filas ocupadas**: +2 por fila adicional desde la segunda.
- **Re-cálculo continuo tras bajas**: ataques y filas se recalculan con criaturas restantes.
- **Turnos por rondas de equipo** en `WargameBattleEngine`:
    - 1 turno = ronda de aliados + ronda de enemigos.
    - El equipo de la unidad viva con mayor iniciativa actúa primero en ese turno.
    - Iniciativa tirada una vez por combatiente (`1d20 + mod DEX`).

---

## 2. Componentes Principales (Wargame Scale v3.0)

### 2.1 Core Architecture

```
com.dnd.wargames
├── units/                      (Definición de unidades)
│   ├── Character.java          (Personaje individual)
│   ├── CombatUnit.java         (Unidad HP por compañía) ★ v3.0
│   ├── CharacterClass.java     (Guerrero, Mago, etc)
│   ├── CreatureType.java       (Trasgo, Ogre, Skeleton, etc)
│   ├── MoraleEffect.java       (NONE, FRIGHTENED, CONFUSED, RAGING)
│   └── UnitFactory.java        (Factory para crear unidades) ★ v3.0
│
├── battle/                     (Sistema de combate)
│   ├── WargameBattleEngine.java (Motor a escala batallón)
│   ├── CombatResolver.java     (Multi-ataques por formación) ★ v3.0
│   ├── DiceRoller.java         (D&D + Warhammer 2d6) ★ v3.0
│   └── InitiativeTracker.java  (Orden de turnos)
│
├── web/                        (Interfaz Web ligera)
│   └── WebBattleServer.java    (HTTP con stats en selección) ★ v3.0
│
├── cli/                        (Interfaz CLI)
│   └── CommandLineInterface.java (Modo texto)
│
├── demo/                       (Demos de combate)
│   └── CombatDemo.java         (Demo batalla CLI)
│
├── test/                       (Tests unitarios)
│   ├── SimpleTest.java         (Test básico HP system) ★ v3.0
│   └── BasicTest.java          (Test formación) ★ v3.0
│
├── spells/                     (Sistema de magia a escala - FUTURO)
│   ├── SpellCaster.java        (Base para Mago/Clérigo/Bardo)
│   ├── Spell.java              (Interfaz de hechizo)
│   ├── AreaSpell.java          (Hechizos de daño/control)
│   ├── SupportSpell.java       (Hechizos de sostenimiento)
│   └── BattleInspiration.java  (Efectos de bardo)
│
├── reactions/                  (Reacciones heroicas - FUTURO)
│   ├── HeroicReaction.java     (Interfaz base)
│   ├── TacticalRetreat.java    (Repliegue táctico)
│   ├── WhirlwindSteel.java     (Torbellino de acero)
│   └── RepulsionShield.java    (Escudo de repulsión)
│
├── battlefield/                (Gestión del mapa - FUTURO)
│   ├── Battlefield.java        (Grid/mapa hexagonal)
│   ├── Tile.java               (Hexágono individual)
│   ├── Terrain.java            (Terreno: cobertura, altura)
│   └── Position.java           (Coordenada en mapa)
│
├── morale/                     (Sistema de moral)
│   ├── MoraleTracker.java      (Rastrear moral de unidades)
│   ├── MoraleEffect.java       (Estado: Asustada, Confundida)
│   └── MoraleCheck.java        (TS de Sabiduría para moral)
│
├── game/                       (Gestión principal del juego)
│   ├── WargameManager.java     (Orquestador principal)
│   ├── BattleState.java        (Estado completo de batalla)
│   └── TurnManager.java        (Control de turnos y rondas)
│
├── persistence/                (Guardar/Cargar)
│   ├── BattleSaver.java        (Serializar batalla)
│   └── BattleLoader.java       (Deserializar batalla)
│
└── ui/                         (Presentación)
    ├── BattleRenderer.java     (Mostrar estado de batalla)
    ├── CommandParser.java      (Parsear comandos jugador)
    └── Logger.java             (Log de eventos de combate)
```

### 2.2 Relaciones Entre Componentes

```
WargameManager
├── BattleState (contiene)
│   ├── List<Character> (jugador individual)
│   ├── List<CombatUnit> (unidades de monstruos)
│   ├── Battlefield (mapa)
│   └── TurnManager (orden de turnos)
│
├── WargameBattleEngine (resuelve combate)
│   ├── CombatResolver (calcula daño)
│   ├── DiceRoller (tiradas)
│   └── MassBonus (bono numéridad)
│
├── SpellCaster (magos/clérigos)
│   └── Spell implementations
│
├── HeroicReaction implementations
│   └── Ejecutadas por personajes
│
└── MoraleTracker (cohesión de unidades)
    └── MoraleEffect applications
```

### 2.3 Módulos Secundarios

- **Utils**: Validaciones, helpers, cálculos matemáticos
- **Config**: Constantes de reglas, bonificadores
- **Events**: Sistema de eventos de combate para logging

### 2.4 Modo Web Implementado (2026-02)

- Servidor embebido con `com.sun.net.httpserver.HttpServer` en puerto `8080`.
- Flujo de configuración en navegador:
    - Tipo de unidad
    - Equipo (`Aliados` / `Enemigos`)
    - Nº de compañías por tipo
    - PF por compañía
- Ejecución de batalla por bloques de turnos:
    - Usuario indica cuántos turnos ejecutar
    - El sistema devuelve estado actualizado y log del bloque
    - Se vuelve a solicitar nuevo bloque hasta fin de batalla
- Estado en memoria por sesión de batalla (ID único), sin persistencia en disco.

### 2.5 Bloque de estadísticas por unidad (2026-02)

- Cada `CombatUnit` mantiene estadísticas detalladas de criatura base:
    - AC, HP por criatura, fórmula de dados, velocidad
    - STR/DEX/CON/INT/WIS/CHA
    - ataque principal y secundario
- Estas estadísticas se renderizan en:
    - CLI (`showUnits` y confirmación de creación)
    - Web (`formatUnits` en vista de batalla)

---

## 3. Arquitectura de Combate (Wargame Scale)

### 3.1 Flujo de Combate a Escala

```
1. Setup: Crear Battlefield, posicionar unidades
   ↓
2. Determinar Iniciativa: Todos combatientes tiran d20+DEX
   ↓
3. TURNO (repetir):
    ├─ Ronda 1: equipo con mayor iniciativa viva
    │  └─ Cada combatiente vivo de ese equipo actúa en orden de iniciativa
    ├─ Ronda 2: equipo contrario
    │  └─ Cada combatiente vivo de ese equipo actúa en orden de iniciativa
    └─ Chequear condición de fin: ¿algún equipo sin combatientes vivos?
   ↓
4. Fin de Batalla: Calcular loot/experiencia

### 3.4 Moral Operativa (estado actual)

- El estado de moral se actualiza dinámicamente con PF restantes.
- Efectos activos en ataque de unidad:
    - `RAGING`: +2 al ataque
    - `CONFUSED`: -1 al ataque
    - `FRIGHTENED`: -2 al ataque
- Estos modificadores se aplican en `CombatResolver` para ataques de unidad vs unidad y unidad vs personaje.
```

### 3.2 Sistema de Daño Asimétrico

```java
// Unidad vs Unidad
if (attacker instanceof CombatUnit) {
    damage = 1 PF; // base
    if (roll > targetCA + 5) {
        damage += 1; // Regla de Hender
    }
}

// Unidad vs Personaje
if (attacker instanceof CombatUnit && target instanceof Character) {
    damage = baseMonsterDamage + massBonus;
    // va a HP del personaje
}

// Personaje vs Unidad
if (attacker instanceof Character) {
    damage = 1 PF; // Ventaja en tirada
    if (roll > targetCA + 5) {
        damage += 1; // Hender
    }
    if (characterHasExtraAttack) {
        // Múltiples ataques
    }
}
```

### 3.3 Pilares de Interacción

| Escenario | Mecánica | Resultado |
| --- | --- | --- |
| Unidad vs Unidad | 1d20 + Bono Masa | 1 PF por impacto |
| Personaje vs Unidad | 1d20 con Ventaja | 1 PF por impacto |
| Unidad vs Personaje | 1d20 + Bono Masa | Daño a HP |
| Personaje vs Personaje | 1d20 normal | Daño D&D normal |
| Magia vs Unidad | Tirada de Salvación | PF = Nivel + MOD |
| Reacción Heroica | Depende de tipo | Movimiento/Daño/Push |

---

## 4. Interfases y Contratos (Wargame Architecture)

### 4.1 Interfaz Combatant (Base para todos)
```java
interface Combatant {
    // Identificación
    String getId();
    String getName();
    
    // Estadísticas de combate
    int getArmorClass();          // CA
    int getAttackBonus();         // Bono de ataque
    int getDexterity();           // DEX para iniciativa
    
    // Daño
    void takeDamage(int amount);
    boolean isAlive();
    
    // Acciones
    CombatAction chooseAction(BattleState state);
}
```

### 4.2 Interfaz CombatUnit (Grupos de monstruos)
```java
interface CombatUnit extends Combatant {
    // Puntos de Fuerza (cantidad de criaturas)
    int getStrengthPoints();      // PF actual
    int getMaxStrengthPoints();   // PF máximo
    
    // Características de la criatura
    int getBaseDamage();          // Daño promedio
    int getDurabilityThreshold(); // Dureza (para criaturas grandes)
    
    // Moral
    int getMorale();
    void applyMoraleEffect(MoraleEffect effect);
    MoraleEffect getMoraleStatus();
    
    // Líder especial
    SpecialLeader getLeader();
    void setLeader(SpecialLeader leader);
}
```

### 4.3 Interfaz Character (Personaje individual)
```java
interface Character extends Combatant {
    // Puntos de vida D&D estándar
    int getHealth();
    int getMaxHealth();
    
    // Atributos D&D (6)
    int getAbilityScore(Ability ability); // STR, DEX, CON, INT, WIS, CHA
    int getAbilityModifier(Ability ability);
    
    // Clase de personaje
    CharacterClass getCharacterClass();
    
    // Hechizos (para magos/clérigos/bardos)
    List<Spell> getAvailableSpells();
    void castSpell(Spell spell, CombatUnit target, BattleState state);
    
    // Reacciones heroicas
    HeroicReaction getHeroicReaction();
    boolean canUseReaction();
    void useReaction(HeroicReaction reaction);
    
    // Ataques múltiples
    int getNumAttacks(); // Extra Attack?
}
```

### 4.4 Interfaz Spell (Hechizos)
```java
interface Spell {
    String getName();
    int getSpellLevel();
    SpellType getType(); // DAMAGE, CONTROL, SUPPORT, INSPIRATION
    
    // Efectos del hechizo
    void executeAgainstUnit(CombatUnit target, Character caster, BattleState state);
    
    // Para unidades enemigas
    int getSavingThrowDC(Character caster);
    Ability getSaveAbility(); // DEX, CON, etc
}

// Subinterfaces
interface AreaSpell extends Spell {
    // Daño de área: Bola de Fuego, Trueno, etc.
    int getAreaRadius();
    int getDamageOnFail(Character caster);
    int getDamageOnSuccess(Character caster);
}

interface SupportSpell extends Spell {
    // Sanación/Buffs: Palabra Sanadora Masiva, Bendición
    int getHealthRestoration();
    Duration getBuffDuration();
}

interface InspirationSpell extends Spell {
    // Efectos de Bardo: Inspiración de Batalla
    int getInspitrationDie(); // d6, d8, d10, d12
    List<String> getAffectedRolls(); // Attack, Damage, etc
}
```

### 4.5 Interfaz HeroicReaction (Reacciones especiales)
```java
interface HeroicReaction {
    String getName();
    ReactionType getType(); // TACTICAL, OFFENSIVE, MAGICAL
    
    // Activación
    boolean canActivate(Character user, BattleState state);
    void activate(Character user, CombatUnit targetUnit, BattleState state);
    
    // Costo
    boolean hasResourceCost(); // Gasta hechizo, acción, etc?
    void payCost();
}

// Tipos concretos
interface TacticalRetreat extends HeroicReaction {
    // Movimiento libre 50% velocidad
}

interface WhirlwindSteel extends HeroicReaction {
    // Ataque contra unidad + frenar movimiento
}

interface RepulsionShield extends HeroicReaction {
    // Empujón + TS Fuerza
}
```

### 4.6 Interfaz CombatResolver (Cálculo de combate)
```java
class CombatResolver {
    // Resolver ataque
    AttackResult resolveAttack(
        Combatant attacker,
        Combatant target,
        int modifyingBonus
    );
    
    // Calcular daño
    int calculateDamage(
        Combatant attacker,
        Combatant target,
        AttackResult previousAttack
    );
    
    // Aplicar daño
    void applyDamage(Combatant target, int damage, DamageType type);
    
    // TS (Tirada de Salvación)
    boolean roleSaveThrow(
        CombatUnit defender,
        int saveDC,
        Ability ability
    );
}
```

### 4.7 Interfaz BattleField (Mapa)
```java
class BattleField {
    // Dimensiones
    int getWidth();
    int getHeight();
    
    // Posicionamiento
    void placeUnit(CombatUnit unit, Position pos);
    void placeCharacter(Character char, Position pos);
    Position getPosition(Combatant combatant);
    
    // Terreno
    Terrain getTerrain(Position pos);
    int getTerrainBonus(Position pos, String type); // CA, Attack, etc
    
    // Movimiento
    boolean canMove(Combatant combatant, Position target);
    int getDistance(Position from, Position to);
}
```

---

## 5. Dependencias Externas

- **JDK 25.0.2** (Eclipse Adoptium)
- **Logging**: java.util.logging (built-in)
- Sin dependencias externas inicialmente

---

## 6. Consideraciones de Escalabilidad

- [ ] Sistema de plugins para nuevas mecánicas
- [ ] Persistencia (guardar/cargar partidas)
- [ ] Multiplayer (opcional futuro)
- [ ] Base de datos para estadísticas

---

## 7. Checkpoints Arquitectónicos (Fases de Desarrollo)

### Phase 1: Core Units & Basic Combat
- [ ] Character entity (stats D&D completos)
- [ ] CombatUnit entity con PF
- [ ] DiceRoller funcional (d20)
- [ ] MassBonus calculation
- [ ] Ataque Unidad vs Unidad (1 tirada)
- [ ] Daño: PF reducción
- [ ] Initiative system

### Phase 2: Character vs Unit Asymmetry
- [ ] Ataque Personaje vs Unidad con Ventaja
- [ ] Ataque Unidad vs Personaje (daño a HP)
- [ ] Zona de Amenaza
- [ ] Ataque de Oportunidad básico
- [ ] Extra Attack para Guerreros

### Phase 3: Reacciones Heroicas
- [ ] HeroicReaction interface
- [ ] TacticalRetreat implementation
- [ ] WhirlwindSteel implementation
- [ ] RepulsionShield implementation
- [ ] Límite de 2 reacciones por ronda

### Phase 4: Magia a Gran Escala
- [ ] SpellCaster base
- [ ] AreaSpell (Bola de Fuego, Trueno)
- [ ] SupportSpell (Sanación, Buffs)
- [ ] Tirada de Salvación para unidades
- [ ] Cálculo de daño PF por hechizo

### Phase 5: Moral y Cohesión
- [ ] MoraleTracker
- [ ] MoraleEffect (Asustada, Confundida)
- [ ] MoraleCheck (d20 + WIS)
- [ ] Disparadores de Moral

### Phase 6: Battlefield & Positioning
- [ ] Mapa hexagonal/cuadriculado
- [ ] Posicionamiento de unidades
- [ ] Terreno (altura, cobertura)
- [ ] Movimiento limitado por velocidad
- [ ] Bonificadores de terreno

### Phase 7: Persistence & Complete Game Loop
- [ ] BattleSaver (serializar estado)
- [ ] BattleLoader (deserializar estado)
- [ ] CommandLineInterface completo
- [ ] Turnos y rondas completos
- [ ] Fin de batalla y reporting

---

## 8. Consideraciones de Escalabilidad

- **Unidades Simultáneas**: Arquitectura permite 50+ sin problema
- **Sistema de Eventos**: Permite logging/debugging
- **Modular**: Fácil agregar nuevas clases de personaje o tipos de unidad
- **Reglas Extensibles**: Nuevosy hereda de Spell, HeroicReaction, etc
- **UI agnóstico**: Motor de combate separado de presentación

---

## 9. Decisiones Arquitectónicas Clave

### 9.1 Por qué dos interfaces: CombatUnit vs Character?
- **CombatUnit**: Optimizado para grandes números (no rastrea cada criatura)
- **Character**: Todo el poder de D&D 5e (6 atributos, hechizos, clases)
- **Ventaja**: Escalabilidad sin sacrificar profundidad

### 9.2 Por qué Reacciones Heroicas aparte?
- **Separación**: Heroic Reactions solo aplican a Character solo vs Unit
- **Extensible**: Fácil agregar nuevas reacciones por clase
- **Balanceado**: Limita a 2 por ronda para evitar poder excesivo

### 9.3 Por qué CombatResolver centralizado?
- **Consistencia**: Todas las tiradas usan mismo RNG
- **Debugging**: Fácil ver dónde se calcula daño
- **Testing**: Testear combate independiente de UI

---

## 10. Plan Arquitectónico v3.2 (UA 2015-aligned)

### Decisiones de Diseño Cerradas
- Motor final en modo **detallado** (sin capa BR por defecto).
- Escala táctica base de **20 ft** por celda.
- Fallo de moral por 50% de bajas -> estado **Rota** con retirada obligatoria.
- Persecución tras huida con decisión de jugador en CLI.

### Implementación Arquitectónica
1. Introducir una máquina de estados de unidad (`Normal/Rota/Huyendo/Reagrupada`).
2. Separar explícitamente resolución de daño y resolución de moral/retiro.
3. Mantener estrategia de decisión inyectable (`BattleDecisionProvider`) para persecución.
4. Añadir punto de extensión para objetivos de batalla (VP) en fase posterior.

---

## Versión
- v2.0 - Wargame Scale - Actualizado: 2026-02-08
- v1.0 - Definido: 2026-02-08

## Changelog Corto
- 2026-02-22: Arquitectura de combate actualizada a filas por alcance 5/10/15ft = 1/2/3.
- 2026-02-22: Flujo de turno actualizado a 2 rondas por turno (equipos alternos).
- 2026-02-22: Cálculo de frente efectivo contra enemigo +2 y extensión por segunda fila documentado.
