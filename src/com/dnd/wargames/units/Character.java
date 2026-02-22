package com.dnd.wargames.units;

/**
 * Representa un personaje individual en D&D Wargames.
 * Personajes únicos con estadísticas completas D&D 5e.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class Character {
    // Identificación
    private String name;
    private CharacterClass charClass;

    // Ability Scores (Puntuaciones de Habilidad D&D 5e)
    private int strength;           // STR (1-20, típicamente 8-15)
    private int dexterity;          // DEX
    private int constitution;       // CON
    private int intelligence;       // INT
    private int wisdom;             // WIS
    private int charisma;           // CHA

    // Salud y Combate
    private int health;             // HP actual
    private int maxHealth;          // HP máximo (calculado de CON)
    private int armorClass;         // CA (defensa)
    private int attackBonus;        // Bono de ataque base
    private int level;              // 1-20

    // Experiencia y Progresión
    private int experience;         // Total XP
    private int proficiencyBonus;   // Bonus de pericia

    /**
     * Constructor básico para un personaje.
     */
    public Character(String name, CharacterClass charClass, int level) {
        this.name = name;
        this.charClass = charClass;
        this.level = level;
        this.proficiencyBonus = calculateProficiencyBonus(level);

        // Valores por defecto (se pueden modificar después)
        this.strength = 10;
        this.dexterity = 10;
        this.constitution = 10;
        this.intelligence = 10;
        this.wisdom = 10;
        this.charisma = 10;

        this.health = 10; // Valor temporal
        this.maxHealth = 10;
        this.armorClass = 10;
        this.attackBonus = 0;
        this.experience = 0;
    }

    /**
     * Constructor completo para un personaje.
     */
    public Character(String name, CharacterClass charClass, int level,
                    int str, int dex, int con, int intel, int wis, int cha,
                    int hp, int ac, int attackBonus) {
        this.name = name;
        this.charClass = charClass;
        this.level = level;
        this.proficiencyBonus = calculateProficiencyBonus(level);

        this.strength = str;
        this.dexterity = dex;
        this.constitution = con;
        this.intelligence = intel;
        this.wisdom = wis;
        this.charisma = cha;

        this.health = hp;
        this.maxHealth = hp;
        this.armorClass = ac;
        this.attackBonus = attackBonus;
        this.experience = 0;
    }

    /**
     * Calcula el bonus de pericia basado en el nivel.
     */
    private int calculateProficiencyBonus(int level) {
        if (level <= 4) return 2;
        if (level <= 8) return 3;
        if (level <= 12) return 4;
        if (level <= 16) return 5;
        return 6;
    }

    /**
     * Calcula el modificador de habilidad (ability modifier).
     */
    public int getAbilityModifier(int abilityScore) {
        return (abilityScore - 10) / 2;
    }

    // Getters
    public String getName() { return name; }
    public CharacterClass getCharacterClass() { return charClass; }
    public int getLevel() { return level; }
    public int getProficiencyBonus() { return proficiencyBonus; }

    public int getStrength() { return strength; }
    public int getDexterity() { return dexterity; }
    public int getConstitution() { return constitution; }
    public int getIntelligence() { return intelligence; }
    public int getWisdom() { return wisdom; }
    public int getCharisma() { return charisma; }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getArmorClass() { return armorClass; }
    public int getAttackBonus() { return attackBonus; }
    public int getExperience() { return experience; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setHealth(int health) { this.health = Math.max(0, health); }
    public void setArmorClass(int armorClass) { this.armorClass = armorClass; }
    public void setAttackBonus(int attackBonus) { this.attackBonus = attackBonus; }
    public void setExperience(int experience) { this.experience = experience; }

    // Métodos de combate
    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    public void heal(int healing) {
        this.health = Math.min(this.maxHealth, this.health + healing);
    }

    @Override
    public String toString() {
        return String.format("%s (Nivel %d %s) - HP: %d/%d, CA: %d",
                           name, level, charClass, health, maxHealth, armorClass);
    }
}