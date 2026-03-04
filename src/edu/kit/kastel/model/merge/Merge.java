package edu.kit.kastel.model.merge;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.MathUtil;
import edu.kit.kastel.model.ai.Printer;
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
public record Merge(RegularUnit firstUnit, RegularUnit secondUnit) {

    /**
     * executes a merge between two units of the same team.
     * @param targetedUnit the unit on the targeted field
     * @param movingUnit the unit moving to the targeted field
     * @param targetPosition the position on which the merge happens
     * @param game the current game state
     * @return a formatted message describing whether the merge was successful or not
     */
    public String mergeResult(Unit targetedUnit, RegularUnit movingUnit,
                              Position targetPosition, Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        MergeResult mergeResult = mergeAction(targetedUnit, movingUnit, targetPosition, game);
        stringBuilder.append(mergeResult.success()
                ? Printer.successfulMergeDisplay(game.getCurrentTeam(), mergeResult.unitInField(),
                mergeResult.unitToPlace(), mergeResult.field())
                : Printer.failedMergeDisplay(game.getCurrentTeam(),
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
        RegularUnit thisUnit = this.firstUnit();
        RegularUnit unit = this.secondUnit();
        CompatibilityType type = checkCompatibility(thisUnit, unit);
        int attackPointAB;
        int defencePointAB;
        int attackPointA = thisUnit.getAttackPoints();
        int defencePointA = thisUnit.getDefencePoints();
        int attackPointB = unit.getAttackPoints();
        int defencePointB = unit.getDefencePoints();
        String mergedQualifier = unit.getQualifier() + " " + thisUnit.getQualifier();
        int g3t = MathUtil.g3t(attackPointA, defencePointA, attackPointB, defencePointB);

        if (type == CompatibilityType.INCOMPATIBLE) {
            return null;
        }

        if (type == CompatibilityType.SYMBIOTIC) {
            attackPointAB = attackPointA;
            defencePointAB = defencePointB;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(thisUnit.getTeam());
            return mergedUnit;
        }

        if (type == CompatibilityType.CONSPIRATORIAL) {
            attackPointAB = attackPointA + attackPointB - g3t;
            defencePointAB = defencePointA + defencePointB - g3t;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(thisUnit.getTeam());
            return mergedUnit;
        }
        if (type == CompatibilityType.PRIME) {
            attackPointAB = attackPointA + attackPointB;
            defencePointAB = defencePointA + defencePointB;
            RegularUnit mergedUnit = new RegularUnit(mergedQualifier, unit.getRole(), attackPointAB, defencePointAB);
            mergedUnit.setTeam(thisUnit.getTeam());
            return mergedUnit;
        }
        return null;
    }

    private CompatibilityType checkCompatibility(RegularUnit thisUnit, RegularUnit unit) {

        if (thisUnit.getName().equals(unit.getName())) {
            return CompatibilityType.INCOMPATIBLE;
        }
        int attackPointA = thisUnit.getAttackPoints();
        int defencePointA = thisUnit.getDefencePoints();
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
     * returns the merge result between two units of the same team.
     *
     * @param unitInField unit positioned on the selected field
     * @param unitToPlace unit to place on the selected field
     * @param position    position of the selected field
     * @return the result of the merge between the two units.
     */
    public MergeResult mergeAction(Unit unitInField, RegularUnit unitToPlace, Position position, Game game) {
        RegularUnit mergedUnit = mergeWith();
        FarmlandBoard board = game.getFarmlandBoard();
        Field field = game.getFarmlandBoard().getField(position);

        if (mergedUnit != null) {
            board.placeUnit(mergedUnit, position);
            return new MergeResult(true, unitInField, unitToPlace, field);
        } else {
            board.removeUnit(position);
            board.placeUnit(unitToPlace, position);
            return new MergeResult(false, unitInField, unitToPlace, field);
        }
    }
}
