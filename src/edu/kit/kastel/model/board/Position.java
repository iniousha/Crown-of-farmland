package edu.kit.kastel.model.board;

/**
 * this record presents a position on the board game defined by a column and a row
 * and identifies a field in the board game.
 *
 * @param column the column index of the position
 * @param row    the row index of the position
 * @author ucktt
 */
public record Position(int column, int row) {

    private static final int MIN_COLUMN = 0;
    private static final int MAX_COLUMN = 6;
    private static final int MIN_ROW = 0;
    private static final int MAX_ROW = 6;

    /**
     * checks if the given column and row index are in bound.
     *
     * @param column the column to be checked
     * @param row    the row to be checked
     * @return true if both of the given row and column are in bound;
     *     false if they are out of bound
     */
    public static boolean isInBounds(int column, int row) {
        return column >= MIN_COLUMN && column <= MAX_COLUMN && row >= MIN_ROW && row <= MAX_ROW;
    }

    /**
     * converts a letter character into its corresponding zero based integer.
     * @param character the character to convert
     * @return a zero based index of the character
     */
    public static int convertToInteger(char character) {
        char upperCharacter = Character.toUpperCase(character);
        return upperCharacter - 'A';
    }


    @Override
    public String toString() {

        char columnChar = (char) ('A' + this.column);
        char rowChar = (char) ('1' + this.row);

        return "" + columnChar + rowChar;
    }

//    public Position getPosition(int column, int row) {
//        return new Position(column, row);
//    }

    /**
     * returns a new position created by moving the current position.
     * @param direction the direction vector of the movement
     * @return the new position after movement
     */
    public Position move(Vector2D direction) {
        return new Position(this.column + direction.horizontal(), this.row + direction.vertical());
    }

//    public Vector2D vectorTo(Position position) {
//        return null;
//    }

    /**
     * returns the distance between this position and the given other position.
     * @param otherPosition the other position to which the distance is being calculated
     * @return the manhattan distance between two positions
     */
    public int distanceTo(Position otherPosition) {
        return Math.abs(this.column - otherPosition.column)
                + Math.abs(this.row - otherPosition.row);
    }

    /**
     * checks whether this position and the specified position are adjacent.
     * @param otherPosition the given position to compare with
     * @param diagonal whether diagonal neighborhood should also be considered
     * @return true if the positions are adjacent; false if they are not
     */
    public boolean isAdjacentTo(Position otherPosition, boolean diagonal) {
        int columnSteps = Math.abs(this.column - otherPosition.column);
        int rowSteps = Math.abs(this.row - otherPosition.row);
        if (diagonal) {
            return columnSteps <= 1 && rowSteps <= 1 && (columnSteps + rowSteps > 0);
        } else {
            return columnSteps + rowSteps == 1;
        }
    }

//    public List<Position> getAdjacentPositions() {
//        return null;
//    }
//
//    public List<Position> getSurroundingPositions() {
//        return null;
//    }
}
