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

    @Override
    public int getAttackPoints() {
        return this.attackPoints;
    }

    @Override
    public int getDefencePoints() {
        return this.defencePoints;
    }

    @Override
    public boolean isBlocking() {
        return this.blocking;
    }

    @Override
    public void startBlocking() {
        this.blocking = true;
    }

    @Override
    public void endBlocking() {
        this.blocking = false;
    }

}
