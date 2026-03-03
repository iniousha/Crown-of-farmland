package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.duel.Duel;
import edu.kit.kastel.model.duel.DuelResult;
import edu.kit.kastel.model.unit.*;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.fileio.BoardPrinter;
import edu.kit.kastel.view.fileio.Printer;

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
        handle.clearJustSelected();
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

        stringBuilder.append(handleBlocking(selectedUnit));
        stringBuilder.append(handleEnPlace(selectedUnit, handle));
        if (isSelectedUnitFarmerKing(selectedUnit, targetedUnit, handle)) {
            stringBuilder.append(selectedUnitIsFarmerKing(selectedUnit, handle));
        } else if (targetedUnit == null) {
            handle.moveUnit(selectedUnit, selectedPosition, targetPosition);
            selectedUnit.setHasMoved(true);
            stringBuilder.append(Printer.moveDisplay(selectedUnit, targetedField));
            stringBuilder.append(System.lineSeparator());

        } else if (selectedUnit instanceof RegularUnit movingUnit) {

            if (movingUnit.getTeam() != targetedUnit.getTeam()) {

                boolean attackerWasFaceDown = !movingUnit.isFaceUp();
                boolean defenderWasFaceDown = (targetedUnit instanceof RegularUnit) && !targetedUnit.isFaceUp();

                DuelResult duelResult = Duel.executeDuel(movingUnit, targetedUnit);
                stringBuilder.append(duelExecute(duelResult, movingUnit, targetedUnit, handle, attackerWasFaceDown, defenderWasFaceDown));

            } else if (selectedUnit.getTeam() == targetedUnit.getTeam()) {
                MergeResult mergeResult = handle.mergeAction(targetedUnit, movingUnit, targetPosition);
                stringBuilder.append(mergeResult.success()
                        ? Printer.successfulMergeDisplay(handle.getCurrentTeam(), mergeResult.unitInField(),
                        mergeResult.unitToPlace(), mergeResult.field())
                        : Printer.failedMergeDisplay(handle.getCurrentTeam(),
                        mergeResult.unitInField(), mergeResult.unitToPlace(), mergeResult.field()));
                stringBuilder.append(System.lineSeparator());
            }
        }

        stringBuilder.append(BoardPrinter.boardDisplay(handle));
        Unit unitToDisplay = board.getField(targetPosition).getUnit();

        if (unitToDisplay != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(Printer.displayUnit(unitToDisplay, handle));
        }
        return Result.success(stringBuilder.toString());
    }

    private String duelExecute(DuelResult duelResult, Unit unit,
                               Unit defender, Game handle,
                               boolean attackerWasFaceDown,
                               boolean defenderWasFaceDown) {
        StringBuilder stringBuilder = new StringBuilder();

        RegularUnit attacker = (RegularUnit) unit;
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        Field selectedField = handle.getFarmlandBoard().getField(handle.getSavedPosition());
        if (defender instanceof FarmerKing) {
            stringBuilder.append(Printer.duelWithFarmerKingDisplay(attacker, defender, targetedField));
        } else if (defender instanceof RegularUnit) {
            stringBuilder.append(Printer.duelWithRegularUnitDisplay(attacker, defender, targetedField));
        }
        if (attackerWasFaceDown) {
            stringBuilder.append(String.format("%s (%d/%d) was flipped on %s!",
                    attacker.getName(), attacker.getAttackPoints(),
                    attacker.getDefencePoints(), selectedField));
            stringBuilder.append(System.lineSeparator());
        }
        if (defenderWasFaceDown) {
            stringBuilder.append(String.format("%s (%d/%d) was flipped on %s!",
                    defender.getName(), ((RegularUnit) defender).getAttackPoints(),
                    ((RegularUnit) defender).getDefencePoints(), targetedField));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.attackerEliminated()) {
            handle.getFarmlandBoard().removeUnit(handle.getSavedPosition());
            stringBuilder.append(String.format("%s was eliminated!", attacker.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.defenderEliminated()) {
            handle.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(String.format("%s was eliminated!", defender.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.damagedTeam() != null) {
            Team damagedTeam = duelResult.damagedTeam();
            int damage = duelResult.damage();
            damagedTeam.takeDamage(damage);
            stringBuilder.append(String.format("%s takes %d damage!", damagedTeam.getName(), damage));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.attackerMoves()) {
            handle.moveUnit(attacker, handle.getSavedPosition(), targetPosition);
            attacker.setHasMoved(true);
            stringBuilder.append(String.format("%s moves to %s.", attacker.getName(), targetedField));
            stringBuilder.append(System.lineSeparator());
        }
        if (handle.getCurrentTeam().getLifePoints() <= 0) {
            stringBuilder.append(String.format("%s's life points dropped to 0!.", handle.getCurrentTeam().getName()));
            stringBuilder.append(System.lineSeparator());
        } else if (handle.getOpponentTeam().getLifePoints() <= 0) {
            stringBuilder.append(String.format("%s's life points dropped to 0!.", handle.getOpponentTeam().getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (handle.isGameOver()) {
            Team winnerTeam = handle.getWinner();
            stringBuilder.append(String.format("%s wins!", winnerTeam.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String handleBlocking(Unit selectedUnit) {
        StringBuilder stringBuilder = new StringBuilder();
        if (selectedUnit instanceof RegularUnit && ((RegularUnit) selectedUnit).isBlocking()) {
            stringBuilder.append(Printer.noLongerBlockDisplay(selectedUnit));
            stringBuilder.append(System.lineSeparator());
            ((RegularUnit) selectedUnit).endBlocking();
        }
        return stringBuilder.toString();
    }

    private String handleEnPlace(Unit selectedUnit, Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        Position selectedPosition = handle.getSavedPosition();
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        if (selectedPosition.equals(targetPosition)) {
            selectedUnit.setHasMoved(true);
            stringBuilder.append(Printer.moveDisplay(selectedUnit, targetedField));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private boolean isSelectedUnitFarmerKing(Unit selectedUnit, Unit targetedUnit, Game handle) {
        return selectedUnit instanceof FarmerKing
                && targetedUnit != null
                && targetedUnit.getTeam() == handle.getCurrentTeam();
    }

    private String selectedUnitIsFarmerKing(Unit selectedUnit, Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        FarmlandBoard board = handle.getFarmlandBoard();
        Field targetedField = handle.getFarmlandBoard().getField(targetPosition);
        board.removeUnit(targetPosition);
        handle.moveUnit(selectedUnit, handle.getSavedPosition(), targetPosition);
        selectedUnit.setHasMoved(true);
        stringBuilder.append(Printer.moveDisplay(selectedUnit, targetedField));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
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
