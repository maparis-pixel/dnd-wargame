package com.dnd.wargames.test;

import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.*;

/**
 * Test básico para multi-ataques por formación y combat resolver.
 *
 * @author Lead Developer
 * @version 3.0
 */
public class BasicTest {

    public static void main(String[] args) {
        System.out.println("=== BasicTest v3.0: Multi-ataques y Combate ===\n");

        boolean allTestsPassed = true;

        // Test 1: Crear unidades HP system
        System.out.println("Test 1: Creación de unidades HP system");
        try {
            CombatUnit guardias = UnitFactory.createHumanGuards(10);
            CombatUnit orcos = UnitFactory.createOrcs(8);

            assert guardias.getHitPoints() == 10 * 11 : "Guardias deberían tener 110 HP";
            assert guardias.getArmorClass() == 16 : "Guardias deberían tener CA 16";
            assert guardias.getStrength() == 13 : "Guardias deberían tener STR 13";
            assert guardias.getMorale() == 8 : "Guardias deberían tener Moral 8 (Warhammer)";
            
            assert orcos.getHitPoints() == 8 * 15 : "Orcos deberían tener 120 HP";
            assert orcos.getBaseAttackBonus() == 5 : "Orcos deberían tener +5 al ataque";
            assert orcos.getBaseDamage() == 9 : "Orcos deberían hacer 9 daño";
            assert orcos.getMorale() == 7 : "Orcos deberían tener Moral 7 (Warhammer)";

            System.out.println("✅ Unidades creadas correctamente (HP system)");
        } catch (Exception e) {
            System.out.println("❌ Error creando unidades: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        // Test 2: Sistema de dados 2d6
        System.out.println("\nTest 2: Sistema de dados (D&D + Warhammer)");
        try {
            int roll1 = DiceRoller.rollD20();
            int roll2 = DiceRoller.roll2D6();
            int roll3 = DiceRoller.rollD6();

            assert roll1 >= 1 && roll1 <= 20 : "d20 debería estar entre 1-20";
            assert roll2 >= 2 && roll2 <= 12 : "2d6 debería estar entre 2-12";
            assert roll3 >= 1 && roll3 <= 6 : "d6 debería estar entre 1-6";

            System.out.println("✅ Sistema de dados funciona correctamente");
            System.out.println("   d20: " + roll1 + ", 2d6: " + roll2 + ", d6: " + roll3);
        } catch (Exception e) {
            System.out.println("❌ Error en sistema de dados: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        // Test 3: Multi-ataques por formación
        System.out.println("\nTest 3: Multi-ataques por formación");
        try {
            CombatUnit atacante = UnitFactory.createHumanGuards(15); // Formación más grande
            CombatUnit defensor = UnitFactory.createOrcs(10);

            int attacksAvailable = atacante.getAttacksAvailable();
            int expectedAttacks = atacante.getFrontWidth() * atacante.getRowsAttacking();
            
            assert attacksAvailable == expectedAttacks : "Ataques disponibles inconsistente";
            assert attacksAvailable > 1 : "Deberían haber múltiples ataques";

            System.out.println("✅ Multi-ataques configurados: " + attacksAvailable + " ataques");
            System.out.println("   Formación: Frente " + atacante.getFrontWidth() + ", Filas " + atacante.getRowsAttacking());
        } catch (Exception e) {
            System.out.println("❌ Error en multi-ataques: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        // Test 4: Combate con multi-ataques
        System.out.println("\nTest 4: Sistema de combate multi-ataques");
        try {
            CombatUnit atacante = UnitFactory.createHumanGuards(12);
            CombatUnit defensor = UnitFactory.createOrcs(10);

            int hpBefore = defensor.getHitPoints();
            CombatResolver.AttackResult result = CombatResolver.resolveUnitVsUnit(atacante, defensor);
            int hpAfter = defensor.getHitPoints();

            assert result.totalAttacks > 1 : "Deberían ejecutarse múltiples ataques";
            
            if (result.hits > 0) {
                assert hpAfter < hpBefore : "Defensor debería perder HP";
                System.out.println("✅ Combate ejecutado: " + result.hits + "/" + result.totalAttacks + " impactos, " + result.damage + " HP daño");
            } else {
                System.out.println("✅ Combate ejecutado: todos los ataques fallaron (válido)");
            }
        } catch (Exception e) {
            System.out.println("❌ Error en sistema de combate: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        // Test 5: Motor de batalla
        System.out.println("\nTest 5: Motor de batalla");
        try {
            WargameBattleEngine battle = new WargameBattleEngine();

            CombatUnit team1Unit = UnitFactory.createHumanGuards(5);
            CombatUnit team2Unit = UnitFactory.createOrcs(5);

            battle.addToTeam1(team1Unit);
            battle.addToTeam2(team2Unit);

            assert battle.getTeam1Units().size() == 1 : "Equipo 1 debería tener 1 unidad";
            assert battle.getTeam2Units().size() == 1 : "Equipo 2 debería tener 1 unidad";

            System.out.println("✅ Motor de batalla inicializado correctamente");

        } catch (Exception e) {
            System.out.println("❌ Error en motor de batalla: " + e.getMessage());
            e.printStackTrace();
            allTestsPassed = false;
        }

        // Resumen final
        System.out.println("\n================================================");
        if (allTestsPassed) {
            System.out.println("✅ TODOS LOS TESTS PASARON");
        } else {
            System.out.println("❌ ALGUNOS TESTS FALLARON");
            System.exit(1);
        }
    }
}