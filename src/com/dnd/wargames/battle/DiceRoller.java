package com.dnd.wargames.battle;

import java.util.Random;

/**
 * Sistema de dados para D&D Wargames.
 * Maneja todas las tiradas de dados del juego (D&D y Warhammer).
 *
 * @author Lead Developer
 * @version 3.0
 */
public class DiceRoller {
    private static final Random random = new Random();

    /**
     * Tira 2d6 (sistema Warhammer para chequeos de moral).
     */
    public static int roll2D6() {
        return rollD6() + rollD6();
    }

    /**
     * Tira 1d6.
     */
    public static int rollD6() {
        return random.nextInt(6) + 1;
    }

    /**
     * Tira un dado de 20 caras (d20).
     */
    public static int rollD20() {
        return random.nextInt(20) + 1;
    }

    /**
     * Tira un dado de 20 caras con modificador.
     */
    public static int rollD20(int modifier) {
        return rollD20() + modifier;
    }

    /**
     * Tira múltiples dados d20 y devuelve el más alto (ventaja).
     */
    public static int rollD20WithAdvantage(int modifier) {
        int roll1 = rollD20() + modifier;
        int roll2 = rollD20() + modifier;
        return Math.max(roll1, roll2);
    }

    /**
     * Tira múltiples dados d20 y devuelve el más bajo (desventaja).
     */
    public static int rollD20WithDisadvantage(int modifier) {
        int roll1 = rollD20() + modifier;
        int roll2 = rollD20() + modifier;
        return Math.min(roll1, roll2);
    }

    /**
     * Tira un dado de N caras.
     */
    public static int rollDie(int sides) {
        return random.nextInt(sides) + 1;
    }

    /**
     * Tira un dado de N caras con modificador.
     */
    public static int rollDie(int sides, int modifier) {
        return rollDie(sides) + modifier;
    }

    /**
     * Tira dados de salvación (d20 + modificador vs CD).
     */
    public static boolean rollSavingThrow(int abilityModifier, int difficultyClass) {
        int roll = rollD20() + abilityModifier;
        return roll >= difficultyClass;
    }

    /**
     * Simula una tirada de iniciativa (d20 + DEX modifier).
     */
    public static int rollInitiative(int dexterityModifier) {
        return rollD20() + dexterityModifier;
    }

    /**
     * Tira dados de moral (d20 + WIS modifier vs CD).
     */
    public static boolean rollMoraleCheck(int wisdomModifier, int difficultyClass) {
        int roll = rollD20() + wisdomModifier;
        return roll >= difficultyClass;
    }
}