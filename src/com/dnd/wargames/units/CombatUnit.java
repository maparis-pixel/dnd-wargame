package com.dnd.wargames.units;

/**
 * Representa una unidad de combate compuesta por múltiples criaturas del mismo tipo.
 * Sistema Warhammer: HP por compañía, formación frente/flancos, moral 2d6.
 *
 * @author Lead Developer
 * @version 3.0
 */
public class CombatUnit {
    // Identidad
    private String id;
    private String name;            // Ej: "Compañía de 15 Trasgos"
    private CreatureType creatureType;    // GOBLIN, SKELETON, ORC, etc

    // Puntos de Golpe (HP) - Sistema por compañía
    private int hitPoints;          // HP actual (suma total de la compañía)
    private int maxHitPoints;       // HP máximo inicial
    private int creaturesCount;     // Número de criaturas en la compañía
    private int hitPointsPerCreature; // HP individual de cada criatura

    // Estadísticas de Combate
    private int armorClass;         // CA de criatura individual
    private int baseAttackBonus;    // Bono de ataque base
    private int baseDamage;         // Daño promedio por criatura
    private int dexterity;          // Para iniciativa
    private int strength;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;
    private String hitDiceFormula;
    private int speedFeet;
    private String primaryAttack;
    private String secondaryAttack;
    private int reachFeet;          // Alcance del arma (5ft estándar, 10ft reach)

    // Formación (Warhammer-style)
    private int frontWidth;         // Ancho del frente (criaturas)
    private int flankExposure;      // Exposición de flancos (criaturas)
    private int rowsAttacking;      // Filas que pueden atacar (2 base, 3 si reach 10ft)

    // Moral Warhammer
    private MoraleEffect moraleStatus;  // NONE, FRIGHTENED, CONFUSED, RAGING
    private int morale;                 // Valor Warhammer 2-12
    private boolean hasStandardBearer;  // Porta estandarte
    private boolean hasFledBattle;      // Ha huido de la batalla

    /**
     * Constructor básico para una unidad de combate.
     */
    public CombatUnit(String name, CreatureType creatureType, int creaturesCount) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.creatureType = creatureType;
        this.creaturesCount = creaturesCount;

        // Valores por defecto según el tipo de criatura
        setDefaultStatsForCreatureType();

        // Calcular HP total de la compañía
        this.hitPoints = creaturesCount * hitPointsPerCreature;
        this.maxHitPoints = this.hitPoints;

        // Calcular formación básica (cuadrado aproximado)
        calculateFormation();

        this.moraleStatus = MoraleEffect.NONE;
        this.hasStandardBearer = (creaturesCount >= 10); // Porta estandarte si >= 10 criaturas
        this.hasFledBattle = false;
    }

    /**
     * Constructor completo para una unidad de combate.
     */
    public CombatUnit(String name, CreatureType creatureType, int creaturesCount,
                     int armorClass, int baseAttackBonus, int baseDamage, int dexterity,
                     int morale) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.creatureType = creatureType;
        this.creaturesCount = creaturesCount;

        this.armorClass = armorClass;
        this.baseAttackBonus = baseAttackBonus;
        this.baseDamage = baseDamage;
        this.dexterity = dexterity;
        this.morale = morale;

        // Establecer valores por defecto y calcular HP
        setDefaultStatsForCreatureType();
        this.hitPoints = creaturesCount * hitPointsPerCreature;
        this.maxHitPoints = this.hitPoints;

        // Calcular formación
        calculateFormation();

        this.moraleStatus = MoraleEffect.NONE;
        this.hasStandardBearer = (creaturesCount >= 10);
        this.hasFledBattle = false;
    }

    /**
     * Calcula la formación de la unidad (frente, flancos, filas atacantes).
     * Por defecto: cuadrado o rectángulo aproximado según tamaño.
     */
    private void calculateFormation() {
        if (creaturesCount <= 0) {
            frontWidth = 0;
            flankExposure = 0;
            rowsAttacking = 0;
            return;
        }

        // Formación cuadrada aproximada
        int sqrtSize = (int) Math.ceil(Math.sqrt(creaturesCount));
        this.frontWidth = Math.min(sqrtSize, creaturesCount);
        int depth = (int) Math.ceil((double) creaturesCount / frontWidth);
        this.flankExposure = Math.min(depth, creaturesCount);

        // Rows attacking: 2 base, 3 si reach >= 10ft
        this.rowsAttacking = (reachFeet >= 10) ? 3 : 2;
    }

    /**
     * Establece estadísticas por defecto según el tipo de criatura.
     */
    private void setDefaultStatsForCreatureType() {
        switch (creatureType) {
            case GOBLIN:
                this.armorClass = 15;
                this.baseAttackBonus = 4;
                this.baseDamage = 5;
                this.strength = 8;
                this.dexterity = 14;
                this.constitution = 10;
                this.intelligence = 10;
                this.wisdom = 8;
                this.charisma = 8;
                this.morale = 5; // Warhammer: cobardes
                this.hitPointsPerCreature = 7;
                this.hitDiceFormula = "2d6";
                this.speedFeet = 30;
                this.primaryAttack = "Scimitar +4, daño medio 5 (1d6+2)";
                this.secondaryAttack = "Shortbow +4, daño medio 5 (1d6+2)";
                this.reachFeet = 5;
                break;
            case ORC:
                this.armorClass = 13;
                this.baseAttackBonus = 5;
                this.baseDamage = 9;
                this.strength = 16;
                this.dexterity = 12;
                this.constitution = 16;
                this.intelligence = 7;
                this.wisdom = 11;
                this.charisma = 10;
                this.morale = 7; // Warhammer: agresivos pero no disciplinados
                this.hitPointsPerCreature = 15;
                this.hitDiceFormula = "2d8+6";
                this.speedFeet = 30;
                this.primaryAttack = "Greataxe +5, daño medio 9 (1d12+3)";
                this.secondaryAttack = "Javelin +5, daño medio 6 (1d6+3)";
                this.reachFeet = 5;
                break;
            case SKELETON:
                this.armorClass = 13;
                this.baseAttackBonus = 4;
                this.baseDamage = 5;
                this.strength = 10;
                this.dexterity = 14;
                this.constitution = 15;
                this.intelligence = 6;
                this.wisdom = 8;
                this.charisma = 5;
                this.morale = 10; // Warhammer: sin miedo
                this.hitPointsPerCreature = 13;
                this.hitDiceFormula = "2d8+4";
                this.speedFeet = 30;
                this.primaryAttack = "Shortsword +4, daño medio 5 (1d6+2)";
                this.secondaryAttack = "Shortbow +4, daño medio 5 (1d6+2)";
                this.reachFeet = 5;
                break;
            case OGRE:
                this.armorClass = 11;
                this.baseAttackBonus = 6;
                this.baseDamage = 13;
                this.strength = 19;
                this.dexterity = 8;
                this.constitution = 16;
                this.intelligence = 5;
                this.wisdom = 7;
                this.charisma = 7;
                this.morale = 6; // Warhammer: estúpidos pero fuertes
                this.hitPointsPerCreature = 59;
                this.hitDiceFormula = "7d10+21";
                this.speedFeet = 40;
                this.primaryAttack = "Greatclub +6, daño medio 13 (2d8+4)";
                this.secondaryAttack = "Javelin +6, daño medio 11 (2d6+4)";
                this.reachFeet = 10; // Criatura grande
                break;
            case HUMAN_GUARD:
                this.armorClass = 16; // Armadura de placas
                this.baseAttackBonus = 3;
                this.baseDamage = 5;
                this.strength = 13;
                this.dexterity = 12;
                this.constitution = 12;
                this.intelligence = 10;
                this.wisdom = 11;
                this.charisma = 10;
                this.morale = 8; // Warhammer: disciplinados
                this.hitPointsPerCreature = 11;
                this.hitDiceFormula = "2d8+2";
                this.speedFeet = 30;
                this.primaryAttack = "Spear +3, daño medio 4 (1d6+1)";
                this.secondaryAttack = "Spear (dos manos) daño medio 5 (1d8+1)";
                this.reachFeet = 5;
                break;
            default:
                // Valores por defecto
                this.armorClass = 13;
                this.baseAttackBonus = 2;
                this.baseDamage = 5;
                this.strength = 10;
                this.dexterity = 12;
                this.constitution = 10;
                this.intelligence = 10;
                this.wisdom = 10;
                this.charisma = 10;
                this.morale = 7;
                this.hitPointsPerCreature = 8;
                this.hitDiceFormula = "2d6";
                this.speedFeet = 30;
                this.primaryAttack = "Ataque básico";
                this.secondaryAttack = "Sin ataque secundario";
                this.reachFeet = 5;
                break;
        }
    }

    /**
     * Calcula el número de ataques disponibles según formación.
     * Base: frontWidth * rowsAttacking
     */
    public int getAttacksAvailable() {
        return frontWidth * rowsAttacking;
    }

    /**
     * Aplica daño a la unidad en HP.
     */
    public void takeDamage(int damage) {
        hitPoints = Math.max(0, hitPoints - damage);
        
        // Actualizar contador de criaturas según HP restante
        creaturesCount = (int) Math.ceil((double) hitPoints / hitPointsPerCreature);
        
        // Recalcular formación si perdemos criaturas
        calculateFormation();
        
        // Chequear si se perdió el porta estandarte (25% probabilidad si hay bajas)
        if (hasStandardBearer && hitPoints < maxHitPoints * 0.75) {
            // Simplificado: se pierde el estandarte al 50% de bajas
            if (hitPoints <= maxHitPoints / 2) {
                hasStandardBearer = false;
            }
        }
    }

    /**
     * Chequeo de moral Warhammer (2d6 vs valor de moral).
     * Se realiza cuando:
     * - El porta estandarte cae (hasStandardBearer pasa a false)
     * - Se alcanzan 50% o más de bajas
     * 
     * @param diceRoll resultado de 2d6
     * @return true si pasa el chequeo, false si huye
     */
    public boolean checkMorale(int diceRoll) {
        if (hasFledBattle) {
            return false; // Ya ha huido
        }
        
        // Skeleton y similares pueden tener inmunidad al miedo
        if (creatureType == CreatureType.SKELETON && morale >= 10) {
            return true; // Sin miedo
        }
        
        boolean passed = diceRoll <= morale;
        
        if (!passed) {
            hasFledBattle = true;
            moraleStatus = MoraleEffect.FRIGHTENED;
        }
        
        return passed;
    }

    /**
     * Actualiza el estado de moral según situación actual.
     */
    private void updateMoraleStatus() {
        if (!isAlive() || hasFledBattle) {
            moraleStatus = MoraleEffect.FRIGHTENED;
            return;
        }

        double hpRatio = (double) hitPoints / (double) maxHitPoints;

        if (hpRatio <= 0.25) {
            moraleStatus = MoraleEffect.FRIGHTENED;
        } else if (hpRatio <= 0.50) {
            moraleStatus = MoraleEffect.CONFUSED;
        } else if (morale >= 9 && hpRatio >= 0.80) {
            moraleStatus = MoraleEffect.RAGING;
        } else {
            moraleStatus = MoraleEffect.NONE;
        }
    }

    /**
     * Verifica si la unidad está viva (tiene HP > 0 y no ha huido).
     */
    public boolean isAlive() {
        return hitPoints > 0 && !hasFledBattle;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public CreatureType getCreatureType() { return creatureType; }
    public int getHitPoints() { return hitPoints; }
    public int getMaxHitPoints() { return maxHitPoints; }
    public int getCreaturesCount() { return creaturesCount; }
    public int getArmorClass() { return armorClass; }
    public int getBaseAttackBonus() { return baseAttackBonus; }
    public int getBaseDamage() { return baseDamage; }
    public int getStrength() { return strength; }
    public int getDexterity() { return dexterity; }
    public int getConstitution() { return constitution; }
    public int getIntelligence() { return intelligence; }
    public int getWisdom() { return wisdom; }
    public int getCharisma() { return charisma; }
    public int getHitPointsPerCreature() { return hitPointsPerCreature; }
    public String getHitDiceFormula() { return hitDiceFormula; }
    public int getSpeedFeet() { return speedFeet; }
    public String getPrimaryAttack() { return primaryAttack; }
    public String getSecondaryAttack() { return secondaryAttack; }
    public int getReachFeet() { return reachFeet; }
    public int getFrontWidth() { return frontWidth; }
    public int getFlankExposure() { return flankExposure; }
    public int getRowsAttacking() { return rowsAttacking; }
    public MoraleEffect getMoraleStatus() { return moraleStatus; }
    public int getMorale() { return morale; }
    public boolean hasStandardBearer() { return hasStandardBearer; }
    public boolean hasFledBattle() { return hasFledBattle; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setArmorClass(int armorClass) { this.armorClass = armorClass; }
    public void setBaseAttackBonus(int baseAttackBonus) { this.baseAttackBonus = baseAttackBonus; }
    public void setBaseDamage(int baseDamage) { this.baseDamage = baseDamage; }
    public void setStrength(int strength) { this.strength = strength; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
    public void setConstitution(int constitution) { this.constitution = constitution; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public void setWisdom(int wisdom) { this.wisdom = wisdom; }
    public void setCharisma(int charisma) { this.charisma = charisma; }
    public void setHitPointsPerCreature(int hitPointsPerCreature) { 
        this.hitPointsPerCreature = hitPointsPerCreature;
        // Recalcular HP total si cambia HP individual
        this.hitPoints = Math.min(hitPoints, creaturesCount * hitPointsPerCreature);
        this.maxHitPoints = creaturesCount * hitPointsPerCreature;
    }
    public void setHitDiceFormula(String hitDiceFormula) { this.hitDiceFormula = hitDiceFormula; }
    public void setSpeedFeet(int speedFeet) { this.speedFeet = speedFeet; }
    public void setPrimaryAttack(String primaryAttack) { this.primaryAttack = primaryAttack; }
    public void setSecondaryAttack(String secondaryAttack) { this.secondaryAttack = secondaryAttack; }
    public void setReachFeet(int reachFeet) { 
        this.reachFeet = reachFeet;
        calculateFormation(); // Recalcular filas atacantes
    }
    public void setFrontWidth(int frontWidth) { this.frontWidth = frontWidth; }
    public void setFlankExposure(int flankExposure) { this.flankExposure = flankExposure; }
    public void setMoraleStatus(MoraleEffect moraleStatus) { this.moraleStatus = moraleStatus; }
    public void setMorale(int morale) { this.morale = morale; }
    public void setHasStandardBearer(boolean hasStandardBearer) { this.hasStandardBearer = hasStandardBearer; }

    public String toStatsString() {
        return String.format(
                "%s | HP %d/%d (%d criaturas) | AC %d | HP/criatura %d (%s) | Vel %d ft | Alcance %d ft | " +
                "Formación: Frente %d, Flancos %d, Filas ataque %d | " +
                "STR %d DEX %d CON %d INT %d WIS %d CHA %d | " +
                "Atk %s | Sec %s | Moral %d (%s) | Estandarte: %s",
                name,
                hitPoints,
                maxHitPoints,
                creaturesCount,
                armorClass,
                hitPointsPerCreature,
                hitDiceFormula,
                speedFeet,
                reachFeet,
                frontWidth,
                flankExposure,
                rowsAttacking,
                strength,
                dexterity,
                constitution,
                intelligence,
                wisdom,
                charisma,
                primaryAttack,
                secondaryAttack,
                morale,
                moraleStatus,
                hasStandardBearer ? "Sí" : "No"
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%d/%d HP, %d criaturas) - CA: %d, Moral: %s",
                           name, hitPoints, maxHitPoints, creaturesCount, armorClass, moraleStatus);
    }
}