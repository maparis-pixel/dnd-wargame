package com.dnd.wargames.battle;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;

/**
 * Resuelve ataques y daño en el combate.
 * Sistema Warhammer con HP, formación y multi-ataques.
 *
 * @author Lead Developer
 * @version 3.0
 */
public class CombatResolver {

    /**
     * Resuelve un ataque de Unidad contra Unidad con sistema de formación.
     * Los ataques disponibles dependen de: frontWidth * rowsAttacking
     *
     * @param attacker Unidad atacante
     * @param defender Unidad defensora
     * @return Resultado del ataque
     */
    public static AttackResult resolveUnitVsUnit(CombatUnit attacker, CombatUnit defender) {
        return resolveUnitVsUnitInternal(attacker, defender, false);
    }

    public static AttackResult resolveUnitVsUnitPursuit(CombatUnit attacker, CombatUnit defender) {
        return resolveUnitVsUnitInternal(attacker, defender, true);
    }

    private static AttackResult resolveUnitVsUnitInternal(CombatUnit attacker, CombatUnit defender, boolean allowFleeingDefender) {
        if (!attacker.isAlive()) {
            return new AttackResult(false, 0, 0, 0);
        }
        if (defender.getHitPoints() <= 0) {
            return new AttackResult(false, 0, 0, 0);
        }
        if (!allowFleeingDefender && !defender.isAlive()) {
            return new AttackResult(false, 0, 0, 0);
        }

        CombatUnit.FormationProfile attackerFormation =
                attacker.getFormationProfileAgainst(defender.getFrontWidth());

        int attacksAvailable = attackerFormation.attacksAvailable;
        int attackBonusByRows = attackerFormation.rowAttackBonus;
        int totalDamage = 0;
        int hits = 0;

        System.out.println("🎲 " + attacker.getBattleDisplayName() + " ataca a " + defender.getBattleDisplayName());
        System.out.println("   Ataques disponibles: " + attacksAvailable + 
                          " (Frente " + attackerFormation.effectiveFrontWidth + 
                          " × Filas " + attackerFormation.occupiedAttackingRows +
                          "/" + attackerFormation.maxAttackRows + ")");
        if (attackBonusByRows > 0) {
            System.out.println("   Bono por filas: +" + attackBonusByRows);
        }

        for (int i = 0; i < attacksAvailable; i++) {
            // Tirada de ataque: d20 + attack bonus
            int attackRoll = DiceRoller.rollD20() + attacker.getBaseAttackBonus() + attackBonusByRows;

            if (attackRoll >= defender.getArmorClass()) {
                // Impacto exitoso - daño base del atacante
                int damage = attacker.getBaseDamage();

                // Regla de Hender: si supera CA por 5+, daño × 1.5
                if (attackRoll >= defender.getArmorClass() + 5) {
                    damage = (int) (damage * 1.5);
                }

                totalDamage += damage;
                hits++;
            }
        }

        boolean moraleChecked = false;
        boolean moralePassed = true;
        int moraleRoll = 0;

        if (hits > 0) {
            System.out.println("   ✅ " + hits + " impactos! " + totalDamage + " HP de daño total.");
            boolean hadStandardBeforeDamage = defender.hasStandardBearer();
            defender.takeDamage(totalDamage);

            if (!allowFleeingDefender) {
                MoraleResolution moraleResolution = resolveMoraleIfNeeded(defender, hadStandardBeforeDamage);
                moraleChecked = moraleResolution.checked;
                moralePassed = moraleResolution.passed;
                moraleRoll = moraleResolution.roll;

                if (moraleResolution.forcedBreak) {
                    System.out.println("   [MORAL] " + defender.getBattleDisplayName() + " se rompe al 50% y se retira.");
                } else if (moraleChecked) {
                    System.out.println("   [MORAL] Chequeo (2d6): " + moraleRoll +
                            " vs " + defender.getMorale() + " -> " + (moralePassed ? "PASA" : "FALLA"));
                    if (!moralePassed) {
                        System.out.println("   [MORAL] " + defender.getBattleDisplayName() + " huye del combate.");
                    }
                }
            }
        } else {
            System.out.println("   ❌ Todos los ataques fallaron!");
        }

        return new AttackResult(
                hits > 0,
                totalDamage,
                hits,
                attacksAvailable,
                moraleChecked,
                moralePassed,
                moraleRoll,
                defender.hasFledBattle()
        );
    }

    /**
     * Resuelve un ataque de Personaje contra Unidad.
     * Personajes tienen ventaja contra unidades.
     *
     * @param attacker Personaje atacante
     * @param defender Unidad defensora
     * @return Resultado del ataque
     */
    public static AttackResult resolveCharacterVsUnit(Character attacker, CombatUnit defender) {
        if (!defender.isAlive()) {
            return new AttackResult(false, 0, 0, 1);
        }

        // Personaje tiene ventaja contra unidades (tira 2d6, toma el mayor)
        int attackRoll = DiceRoller.rollD20WithAdvantage(attacker.getAttackBonus());

        System.out.println("⚔️  " + attacker.getName() + " ataca a " + defender.getBattleDisplayName() + " (con ventaja)");
        System.out.println("   Tirada: " + (attackRoll - attacker.getAttackBonus()) +
                          " + " + attacker.getAttackBonus() + " (bonus) = " + attackRoll);
        System.out.println("   CA objetivo: " + defender.getArmorClass());

        if (attackRoll >= defender.getArmorClass()) {
            // Impacto exitoso - daño del personaje (basado en attackBonus + STR)
            int damage = attacker.getAttackBonus() + Math.max(1, attacker.getAbilityModifier(attacker.getStrength()));

            // Regla de Hender: si supera CA por 5+, daño × 1.5
            if (attackRoll >= defender.getArmorClass() + 5) {
                damage = (int) (damage * 1.5);
                System.out.println("   ¡Golpe crítico! Daño extra por hender.");
            }

            System.out.println("   ✅ Impacto! " + damage + " HP de daño.");
            boolean hadStandardBeforeDamage = defender.hasStandardBearer();
            defender.takeDamage(damage);

            MoraleResolution moraleResolution = resolveMoraleIfNeeded(defender, hadStandardBeforeDamage);
            if (moraleResolution.forcedBreak) {
                System.out.println("   [MORAL] " + defender.getBattleDisplayName() + " se rompe al 50% y se retira.");
            } else if (moraleResolution.checked) {
                System.out.println("   [MORAL] Chequeo (2d6): " + moraleResolution.roll +
                        " vs " + defender.getMorale() + " -> " + (moraleResolution.passed ? "PASA" : "FALLA"));
                if (!moraleResolution.passed) {
                    System.out.println("   [MORAL] " + defender.getBattleDisplayName() + " huye del combate.");
                }
            }

            return new AttackResult(
                    true,
                    damage,
                    1,
                    1,
                    moraleResolution.checked,
                    moraleResolution.passed,
                    moraleResolution.roll,
                    defender.hasFledBattle()
            );
        } else {
            System.out.println("   ❌ Fallo!");
            return new AttackResult(false, 0, 0, 1);
        }
    }

    /**
     * Resuelve un ataque de Unidad contra Personaje.
     *
     * @param attacker Unidad atacante
     * @param defender Personaje defensor
     * @return Resultado del ataque
     */
    public static AttackResult resolveUnitVsCharacter(CombatUnit attacker, Character defender) {
        if (!attacker.isAlive()) {
            return new AttackResult(false, 0, 0, 0);
        }

        CombatUnit.FormationProfile attackerFormation = attacker.getFormationProfileAgainst(1);
        int attacksAvailable = attackerFormation.attacksAvailable;
        int attackBonusByRows = attackerFormation.rowAttackBonus;
        int totalDamage = 0;
        int hits = 0;

        System.out.println("👥 " + attacker.getBattleDisplayName() + " ataca a " + defender.getName());
        System.out.println("   Ataques disponibles: " + attacksAvailable +
                          " (Frente " + attackerFormation.effectiveFrontWidth +
                          ", Filas " + attackerFormation.occupiedAttackingRows +
                          "/" + attackerFormation.maxAttackRows + ")");
        if (attackBonusByRows > 0) {
            System.out.println("   Bono por filas: +" + attackBonusByRows);
        }

        for (int i = 0; i < attacksAvailable; i++) {
            int attackRoll = DiceRoller.rollD20() + attacker.getBaseAttackBonus() + attackBonusByRows;

            if (attackRoll >= defender.getArmorClass()) {
                int damage = attacker.getBaseDamage();
                totalDamage += damage;
                hits++;
            }
        }

        if (hits > 0) {
            System.out.println("   ✅ " + hits + " impactos! " + totalDamage + " puntos de daño.");
            defender.takeDamage(totalDamage);
        } else {
            System.out.println("   ❌ Todos los ataques fallaron!");
        }

        return new AttackResult(hits > 0, totalDamage, hits, attacksAvailable);
    }

    private static MoraleResolution resolveMoraleIfNeeded(CombatUnit defender, boolean hadStandardBeforeDamage) {
        if (!defender.isAlive()) {
            return MoraleResolution.notChecked();
        }

        boolean checkByHalfLoss = defender.shouldCheckMoraleFromHalfLoss();
        boolean checkByStandardLoss = hadStandardBeforeDamage && !defender.hasStandardBearer();

        if (defender.hasBrokenByHalfLoss()) {
            return MoraleResolution.forcedBreak();
        }

        if (!checkByHalfLoss && !checkByStandardLoss) {
            return MoraleResolution.notChecked();
        }

        if (checkByHalfLoss) {
            defender.markHalfLossMoraleChecked();
        }

        int moraleRoll = DiceRoller.roll2D6();
        boolean passed = defender.checkMorale(moraleRoll);
        return new MoraleResolution(true, passed, moraleRoll, false);
    }

    private static class MoraleResolution {
        public final boolean checked;
        public final boolean passed;
        public final int roll;
        public final boolean forcedBreak;

        public MoraleResolution(boolean checked, boolean passed, int roll, boolean forcedBreak) {
            this.checked = checked;
            this.passed = passed;
            this.roll = roll;
            this.forcedBreak = forcedBreak;
        }

        public static MoraleResolution notChecked() {
            return new MoraleResolution(false, true, 0, false);
        }

        public static MoraleResolution forcedBreak() {
            return new MoraleResolution(true, false, 0, true);
        }
    }

    /**
     * Clase interna para representar el resultado de un ataque.
     */
    public static class AttackResult {
        public final boolean hit;
        public final int damage;
        public final int hits;
        public final int totalAttacks;
        public final boolean moraleChecked;
        public final boolean moralePassed;
        public final int moraleRoll;
        public final boolean defenderFled;

        public AttackResult(boolean hit, int damage, int hits, int totalAttacks) {
            this(hit, damage, hits, totalAttacks, false, true, 0, false);
        }

        public AttackResult(boolean hit,
                            int damage,
                            int hits,
                            int totalAttacks,
                            boolean moraleChecked,
                            boolean moralePassed,
                            int moraleRoll,
                            boolean defenderFled) {
            this.hit = hit;
            this.damage = damage;
            this.hits = hits;
            this.totalAttacks = totalAttacks;
            this.moraleChecked = moraleChecked;
            this.moralePassed = moralePassed;
            this.moraleRoll = moraleRoll;
            this.defenderFled = defenderFled;
        }

        @Override
        public String toString() {
            if (hits > 0) {
                return hits + "/" + totalAttacks + " impactos (" + damage + " daño)";
            }
            return "Todos los ataques fallaron";
        }
    }
}