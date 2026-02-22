package com.dnd.wargames.units;

/**
 * Estados de moral para unidades de combate.
 *
 * @author Lead Developer
 * @version 2.0
 */
public enum MoraleEffect {
    NONE("Normal"),
    FRIGHTENED("Asustado"),
    CONFUSED("Confundido"),
    RAGING("Enfurecido");

    private final String displayName;

    MoraleEffect(String displayName) {
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