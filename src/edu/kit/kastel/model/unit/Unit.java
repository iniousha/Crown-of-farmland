package edu.kit.kastel.model.unit;

/**
 * this classes represents a unit in the game.
 * @author ucktt
 */
public abstract class Unit {

    private final String qualifier;
    private final String role;
    private boolean faceUp;
    private boolean hasMoved;
    private Team team;

    /**
     * constructs a unit using the specified parameters.
     * @param qualifier the first part of the unit's name
     * @param role the second part of the unit's name representing the unit's role
     * @param initialFaceUp true if the unit starts face up; false if hidden
     */
    public Unit(String qualifier, String role, boolean initialFaceUp) {
        this.qualifier = qualifier;
        this.role = role;
        this.team = null;
        this.faceUp = initialFaceUp;
    }

    /**
     * returns this unit's team.
     * @return this unit's team
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * sets the team this unit belongs to.
     * @param team the team to assign this unit to
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * returns the full name of this unit.
     * @return the name of this unit
     */
    public String getName() {
        return getQualifier() + " " + getRole();
    }

    /***
     * returns the qualifier part of this unit's name.
     * @return the qualifier part of the name
     */
    public String getQualifier() {
        return this.qualifier;
    }

    /**
     * returns the role of this unit.
     * @return the role of this unit
     */
    public String getRole() {
        return this.role;
    }

    /**
     * checks whether this unit has moved on the game board.
     * @return true if this unit has moved; false if not
     */
    public boolean hasMoved() {
        return this.hasMoved;
    }

    /**
     * sets whether this unit has moved on the game board during this turn.
     * @param hasMoved true if this unit has moved; false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

//    public boolean isMovementEnPlace() {
//        return this.hasMoved;
//    }

    /**
     * returns whether this unit is a farmerKing.
     * @return true if this unit is a farmerKing; false otherwise
     */
    public boolean isFarmerKing() {
        return false;
    }

    /**
     * checks whether this unit is not hidden.
     * @return true if this unit is not hidden; false otherwise
     */
    public boolean isFaceUp() {
        return this.faceUp;
    }

    /**
     * turns this unit face up.
     */
    public void flip() {
        this.faceUp = true;
    }
}
