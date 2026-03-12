package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.BoardFormatter;
import edu.kit.kastel.model.MessageFormatter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this class represents the place command.
 *
 * @author ucktt
 */
public class Place implements Command<Game> {

    private final List<Integer> allIndexes;

    /**
     * constructs a place command with the indexes of the units tp place from hand.
     *
     * @param allIndexes the list of zero-based indexes of the units in hand to place
     */
    public Place(List<Integer> allIndexes) {
        this.allIndexes = allIndexes;
    }

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();

        Result error = handleError(handle);
        if (error != null) {
            return error;
        }

        FarmlandBoard board = handle.getFarmlandBoard();
        Field field = board.getField(handle.getSavedPosition());

        stringBuilder.append(handle.placeUnitsFromHand(allIndexes, handle.getSavedPosition()));
        handle.setSavedPosition(handle.getSavedPosition());
        stringBuilder.append(BoardFormatter.boardDisplay(handle));

        Unit unitToDisplay = field.getUnit();

        if (unitToDisplay != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.displayUnit(unitToDisplay, handle));
        } else if (handle.getSavedPosition() != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.noUnitDisplay());
        }
        return Result.success(stringBuilder.toString());
    }

    private Result handleError(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        if (handle.getSavedPosition() == null) {
            return Result.error("No field selected.");
        }

        Team currentTeam = handle.getCurrentTeam();
        FarmerKing farmerKing = currentTeam.getFarmerKing();
        FarmlandBoard board = handle.getFarmlandBoard();
        Position farmerKingPosition = board.findPosition(farmerKing);
        Field field = board.getField(handle.getSavedPosition());
        Unit unitInField = field.getUnit();

        if (currentTeam.hasSetPlace()) {
            return Result.error("already placed this turn.");
        }

        if (allIndexes.isEmpty()) {
            return Result.error("no index provided.");
        }

        Set<Integer> uniqueIndexes = new HashSet<>(allIndexes);
        if (uniqueIndexes.size() != allIndexes.size()) {
            return Result.error("cannot run duplicate indexes.");
        }

        List<RegularUnit> hand = currentTeam.getHand();

        for (int index : allIndexes) {
            if (index < 0 || index >= hand.size()) {
                return Result.error("index out of bounds.");
            }
        }

        if (!handle.getSavedPosition().isAdjacentTo(farmerKingPosition, true)) {
            return Result.error("selected field not adjacent to Farmer King.");
        }

        if (unitInField != null && unitInField.getTeam() != currentTeam) {
            return Result.error("cannot place on opponent's unit.");
        }

        if (unitInField != null && unitInField.isFarmerKing()
                && unitInField.getTeam() == currentTeam) {
            return Result.error("cannot place on your own Farmer King.");
        }
        return null;
    }
}
