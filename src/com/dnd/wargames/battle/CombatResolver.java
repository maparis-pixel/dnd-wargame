package com.dnd.wargames.battle;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;

/**
 * Resuelve ataques y daño en el combate.
 * Maneja la lógica de combate entre unidades y personajes.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class CombatResolver {

    /**
     * Resuelve un ataque de Unidad contra Unidad.
     *
     * @param attacker Unidad atacante
     * @param defender Unidad defensora
     * @return Resultado del ataque
     */
    public static AttackResult resolveUnitVsUnit(CombatUnit attacker, CombatUnit defender) {
        // Tirada de ataque: d20 + attack bonus + mass bonus
        int moraleMod = attacker.getMoraleAttackModifier();
        int attackRoll = DiceRoller.rollD20(attacker.getBaseAttackBonus() + attacker.getMassBonus() + moraleMod);

        System.out.println("🎲 " + attacker.getName() + " ataca a " + defender.getName());
        System.out.println("   Tirada: " + (attackRoll - attacker.getBaseAttackBonus() - attacker.getMassBonus() - moraleMod) +
                          " + " + attacker.getBaseAttackBonus() + " (bonus) + " +
                  attacker.getMassBonus() + " (masa) + " + moraleMod + " (moral) = " + attackRoll);
        System.out.println("   CA objetivo: " + defender.getArmorClass());

        if (attackRoll >= defender.getArmorClass()) {
            // Impacto exitoso - 1 PF de daño base
            int damage = 1;

            // Regla de Hender: si supera CA por 5+, +1 PF adicional
            if (attackRoll >= defender.getArmorClass() + 5) {
                damage += 1;
                System.out.println("   ¡Golpe crítico! Daño extra por hender.");
            }

            System.out.println("   ✅ Impacto! " + damage + " PF de daño.");
            defender.takeDamage(damage);

            return new AttackResult(true, damage, attackRoll);
        } else {
            System.out.println("   ❌ Fallo!");
            return new AttackResult(false, 0, attackRoll);
        }
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
        // Personaje tiene ventaja contra unidades (tira 2d20, toma el mayor)
        int attackRoll = DiceRoller.rollD20WithAdvantage(attacker.getAttackBonus());

        System.out.println("⚔️  " + attacker.getName() + " ataca a " + defender.getName() + " (con ventaja)");
        System.out.println("   Tirada: " + (attackRoll - attacker.getAttackBonus()) +
                          " + " + attacker.getAttackBonus() + " (bonus) = " + attackRoll);
        System.out.println("   CA objetivo: " + defender.getArmorClass());

        if (attackRoll >= defender.getArmorClass()) {
            // Impacto exitoso - 1 PF de daño base
            int damage = 1;

            // Regla de Hender: si supera CA por 5+, +1 PF adicional
            if (attackRoll >= defender.getArmorClass() + 5) {
                damage += 1;
                System.out.println("   ¡Golpe crítico! Daño extra por hender.");
            }

            System.out.println("   ✅ Impacto! " + damage + " PF de daño.");
            defender.takeDamage(damage);

            return new AttackResult(true, damage, attackRoll);
        } else {
            System.out.println("   ❌ Fallo!");
            return new AttackResult(false, 0, attackRoll);
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
        // Unidad ataca con bono de ataque + bono de masa
        int moraleMod = attacker.getMoraleAttackModifier();
        int attackRoll = DiceRoller.rollD20(attacker.getBaseAttackBonus() + attacker.getMassBonus() + moraleMod);

        System.out.println("👥 " + attacker.getName() + " ataca a " + defender.getName());
        System.out.println("   Tirada: " + (attackRoll - attacker.getBaseAttackBonus() - attacker.getMassBonus() - moraleMod) +
                          " + " + attacker.getBaseAttackBonus() + " (bonus) + " +
                  attacker.getMassBonus() + " (masa) + " + moraleMod + " (moral) = " + attackRoll);
        System.out.println("   CA objetivo: " + defender.getArmorClass());

        if (attackRoll >= defender.getArmorClass()) {
            // Impacto exitoso - daño a HP del personaje
            int damage = attacker.getBaseDamage();
            System.out.println("   ✅ Impacto! " + damage + " puntos de daño.");
            defender.takeDamage(damage);

            return new AttackResult(true, damage, attackRoll);
        } else {
            System.out.println("   ❌ Fallo!");
            return new AttackResult(false, 0, attackRoll);
        }
    }

    /**
     * Clase interna para representar el resultado de un ataque.
     */
    public static class AttackResult {
        public final boolean hit;
        public final int damage;
        public final int attackRoll;

        public AttackResult(boolean hit, int damage, int attackRoll) {
            this.hit = hit;
            this.damage = damage;
            this.attackRoll = attackRoll;
        }

        @Override
        public String toString() {
            return hit ? "Impacto (" + damage + " daño)" : "Fallo";
        }
    }
}