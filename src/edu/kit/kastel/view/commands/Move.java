package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.duel.Duel;
import edu.kit.kastel.model.duel.DuelResult;
import edu.kit.kastel.model.merge.Merge;
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

    private static final String ERROR_WRONG_MOVEMENT = "unit can only move one step.";
    private static final String ERROR_FIELD_NOT_ADJACENT = "selected field not adjacent to targeted field.";
    private static final String ERROR_ALREADY_MOVED = "selected unit has already moved this turn.";
    private static final String ERROR_FARMER_KING_POSITION = "You can't move to the Farmer King's position.";
    private static final String ERROR_FARMER_KING_CANNOT_ATTACK = "farmer king cannot move to the opponent's position.";
    private final Position targetPosition;

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

        String error = validateMove(handle);
        if (error != null) {
            return Result.error(error);
        }
        Position selectedPosition = handle.getSavedPosition();
        Field selectedField = handle.getFarmlandBoard().getField(selectedPosition);
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        Unit selectedUnit = selectedField.getUnit();
        Unit targetedUnit = targetedField.getUnit();
        FarmlandBoard board = handle.getFarmlandBoard();

        stringBuilder.append(handle.executeEndBlocking(selectedUnit));
        stringBuilder.append(handle.executeEnPlace(selectedUnit, targetPosition));
        if (selectedUnit.isFarmerKing()
                && targetedUnit != null
                && targetedUnit.getTeam() == handle.getCurrentTeam()) {
            handle.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(handle.moveUnit(selectedUnit, selectedPosition, targetPosition));
        } else if (targetedUnit == null) {
            stringBuilder.append(handle.moveUnit(selectedUnit, selectedPosition, targetPosition));
        } else {
            if (selectedUnit.getTeam() != targetedUnit.getTeam()) {

                boolean attackerWasFaceDown = !selectedUnit.isFaceUp();
                boolean defenderWasFaceDown = (!targetedUnit.isFarmerKing()) && !targetedUnit.isFaceUp();

                DuelResult duelResult = Duel.executeDuel(selectedUnit, targetedUnit);
                stringBuilder.append(Duel.duelExecutionDisplay(duelResult, selectedUnit, targetedUnit,
                        handle, attackerWasFaceDown, defenderWasFaceDown, targetPosition));

            } else if (selectedUnit.getTeam() == targetedUnit.getTeam()) {
                stringBuilder.append(MessageFormatter.moveDisplay(selectedUnit, targetedField));
                stringBuilder.append(System.lineSeparator());
                Merge merge = new Merge(selectedUnit, targetedUnit);
                stringBuilder.append(merge.mergeResult(targetedUnit, selectedUnit, targetPosition, handle));
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

    private String validateMove(Game handle) {
        if (handle.hasYieldFailed()) {
            return MessageFormatter.failedYieldDisplay();
        } else if (handle.getSavedPosition() == null) {
            return MessageFormatter.noFieldSelectionDisplay();
        }
        Field selectedField = handle.getFarmlandBoard().getField(handle.getSavedPosition());
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        Unit selectedUnit = selectedField.getUnit();
        FarmlandBoard board = handle.getFarmlandBoard();
        Position farmerKingPosition = board.findPosition(handle.getCurrentTeam().getFarmerKing());
        Unit targetedUnit = targetedField.getUnit();
        if (selectedUnit == null) {
            return MessageFormatter.noUnitOnFieldDisplay();
        } else if (handle.getSavedPosition().distanceTo(targetPosition) > 1) {
            return ERROR_WRONG_MOVEMENT;
        } else if (!handle.getSavedPosition().equals(targetPosition)
                && !handle.getSavedPosition().isAdjacentTo(targetPosition, false)) {
            return ERROR_FIELD_NOT_ADJACENT;
        } else if (selectedUnit.hasMoved()) {
            return ERROR_ALREADY_MOVED;
        } else if (targetPosition.equals(farmerKingPosition)) {
            return ERROR_FARMER_KING_POSITION;
        } else if (selectedUnit.isFarmerKing()
                && selectedUnit.getTeam() == handle.getCurrentTeam()
                && targetedUnit != null
                && targetedUnit.getTeam() != handle.getCurrentTeam()) {
            return ERROR_FARMER_KING_CANNOT_ATTACK;
        } else {
            return null;
        }
    }
}
