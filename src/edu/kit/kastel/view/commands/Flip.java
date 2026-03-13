package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.MessageFormatter;
import edu.kit.kastel.model.BoardFormatter;

/**
 * this class represents the flip command.
 * @author ucktt
 */
public class Flip implements Command<Game> {

    private static final String ERROR_FLIPPING_OPPONENT_UNIT = "Cannot flip opponent's unit";
    private static final String ERROR_UNIT_ALREADY_MOVED = "unit has already moved.";
    private static final String ERROR_UNIT_FACE_UP = "unit is face up.";

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        Position position = handle.getSavedPosition();

        if (handle.hasYieldFailed()) {
            return Result.error(MessageFormatter.failedYieldDisplay());
        }

        if (position == null) {
            return Result.error(MessageFormatter.noFieldSelectionDisplay());
        }

        Field field = handle.getFarmlandBoard().getField(handle.getSavedPosition());
        Unit unit = field.getUnit();

        if (unit == null) {
            return Result.error(MessageFormatter.noUnitOnFieldDisplay());
        }
        if (unit.getTeam() != handle.getCurrentTeam()) {
            return Result.error(ERROR_FLIPPING_OPPONENT_UNIT);
        }
        if (unit.hasMoved()) {
            return Result.error(ERROR_UNIT_ALREADY_MOVED);
        } else if (unit.isFaceUp() || unit.isFarmerKing()) {
            return Result.error(ERROR_UNIT_FACE_UP);
        } else {
            unit.flip();
            stringBuilder.append(MessageFormatter.flipDisplay(unit, field));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(BoardFormatter.boardDisplay(handle));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.displayUnit(unit, handle));
            return Result.success(stringBuilder.toString());

        }
    }
}
