package com.dnd.wargames.units;

/**
 * Fábrica para crear unidades de combate predefinidas.
 * Proporciona plantillas estándar para criaturas comunes.
 *
 * @author Lead Developer
 * @version 3.0
 */
public class UnitFactory {

    /**
     * Crea una unidad de guardias humanos.
     */
    public static CombatUnit createHumanGuards(int count) {
        String name = count + " Guardias Humanos";
        CombatUnit unit = new CombatUnit(name, CreatureType.HUMAN_GUARD, count);
        // Los valores vienen de setDefaultStatsForCreatureType (moral Warhammer = 8)
        return unit;
    }

    /**
     * Crea una unidad de orcos.
     */
    public static CombatUnit createOrcs(int count) {
        String name = count + " Orcos";
        CombatUnit unit = new CombatUnit(name, CreatureType.ORC, count);
        // Los valores vienen de setDefaultStatsForCreatureType (moral Warhammer = 7)
        return unit;
    }

    /**
     * Crea una unidad de trasgos (goblins).
     */
    public static CombatUnit createGoblins(int count) {
        String name = count + " Trasgos";
        CombatUnit unit = new CombatUnit(name, CreatureType.GOBLIN, count);
        // Los valores vienen de setDefaultStatsForCreatureType (moral Warhammer = 5)
        return unit;
    }

    /**
     * Crea una unidad de esqueletos.
     */
    public static CombatUnit createSkeletons(int count) {
        String name = count + " Esqueletos";
        CombatUnit unit = new CombatUnit(name, CreatureType.SKELETON, count);
        // Los valores vienen de setDefaultStatsForCreatureType (moral Warhammer = 10, sin miedo)
        return unit;
    }

    /**
     * Crea una unidad de ogros.
     */
    public static CombatUnit createOgres(int count) {
        String name = count + " Ogros";
        CombatUnit unit = new CombatUnit(name, CreatureType.OGRE, count);
        // Los valores vienen de setDefaultStatsForCreatureType (moral Warhammer = 6)
        return unit;
    }

    /**
     * Crea una unidad personalizada con estadísticas completas de D&D.
     */
    public static CombatUnit createCustomUnit(
            String name,
            int count,
            int armorClass,
            int hitPointsPerCreature,
            String hitDiceFormula,
            int speedFeet,
            int reachFeet,
            int baseAttackBonus,
            int baseDamage,
            int strength,
            int dexterity,
            int constitution,
            int intelligence,
            int wisdom,
            int charisma,
            int morale,
            String primaryAttack,
            String secondaryAttack,
            String imagePath
    ) {
        CombatUnit unit = new CombatUnit(name, CreatureType.CUSTOM, count);
        unit.setArmorClass(armorClass);
        unit.setHitPointsPerCreature(hitPointsPerCreature);
        unit.setHitDiceFormula(hitDiceFormula);
        unit.setSpeedFeet(speedFeet);
        unit.setReachFeet(reachFeet);
        unit.setBaseAttackBonus(baseAttackBonus);
        unit.setBaseDamage(baseDamage);
        unit.setStrength(strength);
        unit.setDexterity(dexterity);
        unit.setConstitution(constitution);
        unit.setIntelligence(intelligence);
        unit.setWisdom(wisdom);
        unit.setCharisma(charisma);
        unit.setMorale(morale);
        unit.setPrimaryAttack(primaryAttack);
        unit.setSecondaryAttack(secondaryAttack);
        unit.setImagePath(imagePath);
        return unit;
    }

    /**
     * Crea un personaje guerrero básico.
     */
    public static Character createWarrior(String name, int level) {
        return new Character(name, CharacterClass.WARRIOR, level,
                           16, 14, 14, 10, 12, 10,  // STR, DEX, CON, INT, WIS, CHA
                           25 + (level * 7), 16, 5); // HP, AC, Attack Bonus
    }

    /**
     * Crea un personaje mago básico.
     */
    public static Character createWizard(String name, int level) {
        return new Character(name, CharacterClass.WIZARD, level,
                           9, 14, 12, 16, 13, 12,   // STR, DEX, CON, INT, WIS, CHA
                           18 + (level * 4), 12, 3); // HP, AC, Attack Bonus
    }

    /**
     * Crea un personaje clérigo básico.
     */
    public static Character createCleric(String name, int level) {
        return new Character(name, CharacterClass.CLERIC, level,
                           14, 10, 14, 10, 16, 13,   // STR, DEX, CON, INT, WIS, CHA
                           22 + (level * 6), 16, 4); // HP, AC, Attack Bonus
    }
}