package com.dnd.wargames.demo;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.battle.WargameBattleEngine;

/**
 * Demo rápida del sistema de combate.
 * Muestra cómo crear unidades y hacerlas combatir.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class CombatDemo {

    public static void main(String[] args) {
        System.out.println("🎲 DEMO - Sistema de Combate D&D Wargames");
        System.out.println("==========================================");

        // Crear unidades
        System.out.println("\n1. Creando unidades...");

        CombatUnit guardias = UnitFactory.createHumanGuards(10);
        CombatUnit orcos = UnitFactory.createOrcs(8);
        com.dnd.wargames.units.Character guerrero = UnitFactory.createWarrior("Sir Galen", 3);

        System.out.println("✓ " + guardias.getName() + " creados");
        System.out.println("✓ " + orcos.getName() + " creados");
        System.out.println("✓ " + guerrero.getName() + " creado");

        // Configurar batalla
        System.out.println("\n2. Configurando batalla...");
        WargameBattleEngine battle = new WargameBattleEngine();

        battle.addToTeam1(guardias);  // Equipo 1: Guardias humanos
        battle.addToTeam1(guerrero);  // + Guerrero
        battle.addToTeam2(orcos);     // Equipo 2: Orcos

        // Iniciar batalla
        System.out.println("\n3. ¡Iniciando batalla!");
        battle.startBattle();

        // Ejecutar algunos turnos
        System.out.println("\n4. Ejecutando turnos de combate...");
        for (int i = 0; i < 5; i++) {
            System.out.println("\n--- TURNO " + (i + 1) + " ---");
            battle.executeTurn();

            // Pequeña pausa para que sea legible
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n🎯 Demo completada!");
        System.out.println("El sistema de combate está funcionando correctamente.");
    }
}