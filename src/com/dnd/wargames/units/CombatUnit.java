package com.dnd.wargames.units;

/**
 * Representa una unidad de combate compuesta por múltiples criaturas del mismo tipo.
 * Optimiza el combate a escala de batallón reduciendo PF en lugar de rastrear cada criatura.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class CombatUnit {
    // Identidad
    private String id;
    private String name;            // Ej: "Unidad de 15 Trasgos"
    private CreatureType creatureType;    // GOBLIN, SKELETON, ORC, etc

    // Puntos de Fuerza (PF)
    private int strengthPoints;     // PF actual (cantidad de criaturas)
    private int maxStrengthPoints;  // PF máximo inicial

    // Estadísticas de Combate
    private int armorClass;         // CA de criatura individual
    private int baseAttackBonus;    // Bono de ataque base
    private int baseDamage;         // Daño promedio por criatura
    private int dexterity;          // Para iniciativa

    // Dureza (para criaturas grandes)
    private int durability;         // Cuántos impactos = 1 PF (1, 3, 5)
    private int currentDamageAccumulated;  // Acumula daño parcial

    // Moral
    private MoraleEffect moraleStatus;  // NONE, FRIGHTENED, CONFUSED
    private int morale;                 // Base 10-20

    /**
     * Constructor básico para una unidad de combate.
     */
    public CombatUnit(String name, CreatureType creatureType, int strengthPoints) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.creatureType = creatureType;
        this.strengthPoints = strengthPoints;
        this.maxStrengthPoints = strengthPoints;

        // Valores por defecto según el tipo de criatura
        setDefaultStatsForCreatureType();

        this.moraleStatus = MoraleEffect.NONE;
        this.currentDamageAccumulated = 0;
    }

    /**
     * Constructor completo para una unidad de combate.
     */
    public CombatUnit(String name, CreatureType creatureType, int strengthPoints,
                     int armorClass, int baseAttackBonus, int baseDamage, int dexterity,
                     int durability, int morale) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.creatureType = creatureType;
        this.strengthPoints = strengthPoints;
        this.maxStrengthPoints = strengthPoints;

        this.armorClass = armorClass;
        this.baseAttackBonus = baseAttackBonus;
        this.baseDamage = baseDamage;
        this.dexterity = dexterity;
        this.durability = durability;
        this.morale = morale;

        this.moraleStatus = MoraleEffect.NONE;
        this.currentDamageAccumulated = 0;
    }

    /**
     * Establece estadísticas por defecto según el tipo de criatura.
     */
    private void setDefaultStatsForCreatureType() {
        switch (creatureType) {
            case GOBLIN:
                this.armorClass = 15;
                this.baseAttackBonus = 2;
                this.baseDamage = 5;
                this.dexterity = 14;
                this.durability = 1;
                this.morale = 12;
                break;
            case ORC:
                this.armorClass = 13;
                this.baseAttackBonus = 3;
                this.baseDamage = 7;
                this.dexterity = 12;
                this.durability = 1;
                this.morale = 14;
                break;
            case SKELETON:
                this.armorClass = 13;
                this.baseAttackBonus = 2;
                this.baseDamage = 5;
                this.dexterity = 14;
                this.durability = 1;
                this.morale = 15; // No tienen moral, pero por consistencia
                break;
            case OGRE:
                this.armorClass = 11;
                this.baseAttackBonus = 2;
                this.baseDamage = 13;
                this.dexterity = 8;
                this.durability = 3; // Criatura grande
                this.morale = 13;
                break;
            case HUMAN_GUARD:
                this.armorClass = 16; // Armadura de placas
                this.baseAttackBonus = 3;
                this.baseDamage = 8;
                this.dexterity = 12;
                this.durability = 1;
                this.morale = 15;
                break;
            default:
                // Valores por defecto
                this.armorClass = 13;
                this.baseAttackBonus = 2;
                this.baseDamage = 5;
                this.dexterity = 12;
                this.durability = 1;
                this.morale = 12;
                break;
        }
    }

    /**
     * Calcula el bono de masa para ataques (Mass Bonus).
     * +1 por cada 5 PF actuales.
     */
    public int getMassBonus() {
        return strengthPoints / 5;
    }

    /**
     * Aplica daño a la unidad considerando dureza.
     */
    public void takeDamage(int damage) {
        currentDamageAccumulated += damage;

        // Convertir daño acumulado a PF perdidos
        int pfLost = currentDamageAccumulated / durability;
        if (pfLost > 0) {
            strengthPoints = Math.max(0, strengthPoints - pfLost);
            currentDamageAccumulated = currentDamageAccumulated % durability;
            updateMoraleStatus();
        }
    }

    /**
     * Actualiza el estado de moral según PF restantes y moral base.
     */
    private void updateMoraleStatus() {
        if (!isAlive()) {
            moraleStatus = MoraleEffect.NONE;
            return;
        }

        double ratio = (double) strengthPoints / (double) maxStrengthPoints;

        if (ratio <= 0.25) {
            moraleStatus = MoraleEffect.FRIGHTENED;
        } else if (ratio <= 0.50) {
            moraleStatus = MoraleEffect.CONFUSED;
        } else if (morale >= 15 && ratio >= 0.80) {
            moraleStatus = MoraleEffect.RAGING;
        } else {
            moraleStatus = MoraleEffect.NONE;
        }
    }

    /**
     * Modificador de ataque por estado de moral.
     */
    public int getMoraleAttackModifier() {
        switch (moraleStatus) {
            case RAGING:
                return 2;
            case CONFUSED:
                return -1;
            case FRIGHTENED:
                return -2;
            default:
                return 0;
        }
    }

    /**
     * Verifica si la unidad está viva (tiene PF > 0).
     */
    public boolean isAlive() {
        return strengthPoints > 0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public CreatureType getCreatureType() { return creatureType; }
    public int getStrengthPoints() { return strengthPoints; }
    public int getMaxStrengthPoints() { return maxStrengthPoints; }
    public int getArmorClass() { return armorClass; }
    public int getBaseAttackBonus() { return baseAttackBonus; }
    public int getBaseDamage() { return baseDamage; }
    public int getDexterity() { return dexterity; }
    public int getDurability() { return durability; }
    public MoraleEffect getMoraleStatus() { return moraleStatus; }
    public int getMorale() { return morale; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setArmorClass(int armorClass) { this.armorClass = armorClass; }
    public void setBaseAttackBonus(int baseAttackBonus) { this.baseAttackBonus = baseAttackBonus; }
    public void setBaseDamage(int baseDamage) { this.baseDamage = baseDamage; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
    public void setDurability(int durability) { this.durability = durability; }
    public void setMoraleStatus(MoraleEffect moraleStatus) { this.moraleStatus = moraleStatus; }
    public void setMorale(int morale) { this.morale = morale; }

    @Override
    public String toString() {
        return String.format("%s (%d/%d PF) - CA: %d, Daño: %d, Moral: %s",
                           name, strengthPoints, maxStrengthPoints, armorClass, baseDamage, moraleStatus);
    }
}