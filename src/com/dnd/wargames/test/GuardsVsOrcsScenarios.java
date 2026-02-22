package com.dnd.wargames.test;

import com.dnd.wargames.battle.WargameBattleEngine;
import com.dnd.wargames.units.CombatUnit;
import com.dnd.wargames.units.UnitFactory;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Ejecuta escenarios específicos de combate solo entre guardias humanos y orcos.
 */
public class GuardsVsOrcsScenarios {

    private static class Result {
        final int guardsStart;
        final int orcsStart;
        final int guardsEnd;
        final int orcsEnd;
        final int turns;
        final String winner;

        Result(int guardsStart, int orcsStart, int guardsEnd, int orcsEnd, int turns, String winner) {
            this.guardsStart = guardsStart;
            this.orcsStart = orcsStart;
            this.guardsEnd = guardsEnd;
            this.orcsEnd = orcsEnd;
            this.turns = turns;
            this.winner = winner;
        }
    }

    public static void main(String[] args) {
        int[][] scenarios = {
            {5, 5},
            {10, 10},
            {20, 15}
        };

        System.out.println("⚔️ Escenarios: Guardias Humanos vs Orcos");
        System.out.println("========================================");

        for (int[] scenario : scenarios) {
            Result result = runScenario(scenario[0], scenario[1]);
            System.out.printf("%dv%d -> Ganador: %s | PF finales Guardias: %d/%d | PF finales Orcos: %d/%d | Turnos: %d%n",
                    result.guardsStart,
                    result.orcsStart,
                    result.winner,
                    result.guardsEnd,
                    result.guardsStart,
                    result.orcsEnd,
                    result.orcsStart,
                    result.turns);
        }
    }

    private static Result runScenario(int guardsCount, int orcsCount) {
        CombatUnit guardias = UnitFactory.createHumanGuards(guardsCount);
        CombatUnit orcos = UnitFactory.createOrcs(orcsCount);

        WargameBattleEngine battle = new WargameBattleEngine();
        battle.addToTeam1(guardias);
        battle.addToTeam2(orcos);

        PrintStream originalOut = System.out;
        int turns = 0;

        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            battle.startBattle();

            while (guardias.isAlive() && orcos.isAlive() && turns < 200) {
                battle.executeTurn();
                turns++;
            }
        } finally {
            System.setOut(originalOut);
        }

        String winner;
        if (guardias.isAlive() && !orcos.isAlive()) {
            winner = "Guardias";
        } else if (!guardias.isAlive() && orcos.isAlive()) {
            winner = "Orcos";
        } else {
            winner = "Empate";
        }

        return new Result(
                guardsCount,
                orcsCount,
                guardias.getCreaturesCount(),
                orcos.getCreaturesCount(),
                turns,
                winner
        );
    }
}
