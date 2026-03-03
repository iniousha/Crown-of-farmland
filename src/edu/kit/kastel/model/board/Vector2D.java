package edu.kit.kastel.model.board;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a two-dimensional integer vector which consists of a horizontal and a vertical component.
 * @param horizontal the horizontal component of the vector
 * @param vertical the vertical component of the vector
 * @author ucktt
 */
public record Vector2D(int horizontal, int vertical) {

    private static final Vector2D UP = new Vector2D(0, 1);
    private static final Vector2D RIGHT = new Vector2D(1, 0);
    private static final Vector2D DOWN = new Vector2D(0, -1);
    private static final Vector2D LEFT = new Vector2D(-1, 0);
    private static final Vector2D ZERO = new Vector2D(0, 0);
    private static final String REPRESENTATION = "(%d,%d)";

    /**
     * sums the specified vector and this vector.
     * @param vector the given vector to add
     * @return a new vector built from this vector and the specified vector
     */
    public Vector2D add(Vector2D vector) {
        return new Vector2D(this.horizontal() + vector.horizontal(), this.vertical() + vector.vertical());
    }

//    public Vector2D subtract(Vector2D vector) {
//        return new Vector2D(this.horizontal() - vector.horizontal(), this.vertical() - vector.vertical());
//    }

//    public int manhattanDistance(Vector2D vector) {
//        return Math.abs(this.horizontal() - vector.horizontal()) + Math.abs(this.vertical() - vector.vertical());
//    }

    /**
     * returns a list containing the four directions (up, right, down, left) plus the zero vector.
     * @return a list of direction vector including (0, 0)
     */
    public static List<Vector2D> getDirections() {
        List<Vector2D> directions = getFourDirections();
        directions.add(ZERO);
        return directions;
    }

    /**
     * returns a list of four directions (up, right, down, left).
     * @return a list of directions
     */
    public static List<Vector2D> getFourDirections() {
        List<Vector2D> directions = new ArrayList<>();
        directions.add(UP);
        directions.add(RIGHT);
        directions.add(DOWN);
        directions.add(LEFT);
        return directions;
    }

    @Override
    public String toString() {
        return REPRESENTATION.formatted(this.horizontal, this.vertical);
    }
}
