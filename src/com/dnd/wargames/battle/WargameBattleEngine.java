package com.dnd.wargames.battle;

import com.dnd.wargames.units.Character;
import com.dnd.wargames.units.CombatUnit;
import java.util.*;

/**
 * Motor de batalla a escala de batallón.
 * Coordina el combate entre unidades y personajes.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class WargameBattleEngine {
    private List<Character> team1Characters;
    private List<CombatUnit> team1Units;
    private List<Character> team2Characters;
    private List<CombatUnit> team2Units;

    private List<Combatant> initiativeOrder;
    private boolean battleActive;
    private int turnCounter;
    private BattleDecisionProvider decisionProvider;

    /**
     * Constructor del motor de batalla.
     */
    public WargameBattleEngine() {
        this.team1Characters = new ArrayList<>();
        this.team1Units = new ArrayList<>();
        this.team2Characters = new ArrayList<>();
        this.team2Units = new ArrayList<>();
        this.initiativeOrder = new ArrayList<>();
        this.battleActive = false;
        this.turnCounter = 0;
        this.decisionProvider = (attacker, fleeing, hasAlternativeTarget) -> false;
    }

    public interface BattleDecisionProvider {
        boolean shouldPursue(CombatUnit attacker, CombatUnit fleeingUnit, boolean hasAlternativeTarget);
    }

    public void setDecisionProvider(BattleDecisionProvider decisionProvider) {
        if (decisionProvider == null) {
            this.decisionProvider = (attacker, fleeing, hasAlternativeTarget) -> false;
            return;
        }
        this.decisionProvider = decisionProvider;
    }

    /**
     * Agrega una unidad al Equipo 1.
     */
    public void addToTeam1(Character character) {
        team1Characters.add(character);
    }

    public void addToTeam1(CombatUnit unit) {
        team1Units.add(unit);
    }

    /**
     * Agrega una unidad al Equipo 2.
     */
    public void addToTeam2(Character character) {
        team2Characters.add(character);
    }

    public void addToTeam2(CombatUnit unit) {
        team2Units.add(unit);
    }

    /**
     * Determina el orden de iniciativa.
     */
    public void determineInitiative() {
        initiativeOrder.clear();
        List<Combatant> allCombatants = new ArrayList<>();

        // Agregar todos los combatientes
        for (Character c : team1Characters) allCombatants.add(new Combatant(c, 1));
        for (CombatUnit u : team1Units) allCombatants.add(new Combatant(u, 1));
        for (Character c : team2Characters) allCombatants.add(new Combatant(c, 2));
        for (CombatUnit u : team2Units) allCombatants.add(new Combatant(u, 2));

        // Tirar iniciativa una sola vez por combatiente (d20 + DEX mod)
        for (Combatant combatant : allCombatants) {
            int initiative = DiceRoller.rollInitiative(combatant.getDexterityModifier());
            combatant.setInitiativeScore(initiative);
        }

        // Ordenar por iniciativa descendente
        allCombatants.sort((a, b) -> Integer.compare(b.getInitiativeScore(), a.getInitiativeScore()));

        initiativeOrder.addAll(allCombatants);
    }

    /**
     * Inicia la batalla.
     */
    public void startBattle() {
        if (getTotalCombatants() < 2) {
            System.out.println("❌ Necesitas al menos 2 unidades para combatir.");
            return;
        }

        battleActive = true;
        determineInitiative();

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║           ⚔️ BATALLA INICIADA ⚔️           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();

        showBattleStatus();
        showInitiativeOrder();
    }

    /**
     * Ejecuta un turno completo.
     */
    public void executeTurn() {
        if (!battleActive) {
            System.out.println("❌ La batalla no está activa.");
            return;
        }

        initiativeOrder.removeIf(c -> !c.isAlive());
        if (initiativeOrder.isEmpty()) {
            endBattle();
            return;
        }

        if (!hasLivingCombatants(1) || !hasLivingCombatants(2)) {
            endBattle();
            return;
        }

        turnCounter++;
        System.out.println("\n══════════ TURNO " + turnCounter + " ══════════");

        Combatant highestInitiativeAlive = initiativeOrder.stream()
                .filter(Combatant::isAlive)
                .findFirst()
                .orElse(null);

        if (highestInitiativeAlive == null) {
            endBattle();
            return;
        }

        int firstRoundTeam = highestInitiativeAlive.team;
        int secondRoundTeam = (firstRoundTeam == 1) ? 2 : 1;

        executeRoundForTeam(firstRoundTeam);

        if (!battleActive || !hasLivingCombatants(1) || !hasLivingCombatants(2)) {
            endBattle();
            return;
        }

        executeRoundForTeam(secondRoundTeam);

        initiativeOrder.removeIf(c -> !c.isAlive());

        if (!hasLivingCombatants(1) || !hasLivingCombatants(2)) {
            endBattle();
            return;
        }

        showBattleStatus();
    }

    private void executeRoundForTeam(int team) {
        String roundLabel = (team == 1) ? "Aliados" : "Enemigos";
        System.out.println("\n🔁 Ronda de " + roundLabel + " (Equipo " + team + ")");

        List<Combatant> roundOrder = new ArrayList<>();
        for (Combatant combatant : initiativeOrder) {
            if (combatant.team == team && combatant.isAlive()) {
                roundOrder.add(combatant);
            }
        }

        for (Combatant attacker : roundOrder) {
            if (!attacker.isAlive() || !battleActive) {
                continue;
            }

            Combatant target = findTargetFor(attacker);
            System.out.println("🎯 Actúa: " + attacker.getName() + " (Ini " + attacker.getInitiativeScore() + ")");

            if (target != null) {
                executeAttack(attacker, target);
            } else {
                System.out.println("   No hay objetivos disponibles.");
            }

            initiativeOrder.removeIf(c -> !c.isAlive());

            if (!hasLivingCombatants(1) || !hasLivingCombatants(2)) {
                return;
            }
        }
    }

    /**
     * Ejecuta un ataque entre dos combatientes.
     */
    private void executeAttack(Combatant attacker, Combatant defender) {
        CombatResolver.AttackResult result;

        if (attacker.isCharacter() && defender.isUnit()) {
            // Personaje vs Unidad
            result = CombatResolver.resolveCharacterVsUnit(attacker.character, defender.unit);
        } else if (attacker.isUnit() && defender.isCharacter()) {
            // Unidad vs Personaje
            result = CombatResolver.resolveUnitVsCharacter(attacker.unit, defender.character);
        } else {
            // Unidad vs Unidad
            result = CombatResolver.resolveUnitVsUnit(attacker.unit, defender.unit);
        }

        // Mostrar resultado
        System.out.println("   Resultado: " + result);

        if (result.defenderFled && defender.isUnit()) {
            handleUnitRetreat(attacker, defender);
        }
    }

    private void handleUnitRetreat(Combatant attacker, Combatant fleeingDefender) {
        CombatUnit fleeingUnit = fleeingDefender.unit;
        if (fleeingUnit == null || !fleeingUnit.hasFledBattle()) {
            return;
        }

        System.out.println("   🏳️ " + fleeingUnit.getBattleDisplayName() + " está en retirada.");

        if (attacker.isUnit() && attacker.unit.isAlive()) {
            Combatant alternativeTarget = findTargetForExcludingUnit(attacker, fleeingUnit);
            boolean hasAlternativeTarget = alternativeTarget != null;

            boolean shouldPursue = decisionProvider.shouldPursue(attacker.unit, fleeingUnit, hasAlternativeTarget);

            if (shouldPursue) {
                System.out.println("   🏃 " + attacker.unit.getBattleDisplayName() + " intenta perseguir.");
                if (isPursuitSuccessful(attacker.unit, fleeingUnit)) {
                    System.out.println("   ✅ La persecución alcanza a la unidad en retirada.");
                    CombatResolver.AttackResult pursuitResult = CombatResolver.resolveUnitVsUnit(attacker.unit, fleeingUnit);
                    System.out.println("   Resultado persecución: " + pursuitResult);
                } else {
                    System.out.println("   ❌ La unidad en retirada escapa de la persecución.");
                }
            } else if (hasAlternativeTarget) {
                System.out.println("   🔄 La unidad no persigue y cambia a otro objetivo.");
                executeAttack(attacker, alternativeTarget);
            }
        }

        attemptRegroupIfPossible(fleeingDefender);
    }

    private Combatant findTargetForExcludingUnit(Combatant attacker, CombatUnit excludedUnit) {
        int targetTeam = (attacker.team == 1) ? 2 : 1;

        for (Combatant c : initiativeOrder) {
            if (c.team == targetTeam && c.isCharacter() && c.isAlive()) {
                return c;
            }
        }

        for (Combatant c : initiativeOrder) {
            if (c.team == targetTeam && c.isUnit() && c.isAlive() && c.unit != excludedUnit) {
                return c;
            }
        }

        return null;
    }

    private boolean isPursuitSuccessful(CombatUnit pursuer, CombatUnit fleeingUnit) {
        int pursuerRoll = DiceRoller.rollD20() + Math.max(0, pursuer.getSpeedFeet() / 10);
        int fleeingRoll = DiceRoller.rollD20() + Math.max(0, fleeingUnit.getSpeedFeet() / 10);

        System.out.println("   Tirada persecución: " + pursuerRoll + " vs escape " + fleeingRoll);
        return pursuerRoll >= fleeingRoll;
    }

    private void attemptRegroupIfPossible(Combatant fleeingDefender) {
        if (!fleeingDefender.isUnit() || !fleeingDefender.unit.canAttemptRegroup()) {
            return;
        }

        if (!hasLivingCharacterInTeam(fleeingDefender.team)) {
            return;
        }

        int regroupRoll = DiceRoller.roll2D6();
        boolean regrouped = fleeingDefender.unit.attemptRegroup(regroupRoll);

        System.out.println("   🧭 Reagrupamiento por personaje aliado: 2d6=" + regroupRoll +
                " vs " + fleeingDefender.unit.getMorale() + " -> " + (regrouped ? "SE REAGRUPA" : "NO SE REAGRUPA"));
    }

    private boolean hasLivingCharacterInTeam(int team) {
        for (Combatant combatant : initiativeOrder) {
            if (combatant.team == team && combatant.isCharacter() && combatant.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Encuentra un objetivo para el combatiente actual.
     */
    private Combatant findTargetFor(Combatant attacker) {
        // Buscar en el equipo contrario
        int targetTeam = (attacker.team == 1) ? 2 : 1;

        // Priorizar personajes sobre unidades
        for (Combatant c : initiativeOrder) {
            if (c.team == targetTeam && c.isCharacter() && c.isAlive()) {
                return c;
            }
        }

        // Luego unidades
        for (Combatant c : initiativeOrder) {
            if (c.team == targetTeam && c.isUnit() && c.isAlive()) {
                return c;
            }
        }

        return null;
    }

    /**
     * Verifica si un equipo tiene combatientes vivos.
     */
    private boolean hasLivingCombatants(int team) {
        for (Combatant c : initiativeOrder) {
            if (c.team == team && c.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Muestra el estado actual de la batalla.
     */
    public void showBattleStatus() {
        System.out.println("\n📊 ESTADO DE LA BATALLA");
        System.out.println("════════════════════════════════════════════");

        System.out.println("🏹 EQUIPO 1:");
        for (Character c : team1Characters) {
            System.out.println("   " + c);
        }
        for (CombatUnit u : team1Units) {
            System.out.println("   " + u);
        }

        System.out.println("\n🐺 EQUIPO 2:");
        for (Character c : team2Characters) {
            System.out.println("   " + c);
        }
        for (CombatUnit u : team2Units) {
            System.out.println("   " + u);
        }
        System.out.println();
    }

    /**
     * Muestra el orden de iniciativa.
     */
    private void showInitiativeOrder() {
        System.out.println("🎲 ORDEN DE INICIATIVA:");
        for (int i = 0; i < initiativeOrder.size(); i++) {
            Combatant c = initiativeOrder.get(i);
            String marker = (i == 0) ? " → " : "   ";
            System.out.println(marker + c.getName() + " (Equipo " + c.team + ", Ini " + c.getInitiativeScore() + ")");
        }
        System.out.println();
    }

    /**
     * Finaliza la batalla.
     */
    private void endBattle() {
        battleActive = false;

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           🏁 BATALLA FINALIZADA 🏁         ║");
        System.out.println("╚════════════════════════════════════════════╝");

        if (hasLivingCombatants(1) && !hasLivingCombatants(2)) {
            System.out.println("🎉 ¡EQUIPO 1 GANA!");
        } else if (!hasLivingCombatants(1) && hasLivingCombatants(2)) {
            System.out.println("🎉 ¡EQUIPO 2 GANA!");
        } else {
            System.out.println("🤝 ¡EMPATE!");
        }
    }

    /**
     * Obtiene el total de combatientes.
     */
    private int getTotalCombatants() {
        return team1Characters.size() + team1Units.size() +
               team2Characters.size() + team2Units.size();
    }

    // Getters para la CLI
    public List<Character> getTeam1Characters() { return new ArrayList<>(team1Characters); }
    public List<CombatUnit> getTeam1Units() { return new ArrayList<>(team1Units); }
    public List<Character> getTeam2Characters() { return new ArrayList<>(team2Characters); }
    public List<CombatUnit> getTeam2Units() { return new ArrayList<>(team2Units); }

    /**
     * Clase interna para representar un combatiente en la iniciativa.
     */
    private static class Combatant {
        public final Character character;
        public final CombatUnit unit;
        public final int team;
        private int initiativeScore;

        public Combatant(Character character, int team) {
            this.character = character;
            this.unit = null;
            this.team = team;
        }

        public Combatant(CombatUnit unit, int team) {
            this.character = null;
            this.unit = unit;
            this.team = team;
        }

        public boolean isCharacter() { return character != null; }
        public boolean isUnit() { return unit != null; }

        public String getName() {
            return isCharacter() ? character.getName() : unit.getBattleDisplayName();
        }

        public boolean isAlive() {
            return isCharacter() ? character.isAlive() : unit.isAlive();
        }

        public int getDexterityModifier() {
            if (isCharacter()) {
                return character.getAbilityModifier(character.getDexterity());
            } else {
                return (unit.getDexterity() - 10) / 2;
            }
        }

        public int getInitiativeScore() {
            return initiativeScore;
        }

        public void setInitiativeScore(int initiativeScore) {
            this.initiativeScore = initiativeScore;
        }
    }
}