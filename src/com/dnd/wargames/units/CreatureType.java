package com.dnd.wargames.units;

/**
 * Tipos de criaturas disponibles para unidades de combate.
 *
 * @author Lead Developer
 * @version 2.0
 */
public enum CreatureType {
    CUSTOM("Personalizada"),
    GOBLIN("Trasgo"),
    ORC("Orco"),
    SKELETON("Esqueleto"),
    OGRE("Ogro"),
    HUMAN_GUARD("Guardia Humano"),
    TROLL("Trol"),
    WOLF("Lobo"),
    BEAR("Oso");

    private final String displayName;

    CreatureType(String displayName) {
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