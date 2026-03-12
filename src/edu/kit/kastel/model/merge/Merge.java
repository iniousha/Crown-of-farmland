package edu.kit.kastel.model.merge;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.MathUtil;
import edu.kit.kastel.model.MessageFormatter;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;

/**
 * this record provides methods relating the merge between two units of the same team.
 * @param firstUnit the first unit
 * @param secondUnit the second unit
 * @author ucktt
 */
public record Merge(Unit firstUnit, Unit secondUnit) {

    /**
     * executes a merge between two units of the same team.
     * @param unitInField the unit on the targeted field
     * @param unitToPlace the unit moving to the targeted field
     * @param targetPosition the position on which the merge happens
     * @param game the current game state
     * @return a formatted message describing whether the merge was successful or not
     */
    public String mergeResult(Unit unitInField, Unit unitToPlace,
                              Position targetPosition, Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        MergeResult mergeResult = mergeAction(unitInField, unitToPlace, targetPosition, game);
        stringBuilder.append(mergeResult.success()
                ? MessageFormatter.successfulMergeDisplay(mergeResult.unitInField(),
                mergeResult.unitToPlace(), mergeResult.field())
                : MessageFormatter.failedMergeDisplay(
                mergeResult.unitInField(), mergeResult.unitToPlace(), mergeResult.field()));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }

    /**
     * determines the outcome of a merge between two regular units of the same team.
     *
     * @return the new regular unit after the successful merge. If no merge happened returns null
     */
    public RegularUnit mergeWith() {
        Unit unitToPlace = this.firstUnit();
        Unit targetUnit = this.secondUnit();
        CompatibilityType type = checkCompatibility(unitToPlace, targetUnit);
        int attackPointAB;
        int defencePointAB;
        int attackPointA = unitToPlace.getAttackPoints();
        int defencePointA = unitToPlace.getDefencePoints();
        int attackPointB = targetUnit.getAttackPoints();
        int defencePointB = targetUnit.getDefencePoints();
        String mergedQualifier = targetUnit.getQualifier() + " " + unitToPlace.getQualifier();
        int g3t = MathUtil.g3t(attackPointA, defencePointA, attackPointB, defencePointB);

        if (type == CompatibilityType.INCOMPATIBLE) {
            return null;
        }

        if (type == CompatibilityType.SYMBIOTIC) {
            attackPointAB = attackPointA;
            defencePointAB = defencePointB;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, targetUnit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(unitToPlace.getTeam());
            return mergedUnit;
        }

        if (type == CompatibilityType.CONSPIRATORIAL) {
            attackPointAB = attackPointA + attackPointB - g3t;
            defencePointAB = defencePointA + defencePointB - g3t;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, targetUnit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(unitToPlace.getTeam());
            return mergedUnit;
        }
        if (type == CompatibilityType.PRIME) {
            attackPointAB = attackPointA + attackPointB;
            defencePointAB = defencePointA + defencePointB;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, targetUnit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(unitToPlace.getTeam());
            return mergedUnit;
        }
        return null;
    }

    private CompatibilityType checkCompatibility(Unit unitToPlace, Unit unitInField) {

        if (unitToPlace.getName().equals(unitInField.getName())) {
            return CompatibilityType.INCOMPATIBLE;
        }
        int attackPointA = unitToPlace.getAttackPoints();
        int defencePointA = unitToPlace.getDefencePoints();
        int attackPointB = unitInField.getAttackPoints();
        int defencePointB = unitInField.getDefencePoints();
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
     * returns the merge result between two units of the same team.
     *
     * @param unitInField unit positioned on the selected field
     * @param unitToPlace unit to place on the selected field
     * @param position    position of the selected field
     * @return the result of the merge between the two units.
     */
    public MergeResult mergeAction(Unit unitInField, Unit unitToPlace, Position position, Game game) {
        RegularUnit mergedUnit = mergeWith();
        FarmlandBoard board = game.getFarmlandBoard();
        Field field = game.getFarmlandBoard().getField(position);

        if (mergedUnit != null) {
            board.placeUnit(mergedUnit, position);
            Position unitToPlacePosition = board.findPosition(unitToPlace);
            if (unitToPlacePosition != null) {
                board.removeUnit(unitToPlacePosition);
            }
            game.setSavedPosition(position);
            return new MergeResult(true, unitInField, unitToPlace, field);
        } else {
            board.removeUnit(position);
            board.placeUnit(unitToPlace, position);
            return new MergeResult(false, unitInField, unitToPlace, field);
        }
    }
}
