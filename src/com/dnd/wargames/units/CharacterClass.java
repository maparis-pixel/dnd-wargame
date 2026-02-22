package com.dnd.wargames.units;

/**
 * Clases de personaje disponibles en D&D Wargames.
 *
 * @author Lead Developer
 * @version 2.0
 */
public enum CharacterClass {
    WARRIOR("Guerrero"),
    WIZARD("Mago"),
    CLERIC("Clérigo"),
    BARD("Bardo"),
    ROGUE("Pícaro");

    private final String displayName;

    CharacterClass(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}