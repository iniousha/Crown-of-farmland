package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.duel.Duel;
import edu.kit.kastel.model.duel.DuelResult;
import edu.kit.kastel.model.merge.Merge;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.BoardFormatter;
import edu.kit.kastel.model.MessageFormatter;

/**
 * this class represents the move command.
 *
 * @author ucktt
 */
public class Move implements Command<Game> {

    Position targetPosition;

    /**
     * constructs a move instance wit the given parameter.
     *
     * @param targetPosition the position to move to
     */
    public Move(Position targetPosition) {
        this.targetPosition = targetPosition;
    }

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();

        String error = handleError(handle);
        if (error != null) {
            return Result.error(error);
        }
        Position selectedPosition = handle.getSavedPosition();
        Field selectedField = handle.getFarmlandBoard().getField(selectedPosition);
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        Unit selectedUnit = selectedField.getUnit();
        Unit targetedUnit = targetedField.getUnit();
        FarmlandBoard board = handle.getFarmlandBoard();

        stringBuilder.append(handle.endBlocking(selectedUnit));
        stringBuilder.append(handle.executeEnPlace(selectedUnit, targetPosition));
        if (selectedUnit instanceof FarmerKing
                && targetedUnit != null
                && targetedUnit.getTeam() == handle.getCurrentTeam()) {
            handle.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(handle.moveUnit(selectedUnit, selectedPosition, targetPosition));
        } else if (targetedUnit == null) {
            stringBuilder.append(handle.moveUnit(selectedUnit, selectedPosition, targetPosition));
        } else if (selectedUnit instanceof RegularUnit movingUnit) {

            if (movingUnit.getTeam() != targetedUnit.getTeam()) {

                boolean attackerWasFaceDown = !movingUnit.isFaceUp();
                boolean defenderWasFaceDown = (targetedUnit instanceof RegularUnit) && !targetedUnit.isFaceUp();

                DuelResult duelResult = Duel.executeDuel(movingUnit, targetedUnit);
                stringBuilder.append(Duel.duelExecutionDisplay(duelResult, movingUnit, targetedUnit,
                        handle, attackerWasFaceDown, defenderWasFaceDown, targetPosition));

            } else if (selectedUnit.getTeam() == targetedUnit.getTeam()) {
                stringBuilder.append(MessageFormatter.moveDisplay(selectedUnit, targetedField));
                stringBuilder.append(System.lineSeparator());
                Merge merge = new Merge(movingUnit, (RegularUnit) targetedUnit);
                stringBuilder.append(merge.mergeResult(targetedUnit, movingUnit, targetPosition, handle));
            }
        }

        stringBuilder.append(BoardFormatter.boardDisplay(handle));
        Unit unitToDisplay = board.getField(targetPosition).getUnit();

        if (unitToDisplay != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.displayUnit(unitToDisplay, handle));
        }
        return Result.success(stringBuilder.toString());
    }

    private String handleError(Game handle) {
        if (handle.hasYieldFailed()) {
            return "can only use hand or yield after failed yield";
        } else if (handle.getSavedPosition() == null) {
            return "No field selected.";
        }
        Field selectedField = handle.getFarmlandBoard().getField(handle.getSavedPosition());
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        Unit selectedUnit = selectedField.getUnit();
        FarmerKing farmerKing = handle.getCurrentTeam().getFarmerKing();
        FarmlandBoard board = handle.getFarmlandBoard();
        Position farmerKingPosition = board.findPosition(farmerKing);
        Unit targetedUnit = targetedField.getUnit();

        if (selectedUnit == null) {
            return "No units selected.";
        } else if (handle.getSavedPosition().distanceTo(targetPosition) > 1) {
            return "unit can only move one step.";
        } else if (!handle.getSavedPosition().equals(targetPosition)
                && !handle.getSavedPosition().isAdjacentTo(targetPosition, false)) {
            return "selected field not adjacent to targeted field.";
        } else if (selectedUnit.hasMoved()) {
            return "selected unit has already moved this turn.";
        } else if (targetPosition.equals(farmerKingPosition)) {
            return "You can't move to the Farmer King's position.";
        } else if (selectedUnit.equals(farmerKing)
                && targetedUnit != null
                && targetedUnit.getTeam() != handle.getCurrentTeam()) {
            return "farmer king cannot move to the opponent's position.";
        } else {
            return null;
        }
    }
}
