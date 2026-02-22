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
        if (!attacker.isAlive() || !defender.isAlive()) {
            return new AttackResult(false, 0, 0, 0);
        }

        // Calcular número de ataques disponibles por formación
        int attacksAvailable = attacker.getAttacksAvailable();
        int totalDamage = 0;
        int hits = 0;

        System.out.println("🎲 " + attacker.getName() + " ataca a " + defender.getName());
        System.out.println("   Ataques disponibles: " + attacksAvailable + 
                          " (Frente " + attacker.getFrontWidth() + 
                          " × Filas " + attacker.getRowsAttacking() + ")");

        for (int i = 0; i < attacksAvailable; i++) {
            // Tirada de ataque: d20 + attack bonus
            int attackRoll = DiceRoller.rollD20() + attacker.getBaseAttackBonus();

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

        if (hits > 0) {
            System.out.println("   ✅ " + hits + " impactos! " + totalDamage + " HP de daño total.");
            defender.takeDamage(totalDamage);
        } else {
            System.out.println("   ❌ Todos los ataques fallaron!");
        }

        return new AttackResult(hits > 0, totalDamage, hits, attacksAvailable);
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

        System.out.println("⚔️  " + attacker.getName() + " ataca a " + defender.getName() + " (con ventaja)");
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
            defender.takeDamage(damage);

            return new AttackResult(true, damage, 1, 1);
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

        // Calcular número de ataques disponibles por formación
        int attacksAvailable = attacker.getAttacksAvailable();
        int totalDamage = 0;
        int hits = 0;

        System.out.println("👥 " + attacker.getName() + " ataca a " + defender.getName());
        System.out.println("   Ataques disponibles: " + attacksAvailable);

        for (int i = 0; i < attacksAvailable; i++) {
            int attackRoll = DiceRoller.rollD20() + attacker.getBaseAttackBonus();

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

    /**
     * Clase interna para representar el resultado de un ataque.
     */
    public static class AttackResult {
        public final boolean hit;
        public final int damage;
        public final int hits;
        public final int totalAttacks;

        public AttackResult(boolean hit, int damage, int hits, int totalAttacks) {
            this.hit = hit;
            this.damage = damage;
            this.hits = hits;
            this.totalAttacks = totalAttacks;
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