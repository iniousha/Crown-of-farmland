package edu.kit.kastel.model.unit;

import edu.kit.kastel.model.board.Field;

/**
 * this record represents the merge result between two units of the same team.
 * @param success true if the units were compatible and the merge happened; false otherwise
 * @param unitInField the unit located in the field
 * @param unitToPlace unit to be placed in the field
 * @param field the field where the merge happens
 * @author ucktt
 */
public record MergeResult(boolean success, Unit unitInField, RegularUnit unitToPlace, Field field) {
}
