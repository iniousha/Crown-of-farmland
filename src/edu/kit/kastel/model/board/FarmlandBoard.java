package edu.kit.kastel.model.board;

import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.SymbolSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the board of the game and provides methods related to board operations.
 *
 * @author ucktt
 */
public class FarmlandBoard {

    private final Field[][] fields;
    private final SymbolSet symbolSet;

    /**
     * constructs a 7X7 board using the given symbolSet.
     *
     * @param symbolSet a set of symbols used to build the board
     */
    public FarmlandBoard(SymbolSet symbolSet) {
        this.symbolSet = symbolSet;
        int column = 7;
        int row = 7;
        this.fields = new Field[row][column];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                fields[i][j] = new Field(new Position(j, i));
            }
        }

    }

    /**
     * returns the field located at the given position.
     *
     * @param position the position given for finding the board
     * @return the field at the given position
     */
    public Field getField(Position position) {
        int column = position.column();
        int row = position.row();
        return fields[row][column];
    }

    /**
     * returns the neighboring positions of the given position.
     *
     * @param position the position used to find its neighbors
     * @param diagonal whether diagonal neighbors should be included
     * @return a list of neighboring positions
     */
    public List<Position> getNeighbors(Position position, boolean diagonal) {
        List<Position> neighbors = new ArrayList<>();
        List<Vector2D> directions = diagonal ? Vector2D.getEightDirections() : Vector2D.getFourDirections();

        for (Vector2D vector : directions) {
            Position neighbor = position.move(vector);
            if (Position.isInBounds(neighbor.column(), neighbor.row())) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * places the specified unit at the given position.
     *
     * @param unit     the unit to be placed
     * @param position the given position where the unit is to be placed
     */
    public void placeUnit(Unit unit, Position position) {
        Field field = getField(position);
        field.setUnit(unit);
    }

    /**
     * removes the unit located at the specified position.
     *
     * @param position the specified position from which the unit is to be removed
     */
    public void removeUnit(Position position) {
        Field field = getField(position);
        field.removeUnit();
    }

    /**
     * checks it the field at the specified position is empty.
     *
     * @param position the specified position to check
     * @return true if the field is empty; false if there is a unit at the field
     */
    public boolean isEmpty(Position position) {
        Field field = getField(position);
        return field.isEmpty();
    }

    /**
     * returns a list containing all the fields on the board.
     *
     * @return a list of all the fields on the board
     */
    public List<Field> getAllFields() {
        List<Field> allFields = new ArrayList<>();
        for (int row = 0; row < 7; row++) {
            allFields.addAll(Arrays.asList(fields[row]));
        }
        return allFields;
    }

    /**
     * returns a list of all the units belonging to the specified team.
     *
     * @param team the specified team whose units are to be retrieved
     * @return a list of all units of the specified team
     */
    public List<Unit> getUnitsForTeam(Team team) {
        List<Field> allFields = getAllFields();
        List<Unit> units = new ArrayList<>();
        for (Field field : allFields) {
            Unit unit = field.getUnit();
            if (unit != null && unit.getTeam() == team) {
                units.add(unit);
            }
        }
        return units;
    }

    /**
     * returns the number of units belonging to the specified team.
     *
     * @param team the team whose units are to be counted
     * @return number of units in the specified team
     */
    public int unitCount(Team team) {
        return getUnitsForTeam(team).size() - 1;
    }

    /**
     * returns the set of symbols used for building the board.
     *
     * @return the symbolSet for the board
     */
    public SymbolSet getSymbolSet() {
        return this.symbolSet;
    }

    /**
     * finds the position of the given unit.
     *
     * @param unit the unit whose position is to be found
     * @return position of the given unit
     */
    public Position findPosition(Unit unit) {
        for (Field field : getAllFields()) {
            if (field.getUnit() == unit) {
                return field.getPosition();
            }
        }
        return null;
    }
}
