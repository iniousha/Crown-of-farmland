package edu.kit.kastel.model.board;

import edu.kit.kastel.model.unit.Unit;

/**
 * this class represents a field on the board game.
 * @author ucktt
 */
public class Field {

    private final Position position;
    private Unit unit;

    /**
     * constructs the field on the board game.
     * @param position the position in which the field on the board game is made
     */
    public Field(Position position) {
        this.position = position;
        this.unit = null;
    }

    /**
     * returns the unit on the field.
     * @return the unit on the field
     */
    public Unit getUnit() {
        return this.unit;
    }

    /**
     * sets the given unit on the field.
     * @param unit the given unit to be set on the field
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * removes the unit on the field from the field.
     */
    public void removeUnit() {
        this.unit = null;
    }

    /**
     * checks if the field is empty from unit.
     * @return returns true if there is no unit on the field; false if there is a unit
     */
    public boolean isEmpty() {
        return unit == null;
    }

    /**
     * returns the position of the field.
     * @return the position of the field
     */
    public Position getPosition() {
        return this.position;
    }

}
