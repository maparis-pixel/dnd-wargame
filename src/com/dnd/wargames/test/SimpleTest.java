package com.dnd.wargames.test;

import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.DiceRoller;

/**
 * Test simple para validar sistema HP por compañía y formación.
 * 
 * @version 3.0
 */
public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("=== SimpleTest v3.0: HP System + Formación ===\n");

        // Test 1: Crear unidad con HP por compañía
        System.out.println("Test 1: Crear compañía de 10 Orcos");
        CombatUnit orcs = UnitFactory.createOrcs(10);
        int expectedHP = 10 * 15; // 10 orcos × 15 HP cada uno
        
        if (orcs.getHitPoints() != expectedHP) {
            System.out.println("❌ FAIL: HP esperado " + expectedHP + ", obtenido " + orcs.getHitPoints());
            System.exit(1);
        }
        if (orcs.getCreaturesCount() != 10) {
            System.out.println("❌ FAIL: Criaturas esperadas 10, obtenidas " + orcs.getCreaturesCount());
            System.exit(1);
        }
        System.out.println("✅ PASS: HP = " + orcs.getHitPoints() + "/" + orcs.getMaxHitPoints());
        System.out.println("✅ PASS: Criaturas = " + orcs.getCreaturesCount());

        // Test 2: Formación
        System.out.println("\nTest 2: Validar formación");
        int frontWidth = orcs.getFrontWidth();
        int rowsAttacking = orcs.getRowsAttacking();
        int attacksAvailable = orcs.getAttacksAvailable();
        
        if (rowsAttacking != 2) { // Orcos tienen reach 5ft = 2 filas
            System.out.println("❌ FAIL: rowsAttacking esperado 2, obtenido " + rowsAttacking);
            System.exit(1);
        }
        if (attacksAvailable != frontWidth * rowsAttacking) {
            System.out.println("❌ FAIL: attacksAvailable inconsistente");
            System.exit(1);
        }
        System.out.println("✅ PASS: Formación -> Frente " + frontWidth + ", Filas " + rowsAttacking + ", Ataques " + attacksAvailable);

        // Test 3: Daño y recálculo de criaturas
        System.out.println("\nTest 3: Aplicar daño y verificar recálculo");
        int damageDealt = 45; // Matar 3 orcos (45 HP)
        orcs.takeDamage(damageDealt);
        
        int expectedCreatures = 7; // Deberían quedar 7 orcos
        if (orcs.getCreaturesCount() != expectedCreatures) {
            System.out.println("❌ FAIL: Criaturas esperadas " + expectedCreatures + ", obtenidas " + orcs.getCreaturesCount());
            System.exit(1);
        }
        System.out.println("✅ PASS: Tras " + damageDealt + " HP daño, quedan " + orcs.getCreaturesCount() + " criaturas");

        // Test 4: Moral Warhammer 2d6
        System.out.println("\nTest 4: Sistema de moral Warhammer 2d6");
        int morale = orcs.getMorale();
        System.out.println("  Valor de moral Orco: " + morale);
        
        // Simular chequeo manual
        int roll2d6 = DiceRoller.roll2D6();
        System.out.println("  Tirada 2d6: " + roll2d6);
        boolean passed = orcs.checkMorale(roll2d6);
        
        if (roll2d6 <= morale && !passed) {
            System.out.println("❌ FAIL: Debió pasar el chequeo (roll " + roll2d6 + " <= moral " + morale + ")");
            System.exit(1);
        }
        if (roll2d6 > morale && passed) {
            System.out.println("❌ FAIL: Debió fallar el chequeo (roll " + roll2d6 + " > moral " + morale + ")");
            System.exit(1);
        }
        
        String result = passed ? "Pasa (mantiene posición)" : "Falla (huye)";
        System.out.println("✅ PASS: Chequeo moral " + result);

        // Test 5: Ogre con reach 10ft = 3 filas
        System.out.println("\nTest 5: Ogre con alcance 10ft (3 filas atacantes)");
        CombatUnit ogres = UnitFactory.createOgres(5);
        
        if (ogres.getReachFeet() != 10) {
            System.out.println("❌ FAIL: Reach esperado 10ft, obtenido " + ogres.getReachFeet());
            System.exit(1);
        }
        if (ogres.getRowsAttacking() != 3) {
            System.out.println("❌ FAIL: rowsAttacking esperado 3, obtenido " + ogres.getRowsAttacking());
            System.exit(1);
        }
        System.out.println("✅ PASS: Ogre alcance " + ogres.getReachFeet() + "ft, " + ogres.getRowsAttacking() + " filas");

        System.out.println("\n=== Todos los tests pasaron ✅ ===");
    }
}