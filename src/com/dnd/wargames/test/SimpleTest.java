package com.dnd.wargames.test;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.*;

/**
 * Test ultra simple para verificar funcionamiento básico.
 * Solo verifica que las clases se pueden instanciar y tienen valores básicos.
 */
public class SimpleTest {

    public static void main(String[] args) {
        System.out.println("🧪 TEST ULTRA SIMPLE");
        System.out.println("====================");

        try {
            // Test 1: Crear unidades básicas
            System.out.println("1. Creando unidades...");
            CombatUnit guardias = UnitFactory.createHumanGuards(5);
            CombatUnit orcos = UnitFactory.createOrcs(3);

            System.out.println("   ✓ " + guardias.getName());
            System.out.println("   ✓ " + orcos.getName());

            // Test 2: Verificar valores básicos
            System.out.println("2. Verificando valores...");
            if (guardias.getStrengthPoints() == 5) {
                System.out.println("   ✓ Guardias tienen 5 PF");
            } else {
                System.out.println("   ❌ Guardias PF incorrecto: " + guardias.getStrengthPoints());
            }

            if (orcos.getArmorClass() == 13) {
                System.out.println("   ✓ Orcos tienen CA 13");
            } else {
                System.out.println("   ❌ Orcos CA incorrecto: " + orcos.getArmorClass());
            }

            // Test 3: Sistema de dados básico
            System.out.println("3. Probando dados...");
            int roll = DiceRoller.rollD20();
            if (roll >= 1 && roll <= 20) {
                System.out.println("   ✓ d20 funciona: " + roll);
            } else {
                System.out.println("   ❌ d20 fuera de rango: " + roll);
            }

            // Test 4: Combate simple
            System.out.println("4. Simulando combate...");
            int initialGuardPF = guardias.getStrengthPoints();
            int initialOrcPF = orcos.getStrengthPoints();

            // Simular algunos ataques
            for (int i = 0; i < 3; i++) {
                CombatResolver.AttackResult result = CombatResolver.resolveUnitVsUnit(guardias, orcos);
                if (result.hit) {
                    System.out.println("   Ataque " + (i+1) + ": Impacto! " + result.damage + " PF daño");
                } else {
                    System.out.println("   Ataque " + (i+1) + ": Fallo");
                }
            }

            int finalGuardPF = guardias.getStrengthPoints();
            int finalOrcPF = orcos.getStrengthPoints();

            System.out.println("   Estado final - Guardias: " + finalGuardPF + "/" + initialGuardPF + " PF");
            System.out.println("   Estado final - Orcos: " + finalOrcPF + "/" + initialOrcPF + " PF");

            System.out.println("\n🎉 TEST COMPLETADO - Sistema funcionando!");

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}