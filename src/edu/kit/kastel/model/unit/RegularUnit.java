package edu.kit.kastel.model.unit;

import edu.kit.kastel.model.MathUtil;

/**
 * this class represents a regular unit.
 * @author ucktt
 */
public class RegularUnit extends Unit {

    private final int attackPoints;
    private final int defencePoints;
    private boolean blocking;

    /**
     * constructs a regular unit using the specified parameters.
     * @param qualifier the first part of the name
     * @param role the second part of the regular unit's name representing its role
     * @param attackPoints regular unit's attack points
     * @param defencePoints regular unit's defence points
     */
    public RegularUnit(String qualifier, String role, int attackPoints, int defencePoints) {
        super(qualifier, role, false);
        this.attackPoints = attackPoints;
        this.defencePoints = defencePoints;
    }

    /**
     * determines the outcome of a merge between two regular units of the same team.
     * @param unit the other regular unit to be merged with
     * @return the new regular unit after the successful merge. If no merge happened returns null
     */
    public RegularUnit mergeWith(RegularUnit unit) {
        CompatibilityType type = checkCompatibility(unit);
        int attackPointAB;
        int defencePointAB;
        int attackPointA = this.getAttackPoints();
        int defencePointA = this.getDefencePoints();
        int attackPointB = unit.getAttackPoints();
        int defencePointB = unit.getDefencePoints();
        String mergedQualifier = unit.getQualifier() + " " + this.getQualifier();
        int g3t = MathUtil.g3t(attackPointA, defencePointA, attackPointB, defencePointB);

        if (type == CompatibilityType.INCOMPATIBLE) {
            return null;
        }

        if (type == CompatibilityType.SYMBIOTIC) {
            attackPointAB = attackPointA;
            defencePointAB = defencePointB;
            RegularUnit mergedUnit =  new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(this.getTeam());
            return mergedUnit;
        }

        if (type == CompatibilityType.CONSPIRATORIAL) {
            attackPointAB = attackPointA + attackPointB - g3t;
            defencePointAB = defencePointA + defencePointB - g3t;
            RegularUnit mergedUnit =  new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(this.getTeam());
            return  mergedUnit;
        }
        if (type == CompatibilityType.PRIME) {
            attackPointAB = attackPointA + attackPointB;
            defencePointAB = defencePointA + defencePointB;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(this.getTeam());
            return mergedUnit;
        }
        return null;
    }

    private CompatibilityType checkCompatibility(RegularUnit unit) {

        if (this.getName().equals(unit.getName())) {
            return CompatibilityType.INCOMPATIBLE;
        }
        int attackPointA = this.getAttackPoints();
        int defencePointA = this.getDefencePoints();
        int attackPointB = unit.getAttackPoints();
        int defencePointB = unit.getDefencePoints();
        int g3t = MathUtil.g3t(attackPointA, defencePointA, attackPointB, defencePointB);

        if (attackPointA != attackPointB) {
            int higherAttack;
            int lowerAttack;
            int higherDefence;
            int lowerDefence;
            if (attackPointA > defencePointA) {
                higherAttack = attackPointA;
                lowerAttack = attackPointB;
                higherDefence = defencePointA;
                lowerDefence = defencePointB;
            } else {
                higherAttack = attackPointB;
                lowerAttack = attackPointA;
                higherDefence = defencePointB;
                lowerDefence = defencePointA;
            }
            if (higherAttack == lowerDefence && lowerAttack == higherDefence) {
                return CompatibilityType.SYMBIOTIC;
            }
        }

        if (g3t > 100) {
            return CompatibilityType.CONSPIRATORIAL;
        }

        if (g3t == 100
                && ((MathUtil.isPrime(attackPointA / 100) && MathUtil.isPrime(attackPointB / 100))
                || MathUtil.isPrime(defencePointA / 100) && MathUtil.isPrime(defencePointB / 100))
        ) {
            return CompatibilityType.PRIME;
        }
        return CompatibilityType.INCOMPATIBLE;
    }

    /**
     * returns this regular unit's attack points.
     * @return this regular unit's attack points
     */
    public int getAttackPoints() {
        return this.attackPoints;
    }

    /**
     * returns this regular unit's defence points.
     * @return this regular unit's defence points
     */
    public int getDefencePoints() {
        return this.defencePoints;
    }

    /**
     * checks whether this regular unit is blocking.
     * @return true if this regular unit is blocking; false otherwise
     */
    public boolean isBlocking() {
        return this.blocking;
    }

    /**
     * sets this regular unit to blocking state.
     */
    public void startBlocking() {
        this.blocking = true;
    }

    /**
     * ends this regular unit's blocking state.
     */
    public void endBlocking() {
        this.blocking = false;
    }

}
