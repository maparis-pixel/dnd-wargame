# D&D Wargames - Test Leader Specification

**Rol:** Test Lead / QA Lead  
**Responsabilidad:** Estrategia de testing, casos de prueba y validación  
**Versión:** 3.1 (Warhammer HP System + Front/Rows Initiative Update)

---

## 1. Estrategia de Testing

### 1.1 Pirámi de Testing
```
        UI Tests (5%)
      Integration Tests (15%)
      Unit Tests (80%)
```

### 1.2 Cobertura Objetivos
- **Unit Tests**: Mínimo 80% código coverage
- **Integration Tests**: Todos los flujos críticos
- **Smoke Tests**: Validaciones básicas pre-deployment

### 1.3 Gate Obligatorio por Iteración v3.0

En este repositorio, cada iteración debe ejecutar:

1. **Compilación completa** del proyecto: `Compile D&D Wargames` task.
2. **Suite mínima obligatoria** (1 única ejecución completa):
  - `com.dnd.wargames.test.SimpleTest` (HP system, formación básica)
  - `com.dnd.wargames.test.BasicTest` (multi-ataques, moral Warhammer)
  - `com.dnd.wargames.test.MoraleAndWebSmokeTest` (2d6 moral, web instantiation)

Si cualquier punto falla, la iteración no se considera válida.

Cobertura mínima adicional de dominio v3.0:
- Validar que unidades base conserven estadísticas correctas:
  - **HP/criatura**, **AC**, **Ataque**, **Daño**, **Moral Warhammer (2-12)**
  - **Alcance** (5ft estándar, 10ft Ogre)
  - **Formación**: `frontWidth`, `rowsAttacking` (5ft=1, 10ft=2, 15ft=3)
- Validar `getAttacksAvailable()` = frontWidth × rowsAttacking
- Validar `checkMorale(2d6)` con triggers: porta estandarte, 50% HP

Cobertura adicional obligatoria v3.1:
- Validar `getFormationProfileAgainst(enemyFront)`:
  - frente efectivo inicial limitado a `enemyFront + 2`
  - extensión de frente al completar dos filas
  - recálculo tras bajas en el mismo combate
- Validar bono por filas ocupadas: +2 por fila desde la segunda.
- Validar modelo de turno del motor: 1 turno = ronda de aliados + ronda de enemigos.

---

## 2. Unit Testing Specification

### 2.1 Framework & Tools
- **Estado actual en repo**: Tests ejecutables por clase `main` (sin JUnit)
- **Ejecución**: `java -cp src <test-class>`
- **Cobertura mínima funcional**: smoke + validaciones de mecánicas core (HP + formación + moral 2d6)

Nota: La adopción de JUnit puede planificarse como mejora futura, pero la validación actual obligatoria se realiza con tests `main`.

### 2.2 Estructura de Tests (Wargame Scale v3.0)
```
src/test/java/com/dnd/wargames/
├── units/
│   ├── CharacterTest.java
│   ├── CombatUnitTest.java
│   ├── SpecialLeaderTest.java
│   └── UnitStateTest.java
├── battle/
│   ├── WargameBattleEngineTest.java
│   ├── CombatResolverTest.java
│   ├── DiceRollerTest.java
│   ├── MassBonusTest.java
│   └── InitiativeTrackerTest.java
├── spells/
│   ├── SpellTest.java
│   ├── AreaSpellTest.java
│   └── SupportSpellTest.java
├── reactions/
│   ├── TacticalRetreatTest.java
│   ├── WhirlwindSteelTest.java
│   └── RepulsionShieldTest.java
├── morale/
│   ├── MoraleTrackerTest.java
│   └── MoraleCheckTest.java
└── battlefield/
    └── BattlefieldTest.java
```

### 2.3 Naming Convention Tests
- `test<MethodName><Scenario><Expected>`
- Ejemplo: `testCalculateMassBonus_12PF_ShouldReturn2`
- Ejemplo: `testTakePFDamage_UnitWith10PF_Loses3_ShouldBe7`

---

## 3. Test Cases Detallados (Wargame Scale)

## 3. Test Cases Detallados (Wargame Scale)

### 3.1 Character Tests

#### Test Suite: Character Creation
```
✓ testCreateCharacter_ValidAllAttributes_ShouldInstantiate
✓ testCreateCharacter_AllAbilityScores_ShouldBeInRange8to15
✓ testCreateCharacter_NegativeLevel_ShouldThrowException
✓ testCalculateMaxHealth_CON14Level5_ShouldBe(10+2*5=20)
✓ testAssignHeroicReaction_WarriorClass_ShouldGetWhirlwindSteel
✓ testAssignHeroicReaction_WizardClass_ShouldGetRepulsionShield
✓ testAssignHeroicReaction_RogueClass_ShouldGetTacticalRetreat
```

#### Test Suite: Character Ability Modifiers
```
✓ testGetAbilityModifier_Score8_ShouldReturn-1
✓ testGetAbilityModifier_Score10_ShouldReturn0
✓ testGetAbilityModifier_Score12_ShouldReturn1
✓ testGetAbilityModifier_Score16_ShouldReturn3
✓ testGetAbilityModifier_Score20_ShouldReturn5
```

#### Test Suite: Character vs Unit Combat
```
✓ testAttackUnit_RollsWithAdvantage_ShouldUseHigherOfTwo
✓ testAttackUnit_ImpactCausesOnePF_ShouldReduceUnitPF
✓ testAttackUnit_SuperCA_By5Plus_ShouldCauseTwoPF
✓ testExtraAttack_WarriorWithTwoAttacks_ShouldApplyMultiple
✓ testTakeDamage_FromUnit_ShouldReduceHP_NotPF
```

#### Test Suite: Heroic Reactions
```
✓ testHeroicReaction_TwoPerRound_ShouldStartWithTwo
✓ testHeroicReaction_UsedOnce_ShouldDecrementCounter
✓ testHeroicReaction_ExhaustedBoth_ShouldBeUnavailable
✓ testHeroicReaction_ResetNextRound_ShouldBackToTwo
```

### 3.2 CombatUnit Tests

#### Test Suite: CombatUnit Creation
```
✓ testCreateUnit_GoblinUnit15PF_ShouldInstantiate
✓ testCreateUnit_SetMaxPF_ShouldMatchInitialValue
✓ testCreateUnit_InvalidPFNegative_ShouldThrowException
✓ testCreateUnit_AddSpecialLeader_ShouldNotNull
```

#### Test Suite: Puntos de Fuerza (PF)
```
✓ testTakePFDamage_1Point_ShouldReduce1PF
✓ testTakePFDamage_ExceedMaximum_ShouldNotGoBelowZero
✓ testGetCurrentPF_After5Losses_ShouldBe5Less
✓ testGetCurrentPF_WhenZero_ShouldBeDefeated
```

#### Test Suite: Mass Bonus Calculation
```
✓ testCalculateMassBonus_1to4PF_ShouldReturn0
✓ testCalculateMassBonus_5to9PF_ShouldReturn1
✓ testCalculateMassBonus_10to14PF_ShouldReturn2
✓ testCalculateMassBonus_15to19PF_ShouldReturn3
✓ testCalculateMassBonus_25PF_ShouldReturn5
```

#### Test Suite: Unit vs Character Attack
```
✓ testAttackCharacter_D20WithMassBonus_ShouldIncludeBonus
✓ testAttackCharacter_HitRoll_ShouldApplyDamageToHP
✓ testAttackCharacter_DamageFormula_BaseDmg5Unit20PF_Should5+4=9HP
✓ testAttackCharacter_MassBonus2_IncreasesDamageCorrectly
```

#### Test Suite: Durability (for Large Creatures)
```
✓ testDurability_Goblin1To4Hits_NeedsOnlyOnce
✓ testDurability_Ogre_Durability3_NeedsThreeHits
✓ testDurability_GiantDurability5_NeedsSevenHits
✓ testDurability_AccumulatePartialDamage_ShouldCountCorrectly
```

#### Test Suite: Morale & Cohesion
```
✓ testMorale_LosesHalf_ShouldCheckMorale
✓ testMorale_CheckFails_ShouldBecomeFreightened
✓ testMorale_LeaderDies_ShouldForceCheck
✓ testMorale_FreightenedUnit_ShouldHave-1ToAttack
✓ testMorale_ConfusedUnit_MayAttackAllies
```

### 3.3 DiceRoller Tests

#### Test Suite: Single Die Rolls
```
✓ testRoll_d4_ShouldReturnBetween1And4
✓ testRoll_d6_ShouldReturnBetween1And6
✓ testRoll_d8_ShouldReturnBetween1And8
✓ testRoll_d10_ShouldReturnBetween1And10
✓ testRoll_d12_ShouldReturnBetween1And12
✓ testRoll_d20_ShouldReturnBetween1And20
✓ testRoll_d100_ShouldReturnBetween1And100
```

#### Test Suite: Multiple Dice
```
✓ testRollMultiple_1d6_ShouldReturnBetween1And6
✓ testRollMultiple_3d6_ShouldReturnBetween3And18
✓ testRollMultiple_2d12Plus5_ShouldReturnBetween7And29
✓ testRollMultiple_ZeroDice_ShouldReturnZero
✓ testRollMultiple_NegativeDice_ShouldThrowException
```

#### Test Suite: Advantage/Disadvantage
```
✓ testD20Advantage_TwoRolls_ShouldReturnHigher
✓ testD20Advantage_AlwaysHigherThanRegular
✓ testD20Disadvantage_TwoRolls_ShouldReturnLower
✓ testD20Disadvantage_AlwaysLowerThanRegular
```

#### Test Suite: Randomness & Distribution
```
✓ testRoll_d20Million_ShouldHaveUniformDistribution
✓ testRoll_d6Average_1Million_ShouldApproach3Point5
✓ testRoll_NoPattern_ConsecutiveCalls_ShouldDiffer
```

### 3.4 Combat Resolver Tests

#### Test Suite: Attack Resolution
```
✓ testResolveAttack_d20Plus2vsCA13_Roll16Total_ShouldHit
✓ testResolveAttack_d20Plus2vsCA13_Roll13Total_ShouldHit
✓ testResolveAttack_d20Plus2vsCA13_Roll12Total_ShouldMiss
✓ testResolveAttack_RollsNatural20_ShouldCritical
✓ testResolveAttack_RollsNatural1_ShouldFail
```

#### Test Suite: Damage Calculation
```
✓ testCalculateDamage_UnitVsUnit_BasicHit_Should1PF
✓ testCalculateDamage_UnitVsUnit_ExceedCA5Plus_Should2PF
✓ testCalculateDamage_CharacterVsUnit_ShouldBe1PF
✓ testCalculateDamage_UnitVsCharacter_FormulaDmg5Plus2Bonus_Should7
```

#### Test Suite: Save Throws
```
✓ testRollSaveThrow_D20Mod_DEX_vsCD12_Roll11Total_ShouldFail
✓ testRollSaveThrow_D20Mod_STR_vsCD10_Roll15Total_ShouldPass
✓ testRollSaveThrow_NaturalOne_ShouldAlwaysFail
```

### 3.5 Spell Tests

#### Test Suite: Area Spells (Damage)
```
✓ testAreaSpell_FireballLevel3_FailedSave_Should3PFDamage
✓ testAreaSpell_FireballLevel3_PassedSave_Should1PFDamage
✓ testAreaSpell_ValidEffectRadius_30Feet
```

#### Test Suite: Support Spells
```
✓ testSupportSpell_HealingMasiva_Recovers1d4PF
✓ testSupportSpell_Blessing_AddsD4ToAttacks_For3Rounds
✓ testSupportSpell_BlessingExpires_After3Rounds
```

#### Test Suite: Bard Inspiration
```
✓ testBardSpell_Inspiration_SingerRollD8_AddsToAttack
✓ testBardSpell_Inspiration_Unit_CanRepeatFailedAttack
```

### 3.6 Heroic Reaction Tests

#### Test Suite: Tactical Retreat
```
✓ testTacticalRetreat_Rogue_EntersUnitZone_CanMoveFree
✓ testTacticalRetreat_Movement50Percent_OfMaxSpeed
✓ testTacticalRetreat_NoAoO_FromEnteringUnit
```

#### Test Suite: Whirlwind Steel
```
✓ testWhirlwindSteel_Warrior_UnitEnters_RollsAttack
✓ testWhirlwindSteel_Hit_Reduces1PF_StopsMovement
✓ testWhirlwindSteel_Critical_StunnsUnit1Turn
```

#### Test Suite: Repulsion Shield
```
✓ testRepulsionShield_Mage_UnitEnters_STRSave
✓ testRepulsionShield_SaveFails_Push10Feet
✓ testRepulsionShield_SaveFails_LosesAction
✓ testRepulsionShield_SavePass_NothingHappens
✓ testRepulsionShield_Costs1SpellSlot
```

### 3.7 Initiative Tracker Tests

#### Test Suite: Initiative Calculation
```
✓ testInitiative_Character_D20PlusDEX_2Mod_ShouldCalc
✓ testInitiative_Unit_UsesUnitDEX_Plus1_ShouldCalc
```

#### Test Suite: Order Determination
```
✓ testDetermineOrder_MultipleUnits_ShouldBeDescending
✓ testDetermineOrder_Tied_ShouldMaintainOrder
```

#### Test Suite: Round Management
```
✓ testNewRound_ResetsReactions_CharacterBackTo2
✓ testNewRound_ClearsTemporaryEffects_BuffsExpire
```

### 3.3 BattleEngine Tests

#### Test Suite: Battle Initialization
```
✓ testStartBattle_ValidCombatants_ShouldInitialize
✓ testStartBattle_DetermineInitiative_ShouldOrderTurns
✓ testGetCurrentState_AfterStart_ShouldReturnBattle
```

#### Test Suite: Battle Flow
```
✓ testExecuteRound_FirstCombatant_ShouldAct
✓ testExecuteRound_ValidAction_ShouldResolve
✓ testExecuteRound_SecondCombatant_ShouldAct
```

#### Test Suite: Battle End Conditions
```
✓ testBattleEnd_PlayerHealth0_ShouldEndLoss
✓ testBattleEnd_EnemyHealth0_ShouldEndVictory
✓ testBattleEnd_PlayerFlees_ShouldEndFlight
```

### 3.4 Action Tests

#### Test Suite: Attack Action
```
✓ testAttackAction_HitRoll_ShouldRollD20Plus
✓ testAttackAction_Hit_ShouldDealDamage
✓ testAttackAction_Miss_ShouldNoDamage
✓ testAttackAction_CriticalHit_ShouldDouble
```

#### Test Suite: Defend Action
```
✓ testDefendAction_Active_ShouldIncreaseAC
✓ testDefendAction_NextRound_ShouldRemoveBonus
```

---

## 4. Integration Tests (Wargame Scenarios)

### 4.1 Flujo Completo de Batalla Simple
```
✓ testBattle_OneCharacterVsOneUnit_ShouldResolveToEnd
  - Character vs 10 Goblins
  - Battle determina ganador
  - Ambos combatientes reflejan daño correcto
```

### 4.2 Batalla Multi-Unidad
```
✓ testBattle_CharacterAnd2UnitsVs3EnemyUnits_ShouldResolve
  - 2 aliados, 3 enemigos
  - Múltiples rondas
  - Iniciativa mantiene orden correcto
```

### 4.3 Reacciones Heroicas en Contexto
```
✓ testBattle_RogueUsesTacticalRetreat_ShouldEscape
✓ testBattle_WarriorUsesWhirlwindSteel_ShouldDamageUnit
✓ testBattle_MageUsesRepulsionShield_ShouldPush
```

### 4.4 Magia a Escala en Combate
```
✓ testBattle_WizardCastsFireball_3Level_Unit12Goblins_Fails_Should3PFDamage
✓ testBattle_ClericCastsHealing_Recovers2dLosidos1PF
```

### 4.5 Moral y Cohesión Durante Batalla
```
✓ testBattle_UnitLosesHalfHP_ShouldCheckMorale
✓ testBattle_UnitLeaderDies_ShouldBeConfused_Turn1
```

---

## 5. Edge Cases & Validations (Wargame)

### 5.1 Casos Límite Críticos
```
✓ testCharacter_HP=0_ShouldBeDead
✓ testCharacter_HP=MaxHP_ShouldBeFull
✓ testUnit_PF=0_ShouldBeDefeated
✓ testUnit_PF=MaxPF_ShouldBeFull
✓ testAttackRoll_Natural20_ShouldCritical
✓ testAttackRoll_Natural1_ShouldFail
✓ testMassBonus_0PF_ButUnitNotDefeated_ShouldReturn0Bonus
✓ testDurability_AccumulatePartial_3Hits_Durability3_Should1PF
```

### 5.2 Validaciones de Input (Wargame Scale)
```
✓ testCharacterCreation_NullName_ShouldThrow
✓ testCharacterCreation_STRBelow1_ShouldThrow
✓ testCharacterCreation_STRAbove20_ShouldThrow
✓ testCombatUnitCreation_NegativePF_ShouldThrow
✓ testDiceRoll_NegativeSides_ShouldThrow
✓ testDiceRoll_ZeroSides_ShouldThrow
✓ testReaction_TwoUsed_TriesToUseThird_ShouldFail
```

### 5.3 Casos de Conflicto
```
✓ testBattle_BothCombatantsKilled_ShouldBeDraw
✓ testReaction_LastOne_UsedTwice_ShouldFail
✓ testMorale_UnitAlreadyFrightened_NewCheck_ShouldPass_ThenContinue
```

---

## 6. Test Data & Fixtures (Wargame)

### 6.1 Test Fixtures
```java
@BeforeEach
public void setUp() {
    // Personajes típicos
    warrior = new Character("TestWarrior", CharacterClass.WARRIOR);
    warrior.setAbilityScore(Ability.STRENGTH, 16);
    warrior.setAbilityScore(Ability.DEXTERITY, 12);
    warrior.setAbilityScore(Ability.CONSTITUTION, 14);
    
    wizard = new Character("TestWizard", CharacterClass.WIZARD);
    wizard.setAbilityScore(Ability.INTELLIGENCE, 16);
    wizard.setAbilityScore(Ability.DEXTERITY, 14);
    
    // Unidades típicas
    goblins10 = new CombatUnit("Unidad Trasgos", "GOBLIN", 10);
    goblins10.setBaseAttackBonus(2);
    goblins10.setBaseDamage(5);
    
    skeletons20 = new CombatUnit("Unidad Esqueletos", "SKELETON", 20);
    skeletons20.setBaseDamage(6);
    
    // Motor
    battleEngine = new WargameBattleEngine();
}
```

### 6.2 Sample Data Sets (No Magic)
```
// Combatant Test Data
Personaje Guerrero típico:
  STR=16, DEX=12, CON=14, INT=10, WIS=13, CHA=11
  HP=15 (10 + 2+2+2+2+2+2)
  CA=15, Attack Bonus=5

Personaje Mago típico:
  STR=10, DEX=14, CON=12, INT=16, WIS=13, CHA=11
  HP=9 (10 + 1+2+1+3+1+1)
  CA=13, Attack Bonus=2

// Unit Test Data  
Unidad Trasgos (10 PF):
  PF=10, CA=15, Attack Bonus=2, Damage=5
  Mass Bonus=2 (10/5=2)
  Final Attack = d20+2+2 = d20+4

Unidad Ogros (5 PF):
  PF=5, CA=13, Attack Bonus=3, Damage=8
  Durability=3 (cada 3 hits = 1 PF)
  Mass Bonus=1 (5/5=1)
  Final Attack = d20+3+1 = d20+4
```

---

## 7. Success Criteria (Wargame)

### 7.1 Unit Test Success
- ✓ 80%+ code coverage en clases core
- ✓ Todos los tests pasan sin errores
- ✓ Sin warnings en ejecución
- ✓ Ejecución < 5 segundos total

### 7.2 Integration Test Success
- ✓ Batallas simples resuelven correctamente
- ✓ Multi-round battles funcionan
- ✓ Reacciones se aplican apropiadamente
- ✓ Moral afecta unidades correctamente

### 7.3 End-to-End Acceptance Criteria
```
✓ Crear Personaje (stats D&D válidos)
✓ Crear Unidades (PF, stats, CA)
✓ Iniciar Batalla (setup, iniciativa)
✓ Ejecutar Ronda 1 (todos actúan)
✓ Aplicar Daño (PF y HP reducen)
✓ Usar Reacción (Heroica una vez)
✓ Lanzar Hechizo (reducir PF por área)
✓ Chequear Moral (unit pasa o falla)
✓ Fin de Batalla (alguien muere)
```

---

## 8. Regression Testing (Wargame Scale)

### 8.1 Componentes Críticos (Requieren Full Regression)
Cualquier cambio a:
- CombatResolver (cálculo de daño)
- MassBonus (bonificador de masa)
- DiceRoller (tiradas)
- Character combat methods
- CombatUnit PF mechanics
- Initiative ordering
- Morale calculations

### 8.2 Cambios Menores (Quick Tests)
- UI/Presentation
- Logging
- Configuration
- Comments/Documentation

### 8.3 Full Regression Test Suite
```powershell
# Ejecutar todos los unit tests
javac -d bin src/com/dnd/wargames/**/*.java
java -cp bin:lib/junit.jar org.junit.runner.JUnitCore com.dnd.wargames.*Test

# O via build system (cuando exista)
gradlew test

# Con coverage report
gradlew test jacoco
```

---

## 9. Test Execution Plan (Wargame Development)

### 9.1 Durante Desarrollo
```
Before committing code:
1. Run unit tests para tu componente
2. Verify 80%+ coverage
3. Run integration tests si cambió API
4. Asegúrate de pasar pre-commit hooks

Daily (cada dev):
- Correr full test suite al less 1x
```

### 9.2 Pre-Release (Antes de Phase Completion)
```
1. Ejecuta TODOS los unit tests (80%+ coverage requerido)
2. Ejecuta integration tests (al menos 1 complete battle)
3. Valida edge cases listados en sección 5
4. Smoke tests: crear unit, crear personaje, battle simple
5. Performance tests: battle con 20 unidades < 500ms
```

### 9.3 Cada Nueva Fase
```
Fase 1 Complete:
- [ ] All Character tests pass
- [ ] All CombatUnit tests pass
- [ ] DiceRoller tests pass
- [ ] Initiative tests pass

Fase 2 Complete:
- [ ] All Reaction interaction tests pass
- [ ] Character vs Unit asymmetry works
- [ ] All edge cases covered

Fase 3 Complete:
- [ ] Spell tests pass
- [ ] Morale system works
- [ ] Integration with combat

Fase 4 Complete:
- [ ] Battlefield positioning correct
- [ ] Movement calculations accurate
- [ ] Full battle scenarios work
```

---

## 10. Defect Tracking Template (Wargame)

```
BUG-###: [Component] Brief description
Severity: Critical | High | Medium | Low
Status: Open | In Progress | Resolved | Closed
Component: Character | CombatUnit | Spell | Reaction | Battle Engine | Morale

Steps to Reproduce:
1. [Setup battle state]
2. [Execute action]
3. [What incorrectly happened]

Expected Behavior:
[What should have happened]

Actual Behavior:
[What actually happened]

Additional Notes:
- Related test case: [TestClass.methodName]
- Affects phase: [1/2/3/4]
- Environment: JDK 25.0.2, Windows 11
```

---

## 11. Performance Testing Checklist (Wargame)

```
✓ Single Battle Resolution < 500ms
✓ 10 Units Combat Round < 100ms
✓ 20 Units Combat Round < 300ms
✓ 50 Units Combat Round < 1000ms
✓ Spell AOE Calculation < 50ms
✓ Morale Check < 10ms
✓ Initiative Determination < 20ms
✓ No Memory Leaks (battle persistence)
```

---

## Versión
- v2.0 - Wargame Scale - Actualizado: 2026-02-08
- v1.0 - Definido: 2026-02-08

## Changelog Corto
- 2026-02-22: Cobertura QA actualizada para validar frente enemigo +2 y recálculo por bajas.
- 2026-02-22: Añadida validación de bono por filas (+2 por fila desde la segunda).
- 2026-02-22: Validación del modelo de turno por 2 rondas incorporada al gate funcional.
