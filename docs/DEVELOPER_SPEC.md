# D&D Wargames - Developer Specification

**Rol:** Lead Developer  
**Responsabilidad:** Implementación, coding standards y especificaciones técnicas

---

## 1. Estándares de Código

### 1.1 Naming Conventions
- **Clases**: PascalCase (`Character`, `BattleEngine`)
- **Métodos/Variables**: camelCase (`getHealth()`, `playerAction`)
- **Constantes**: UPPER_SNAKE_CASE (`MAX_HP`, `ATTACK_BONUS`)
- **Packages**: lowercase, reverse domain (`com.dnd.wargames`)

### 1.2 Estructura de Clases
```java
public class Example {
    // 1. Constantes
    private static final int DEFAULT_VALUE = 10;
    
    // 2. Campos estáticos
    private static int counter = 0;
    
    // 3. Campos de instancia
    private String name;
    private int health;
    
    // 4. Constructores
    public Example(String name) {
        this.name = name;
    }
    
    // 5. Métodos públicos
    public void doSomething() {}
    
    // 6. Métodos privados
    private void helper() {}
    
    // 7. getters/setters
    public String getName() { return name; }
}
```

### 1.3 Documentación (JavaDoc)
```java
/**
 * Breve descripción de la clase.
 * 
 * Descripción más detallada si es necesario.
 * 
 * @author Developer Name
 * @version 1.0
 */
public class MyClass {
    
    /**
     * Descripción del método.
     * 
     * @param param1 descripción del parámetro
     * @return descripción del retorno
     * @throws ExceptionType descripción de la excepción
     */
    public String doSomething(String param1) {
        return param1;
    }
}
```

---

## 2. Especificaciones de Entidades (Wargame Scale)

### 2.1 Character (Personaje Individual)

**Atributos:**
```java
// Identidad
private String id;              // UUID
private String name;            // Nombre del personaje
private CharacterClass charClass; // WARRIOR, WIZARD, CLERIC, BARD, ROGUE

// Ability Scores (Puntuaciones de Habilidad D&D 5e)
private int strength;           // STR (1-20, típicamente 8-15)
private int dexterity;          // DEX
private int constitution;       // CON
private int intelligence;       // INT
private int wisdom;             // WIS
private int charisma;           // CHA

// Salud y Combate
private int health;             // HP actual
private int maxHealth;          // HP máximo (calculado de CON)
private int armorClass;         // CA (defensa)
private int attackBonus;        // Bono de ataque base
private int level;              // 1-20

// Experiencia y Progresión
private int experience;         // Total XP
private int proficiencyBonus;   // Bonus de pericia

// Equipamiento
private Item weapon;            // Arma equipada
private Item armor;             // Armadura equipada

// Hechizos
private List<Spell> preparedSpells;  // Hechizos preparados
private int spellSlots[];            // Slots por nivel 1-9 (array de 9)

// Habilidades Especiales
private HeroicReaction heroicReaction;   // Repliegue/Torbellino/Repulsión
private int reactionsUsedThisRound;      // Contador (máximo 2)
private boolean hasExtraAttack;          // Ataque Extra para Guerreros
```

**Métodos principales:**
```java
// Cálculos D&D
public int getAbilityModifier(Ability ability);      // (Score - 10) / 2
public int calculateMaxHealth();                      // 10 + (CON mod * Level)
public int calculateInitiative();                     // d20 + DEX mod
public int calculateAttackBonus();                    // Base + Weapon Bonus

// Combate
public int rollAttack();                              // d20 + Attack Bonus
public int calculateDamage();                         // Weapon Damage + STR/DEX
public void takeDamage(int amount);                   // Reduce HP
public boolean isAlive();                             // HP > 0?

// Spells
public void castSpell(Spell spell, CombatUnit target, BattleState state);
public boolean canCastSpell(Spell spell);             // ¿Tiene slots?

// Reacciones Heroicas
public void activateReaction(HeroicReaction reaction, CombatUnit unit, BattleState state);
public boolean canUseReaction();                      // ¿Quedan reacciones?
public void resetReactionsForNewRound();              // Al inicio de ronda
```

### 2.2 CombatUnit (Unidad de Monstruos)

**Atributos:**
```java
// Identidad
private String id;              // UUID
private String name;            // Ej: "Unidad de 15 Trasgos"
private String creatureType;    // GOBLIN, SKELETON, OGREs, etc

// Puntos de Fuerza (PF)
private int strengthPoints;     // PF actual (cantidad de criaturas)
private int maxStrengthPoints;  // PF máximo inicial

// Estadísticas de Combate
private int armorClass;         // CA de criatura individual
private int baseAttackBonus;    // Bono de ataque base
private int baseDamage;         // Daño promedio por criatura
private int dexterity;          // Para iniciativa

// Dureza (para criaturas grandes)
private int durability;         // Cuántos impactos = 1 PF (1, 3, 5)
private int currentDamageAccumulated;  // Acumula daño parcial

// Moral
private MoraleEffect moraleStatus;  // NONE, FRIGHTENED, CONFUSED
private int morale;                 // Base 10-20

// Posicionamiento
private Position position;      // Ubicación en mapa
private int maxMovement;        // Velocidad (30, 40, 60 pies)

// Líder Especial
private SpecialLeader leader;   // Capitán/Sargento (puede ser null)

// Bonificadores Temporales
private int temporaryAttackBonus;   // De Inspiración, Bendición, etc
private int temporaryACBonus;
```

**Métodos principales:**
```java
// Combate
public int calculateMassBonus();                // +1 per 5 PF actual
public int rollAttack();                        // d20 + Base + Mass
public int calculateDamageToCharacter(Character target);  // D&D normal + mass

// Recibir daño
public void takePFDamage(int pfAmount);                // Reduce PF
public void applyDamageWithDurability(int rawDamage);  // Acumula parcial

// Moral
public void applyMoraleEffect(MoraleEffect effect);
public boolean mustMakeMoraleCheck();
public boolean passMoraleCheck();

// Posicionamiento
public void moveTo(Position newPosition);

// Líder
public SpecialLeader getLeader();
public void leaderDies();  // ¿Unidad queda confundida?
```

### 2.3 Spell (Hechizos)

**Atributos Base:**
```java
private String id;              // spell_fireball
private String name;            // "Bola de Fuego"
private int spellLevel;         // 0-9 (Cantrip=0)
private SpellType type;         // DAMAGE, CONTROL, SUPPORT, INSPIRATION
private int saveDC;             // CD base
private Ability saveAbility;    // DEX, CON, WIS, etc para TS
private String description;     // Descripción del efecto
```

**AreaSpell (Daño):**
```java
private int areaRadius;         // Radio en pies
private int damageOnFail;       // Daño PF si falla TS
private int damageOnSuccess;    // Daño PF si pasa TS (típicamente mitad)
```

**SupportSpell (Sostenimiento):**
```java
private int healthRestoration;  // 1d4 PF recuperados
private int buffValue;          // Bonificador (+1d4)
private int buffDuration;       // Turnos que dura
```

**InspirationSpell (Bardo):**
```java
private int inspirationDieSize; // d6, d8, d10, d12
private String affectsRolls[];  // {"attack", "damage"}, etc
```

**Métodos:**
```java
public void executeAgainstUnit(CombatUnit target, Character caster, BattleState state);
public int calculateDC(Character caster);
public int calculateDamage(Character caster, boolean failedSave);
public boolean canCast(Character caster);
public void consumeSpellSlot(Character caster);
```

### 2.4 HeroicReaction (Reacciones Especiales)

**Estructura común:**
```java
public interface HeroicReaction {
    String getName();
    ReactionType getType();
    boolean canActivate(Character user, BattleState state);
    void activate(Character user, CombatUnit targetUnit);
    void reset();  // Al inicio de ronda
}
```

**TacticalRetreat (Pícaro/Monje/Guardabosques):**
```java
// Efecto: Movimiento gratuito 50% velocidad SIN Ataque de Oportunidad
// Costo: 1 Reacción
public void activate(Character user, CombatUnit targetUnit) {
    // user puede moverse hasta 50% de su velocidad
    // Evita AoO de targetUnit
}
```

**WhirlwindSteel (Guerrero/Bárbaro/Paladín):**
```java
// Efecto: Ataque inmediato -1 PF a unidad, frena movimiento
// Si crítico: Unidad Aturdida 1 turno
// Costo: 1 Reacción
public void activate(Character user, CombatUnit targetUnit) {
    AttackResult attack = user.rollAttack();
    if (attack.hits()) {
        targetUnit.takePFDamage(1);
        targetUnit.stopMovement();
        if (attack.isCritical()) {
            targetUnit.applyMoraleEffect(STUNNED);
        }
    }
}
```

**RepulsionShield (Mago/Clérigo/Brujo):**
```java
// Efecto: TS Fuerza contra CD hechizo caster
// Si falla: Push 10 pies atrás, pierde acción
// Si pasa: Nada
// Costo: 1 Reacción + 1 Slot de hechizo nivel 1+
public void activate(Character user, CombatUnit targetUnit) {
    int saveDC = user.getSpellSaveDC();
    if (!targetUnit.makeSaveThrow(Ability.STRENGTH, saveDC)) {
        targetUnit.push(10);  // 10 pies atrás
        targetUnit.loseAction();
    }
}
```

---

## 3. Especificaciones de Combate (Wargame Scale)

### 3.1 DiceRoller (Sistema de Dados D&D)

```java
public class DiceRoller {
    /**
     * Tira un dado de N caras
     * @param sides Número de caras del dado (4, 6, 8, 10, 12, 20, 100)
     * @return Resultado entre 1 y sides inclusive
     */
    public static int roll(int sides);
    
    /**
     * Tira múltiples dados y suma
     * @param numDice Cantidad de dados (ej. 3)
     * @param sides Caras por dado (ej. 6)
     * @return Suma total de todas las tiradas
     * Ejemplo: rollMultiple(3, 6) = tirada de 3d6
     */
    public static int rollMultiple(int numDice, int sides);
    
    /**
     * Tirada d20 estándar de D&D
     * @return Resultado entre 1 y 20
     */
    public static int d20();
    
    /**
     * Tirada d20 con Ventaja
     * @return Mayor de dos d20
     */
    public static int d20Advantage();
    
    /**
     * Tirada d20 con Desventaja
     * @return Menor de dos d20
     */
    public static int d20Disadvantage();
    
    /**
     * Generalizado: Multiple dice a la vez
     * @param formula String formato "3d6+5" o "2d12"
     * @return Resultado de la tirada
     */
    public static int rollFormula(String formula);
}
```

### 3.2 CombatResolver (Resolución de Combate)

```java
public class CombatResolver {
    /**
     * Resuelve un ataque entre combatientes
     * @param attacker Quién ataca
     * @param target Quién recibe el ataque
     * @param modifyingBonus Bonificador adicional (Bono de Masa, etc)
     * @return AttackResult (hit/miss, roll total)
     */
    public AttackResult resolveAttack(
        Combatant attacker,
        Combatant target,
        int modifyingBonus
    );
    
    /**
     * Calcula daño después de ataque exitoso
     * @param attacker Atacante
     * @param target Defensor
     * @param attackResult Resultado del ataque
     * @return Cantidad de daño a aplicar
     */
    public int calculateDamage(
        Combatant attacker,
        Combatant target,
        AttackResult attackResult
    );
    
    /**
     * Aplica daño al objetivo
     * @param target Quien recibe daño
     * @param damage Cantidad de daño
     * @param damageType PF (para unidades) o HP (para personajes)
     */
    public void applyDamage(
        Combatant target,
        int damage,
        DamageType damageType
    );
    
    /**
     * Resuelve tirada de salvación
     * @param defender Quien debe salvar
     * @param saveDC CD de la salvación
     * @param ability Qué habilidad (DEX, CON, STR, etc)
     * @return true si pasa, false si falla
     */
    public boolean rollSaveThrow(
        CombatUnit defender,
        int saveDC,
        Ability ability
    );
    
    /**
     * Calcula daño de hechizo vs unidad
     * @param caster Quien lanza el hechizo
     * @param spell El hechizo
     * @param failedSave Si la unidad falló la TS
     * @return Daño PF que sufre la unidad
     */
    public int calculateSpellDamage(
        Character caster,
        Spell spell,
        boolean failedSave
    );
}
```

### 3.3 MassBonus (Sistema de Bonificador de Masa)

```java
public class MassBonus {
    /**
     * Calcula el bonificador por ventaja numérica
     * Fórmula: +1 por cada 5 PF completos
     * Ejemplos:
     *   1-4 PF = +0
     *   5-9 PF = +1
     *   10-14 PF = +2
     *   15-19 PF = +3
     *
     * @param currentPF Puntos de Fuerza actuales de la unidad
     * @return Bonificador a sumar a tirada de ataque
     */
    public static int calculate(int currentPF);
    
    /**
     * Daño adicional vs personaje por masa
     * @param massBonus Bonificador de masa calculado
     * @param baseUnitDamage Daño base de criatura
     * @return Daño total = baseUnit + massBonus
     */
    public static int getDamageBonus(int massBonus);
}
```

### 3.4 InitiativeTracker (Seguimiento de turnos)

```java
public class InitiativeTracker {
    /**
     * Ordena combatientes por iniciativa
     * Todos tiran: d20 + DEX modifier
     * @param combatants Todos los participantes (Character + CombatUnit)
     * @return Lista ordenada por iniciativa (mayor primero)
     */
    public List<Combatant> determineOrder(List<Combatant> combatants);
    
    /**
     * Obtiene siguiente combatiente en orden
     * @return Siguiente en la iniciativa
     */
    public Combatant getNextCombatant();
    
    /**
     * Avanza a siguiente ronda
     * Reseta reacciones, buffs temporales, etc
     */
    public void nextRound();
}
```

---

## 4. Especificaciones de API Pública (Wargame Manager)

### 4.1 WargameManager (Gestor Principal)

```java
public class WargameManager {
    // Inicialización
    public void startNewBattle(List<Character> players, List<CombatUnit> enemies);
    public void loadBattle(String savePath);
    
    // Flujo de batalla
    public void executeRound();
    public void attemptAction(Combatant actor, CombatAction action);
    public void endBattle();
    
    // Estado
    public BattleState getCurrentBattleState();
    public boolean isBattleActive();
    public void pauseBattle();
    public void resumeBattle();
    
    // Persistencia
    public void saveBattle(String savePath);
    public List<String> listSavedBattles();
}
```

### 4.2 Enums y Constantes

```java
enum CharacterClass {
    WARRIOR,     // Extra Attack, Whirlwind Steel
    WIZARD,      // Area Spells, Repulsion Shield
    CLERIC,      // Support Spells, Repulsion Shield
    BARD,        // Inspiration Spells, Tactical Retreat
    ROGUE        // Tactical Retreat, sneak attack
}

enum SpellType {
    DAMAGE,      // Bola de Fuego, Trueno
    CONTROL,     // Muro de Hielo, Muro de Fuego
    SUPPORT,     // Sanación, Bendición
    INSPIRATION  // Inspiración de Batalla
}

enum ReactionType {
    TACTICAL,    // Repliegue
    OFFENSIVE,   // Torbellino
    MAGICAL      // Escudo
}

enum MoraleEffect {
    NONE,        // Normal
    FRIGHTENED,  // -1 ataque, velocidad reducida
    CONFUSED,    // Puede atacar aliados
    STUNNED      // Pierde acción próximo turno
}

enum DamageType {
    HP,          // Daño a personaje (Puntos de Vida)
    PF           // Daño a unidad (Puntos de Fuerza)
}

enum Ability {
    STRENGTH,     // STR
    DEXTERITY,    // DEX
    CONSTITUTION, // CON
    INTELLIGENCE, // INT
    WISDOM,       // WIS
    CHARISMA      // CHA
}
```

---

## 5. Convenciones de Datos

### 5.1 Atributos de D&D (Ability Scores)
- Rango válido: 1-20 (típicamente 8-15 en creación)
- Modificador = (valor - 10) / 2
- Ejemplos:
  - 8 = -1 mod
  - 10 = 0 mod
  - 12 = +1 mod
  - 16 = +3 mod

### 5.2 Sistema de Bonos/Penales
- Todos los bonos/penales se aplican como modificadores enteros
- Se suman a tiradas específicas (ataque, defensa, tirada de salvación)
- Ej: +1 a tirada de ataque, +2 a CA, +3 a daño

### 5.3 Puntos de Fuerza (PF)
- 1 PF = 1 criatura individual
- Rango típico: 1-50 PF por unidad
- Se reduce por daño: 1 PF perdido = 1 criatura muere

### 5.4 Clase de Armadura (CA)
- Rango típico: 10-20
- Se compara contra d20 + bonificadores
- Si d20 + bonus >= CA: Impacto

---

## 6. Compilación y Ejecución

### 6.1 Compilación
```powershell
# Compilar todo
javac -d bin src/com/dnd/wargames/**/*.java

# O usando task de VS Code
# Ctrl+Shift+B -> Compile WargameProject
```

### 6.2 Ejecución
```powershell
java -cp bin com.dnd.wargames.WargameManager

# O desde VS Code
# Ctrl+Shift+B -> Run WargameProject
```

### 6.3 GitIgnore
```
bin/
*.class
*.log
.DS_Store
saves/
*.swp
.idea/
.classpath
.project
```

---

## 7. Checklist de Desarrollo

Cada clase debe:
- [ ] Tener JavaDoc completo
- [ ] Seguir naming conventions (PascalCase clases, camelCase métodos)
- [ ] Sin código duplicado (aplicar DRY)
- [ ] Máximo 100 líneas por método (idealmente 20-40)
- [ ] Sin System.out directos (usar Logger)
- [ ] Constantes en UPPER_SNAKE_CASE
- [ ] Métodos pequeños y enfocados en una responsabilidad
- [ ] Manejo de excepciones apropiado
- [ ] 1 clase por archivo .java

---

## Versión
- v2.0 - Wargame Scale - Actualizado: 2026-02-08
- v1.0 - Definido: 2026-02-08
