package com.dnd.wargames.test;

import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.MoraleEffect;
import com.dnd.wargames.units.UnitFactory;
import com.dnd.wargames.web.WebBattleServer;

/**
 * Smoke test dedicado a validar moral y disponibilidad del modo web.
 */
public class MoraleAndWebSmokeTest {

    public static void main(String[] args) {
        System.out.println("🧪 MORALE + WEB SMOKE TEST");
        System.out.println("===========================");

        boolean passed = true;

        try {
            CombatUnit guards = UnitFactory.createHumanGuards(10);

            guards.takeDamage(2);
            if (guards.getMoraleStatus() != MoraleEffect.RAGING || guards.getMoraleAttackModifier() != 2) {
                throw new IllegalStateException("Estado RAGING inválido");
            }

            guards.takeDamage(3);
            if (guards.getMoraleStatus() != MoraleEffect.CONFUSED || guards.getMoraleAttackModifier() != -1) {
                throw new IllegalStateException("Estado CONFUSED inválido");
            }

            guards.takeDamage(3);
            if (guards.getMoraleStatus() != MoraleEffect.FRIGHTENED || guards.getMoraleAttackModifier() != -2) {
                throw new IllegalStateException("Estado FRIGHTENED inválido");
            }

            System.out.println("✅ Transiciones de moral correctas");
        } catch (Exception e) {
            passed = false;
            System.out.println("❌ Error en validación de moral: " + e.getMessage());
        }

        try {
            WebBattleServer server = new WebBattleServer();
            if (server == null) {
                throw new IllegalStateException("No se pudo instanciar WebBattleServer");
            }
            System.out.println("✅ Modo web disponible (instanciación correcta)");
        } catch (Exception e) {
            passed = false;
            System.out.println("❌ Error en validación web: " + e.getMessage());
        }

        if (passed) {
            System.out.println("🎉 SMOKE TEST OK");
        } else {
            System.out.println("⚠️ SMOKE TEST CON FALLOS");
            System.exit(1);
        }
    }
}
