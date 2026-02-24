package com.dnd.wargames.test;

import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.DiceRoller;
import com.dnd.wargames.web.WebBattleServer;

/**
 * Smoke test para validar moral Warhammer 2d6 y disponibilidad del modo web.
 * @version 3.2
 */
public class MoraleAndWebSmokeTest {

    public static void main(String[] args) {
        System.out.println("=== MORALE 2D6 + WEB SMOKE TEST v3.2 ===\n");

        boolean passed = true;

        // Test 1: Moral Warhammer 2d6
        try {
            System.out.println("Test 1: Moral Warhammer 2d6");
            CombatUnit guards = UnitFactory.createHumanGuards(10);
            
            // Validar valor de moral Warhammer
            int morale = guards.getMorale();
            if (morale < 2 || morale > 12) {
                throw new IllegalStateException("Moral fuera de rango Warhammer (2-12): " + morale);
            }
            
            // Tirar 2d6 y validar rango
            int roll = DiceRoller.roll2D6();
            if (roll < 2 || roll > 12) {
                throw new IllegalStateException("2d6 fuera de rango: " + roll);
            }
            
            // Chequeo de moral
            boolean moraleCheck = guards.checkMorale(roll);
            System.out.println("   Moral = " + morale + ", 2d6 = " + roll + ", resultado = " + (moraleCheck ? "Pasa" : "Huye"));

            System.out.println("✅ Sistema de moral Warhammer 2d6 OK");
        } catch (Exception e) {
            passed = false;
            System.out.println("❌ Error en moral Warhammer: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 2: Rota al 50% y reagrupamiento unico
        try {
            System.out.println("\nTest 2: Rota al 50% y reagrupamiento");
            CombatUnit guards = UnitFactory.createHumanGuards(10);
            int breakDamage = guards.getMaxHitPoints() / 2;
            guards.takeDamage(breakDamage);

            if (!guards.hasBrokenByHalfLoss() || !guards.hasFledBattle()) {
                throw new IllegalStateException("La unidad deberia estar Rota y en retirada");
            }
            if (!guards.canAttemptRegroup()) {
                throw new IllegalStateException("Deberia permitir reagrupamiento unico");
            }

            boolean regrouped = guards.attemptRegroup(guards.getMorale());
            if (!regrouped || guards.hasFledBattle()) {
                throw new IllegalStateException("La unidad deberia reagruparse correctamente");
            }

            System.out.println("✅ Rota al 50% y reagrupamiento OK");
        } catch (Exception e) {
            passed = false;
            System.out.println("❌ Error en Rota/reagrupamiento: " + e.getMessage());
            e.printStackTrace();
        }

        // Test 3: Web instantiation
        try {
            System.out.println("\nTest 3: Instanciación WebBattleServer");
            WebBattleServer server = new WebBattleServer();
            if (server == null) {
                throw new IllegalStateException("No se pudo instanciar WebBattleServer");
            }
            System.out.println("✅ Modo web disponible (instanciación correcta)");
        } catch (Exception e) {
            passed = false;
            System.out.println("❌ Error en validación web: " + e.getMessage());
            e.printStackTrace();
        }

        if (passed) {
            System.out.println("\n=== SMOKE TEST OK ===");
        } else {
            System.out.println("\n=== SMOKE TEST CON FALLOS ===");
            System.exit(1);
        }
    }
}
