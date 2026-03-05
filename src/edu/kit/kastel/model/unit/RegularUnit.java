package edu.kit.kastel.model.unit;

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
