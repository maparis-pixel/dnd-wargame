package com.dnd.wargames.units;

/**
 * Fábrica para crear unidades de combate predefinidas.
 * Proporciona plantillas estándar para criaturas comunes.
 *
 * @author Lead Developer
 * @version 2.0
 */
public class UnitFactory {

    /**
     * Crea una unidad de guardias humanos.
     */
    public static CombatUnit createHumanGuards(int count) {
        String name = count + " Guardias Humanos";
        CombatUnit unit = new CombatUnit(name, CreatureType.HUMAN_GUARD, count);

        // Ajustes específicos para guardias humanos
        unit.setArmorClass(16); // Armadura de placas
        unit.setBaseAttackBonus(3);
        unit.setBaseDamage(8); // Espada larga
        unit.setDexterity(12);
        unit.setMorale(15); // Alta moral disciplinada

        return unit;
    }

    /**
     * Crea una unidad de orcos.
     */
    public static CombatUnit createOrcs(int count) {
        String name = count + " Orcos";
        CombatUnit unit = new CombatUnit(name, CreatureType.ORC, count);

        // Ajustes específicos para orcos
        unit.setArmorClass(13); // Armadura de cuero tachonado
        unit.setBaseAttackBonus(3);
        unit.setBaseDamage(7); // Hacha grande
        unit.setDexterity(12);
        unit.setMorale(14); // Alta moral agresiva

        return unit;
    }

    /**
     * Crea una unidad de trasgos (goblins).
     */
    public static CombatUnit createGoblins(int count) {
        String name = count + " Trasgos";
        CombatUnit unit = new CombatUnit(name, CreatureType.GOBLIN, count);
        return unit; // Usa valores por defecto
    }

    /**
     * Crea una unidad de esqueletos.
     */
    public static CombatUnit createSkeletons(int count) {
        String name = count + " Esqueletos";
        CombatUnit unit = new CombatUnit(name, CreatureType.SKELETON, count);
        return unit; // Usa valores por defecto
    }

    /**
     * Crea una unidad de ogros.
     */
    public static CombatUnit createOgres(int count) {
        String name = count + " Ogros";
        CombatUnit unit = new CombatUnit(name, CreatureType.OGRE, count);
        return unit; // Usa valores por defecto (incluye dureza 3)
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