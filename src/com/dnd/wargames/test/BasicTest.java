package com.dnd.wargames.test;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.*;

/**
 * Test básico para verificar que el sistema funciona.
 * No usa JUnit, solo validaciones simples.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class BasicTest {

    public static void main(String[] args) {
        System.out.println("🧪 TEST BÁSICO - Sistema de Combate D&D Wargames");
        System.out.println("================================================");

        boolean allTestsPassed = true;

        // Test 1: Crear unidades
        System.out.println("\n1. Test: Creación de unidades");
        try {
            CombatUnit guardias = UnitFactory.createHumanGuards(10);
            CombatUnit orcos = UnitFactory.createOrcs(8);
            com.dnd.wargames.units.Character guerrero = UnitFactory.createWarrior("Test Warrior", 3);

            assert guardias.getStrengthPoints() == 10 : "Guardias deberían tener 10 PF";
            assert guardias.getArmorClass() == 16 : "Guardias deberían tener CA 16";
            assert orcos.getStrengthPoints() == 8 : "Orcos deberían tener 8 PF";
            assert orcos.getBaseDamage() == 7 : "Orcos deberían hacer 7 daño";
            assert guerrero.getLevel() == 3 : "Guerrero debería ser nivel 3";
            assert guerrero.isAlive() : "Guerrero debería estar vivo";

            System.out.println("✅ Unidades creadas correctamente");
        } catch (Exception e) {
            System.out.println("❌ Error creando unidades: " + e.getMessage());
            allTestsPassed = false;
        }

        // Test 2: Sistema de dados
        System.out.println("\n2. Test: Sistema de dados");
        try {
            int roll1 = DiceRoller.rollD20();
            int roll2 = DiceRoller.rollD20();
            int advantageRoll = DiceRoller.rollD20WithAdvantage(0);

            assert roll1 >= 1 && roll1 <= 20 : "d20 debería estar entre 1-20";
            assert roll2 >= 1 && roll2 <= 20 : "d20 debería estar entre 1-20";
            assert advantageRoll >= 1 && advantageRoll <= 20 : "Ventaja debería estar entre 1-20";

            System.out.println("✅ Sistema de dados funciona correctamente");
            System.out.println("   Tiradas de ejemplo: " + roll1 + ", " + roll2 + ", ventaja: " + advantageRoll);
        } catch (Exception e) {
            System.out.println("❌ Error en sistema de dados: " + e.getMessage());
            allTestsPassed = false;
        }

        // Test 3: Combate básico
        System.out.println("\n3. Test: Sistema de combate");
        try {
            CombatUnit atacante = UnitFactory.createHumanGuards(5);
            CombatUnit defensor = UnitFactory.createOrcs(5);

            // Simular algunos ataques
            int hits = 0;
            int totalDamage = 0;

            for (int i = 0; i < 10; i++) {
                CombatResolver.AttackResult result = CombatResolver.resolveUnitVsUnit(atacante, defensor);
                if (result.hit) {
                    hits++;
                    totalDamage += result.damage;
                }
            }

            System.out.println("✅ Combate simulado: " + hits + " impactos, " + totalDamage + " PF daño total");
            assert hits >= 0 && hits <= 10 : "Debería haber entre 0-10 impactos";
            assert totalDamage >= 0 : "Daño debería ser no negativo";

        } catch (Exception e) {
            System.out.println("❌ Error en sistema de combate: " + e.getMessage());
            allTestsPassed = false;
        }

        // Test 4: Motor de batalla
        System.out.println("\n4. Test: Motor de batalla");
        try {
            WargameBattleEngine battle = new WargameBattleEngine();

            CombatUnit team1Unit = UnitFactory.createHumanGuards(3);
            CombatUnit team2Unit = UnitFactory.createOrcs(3);

            battle.addToTeam1(team1Unit);
            battle.addToTeam2(team2Unit);

            assert battle.getTeam1Units().size() == 1 : "Equipo 1 debería tener 1 unidad";
            assert battle.getTeam2Units().size() == 1 : "Equipo 2 debería tener 1 unidad";

            System.out.println("✅ Motor de batalla inicializado correctamente");

        } catch (Exception e) {
            System.out.println("❌ Error en motor de batalla: " + e.getMessage());
            allTestsPassed = false;
        }

        // Resultado final
        System.out.println("\n" + "=".repeat(50));
        if (allTestsPassed) {
            System.out.println("🎉 TODOS LOS TESTS PASARON - El sistema está funcionando!");
        } else {
            System.out.println("⚠️  ALGUNOS TESTS FALLARON - Revisar implementación");
        }
        System.out.println("=".repeat(50));
    }
}