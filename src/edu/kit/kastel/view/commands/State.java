package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.BoardFormatter;
import edu.kit.kastel.model.MessageFormatter;

/**
 * this class represents the state command.
 * @author ucktt
 */
public class State implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error(MessageFormatter.failedYieldDisplay());
        }
        Position savedPosition = handle.getSavedPosition();
        Unit unit = null;
        if (savedPosition != null) {
            Field field = handle.getFarmlandBoard().getField(handle.getSavedPosition());
            unit = field.getUnit();
        }

        StringBuilder stringBuilder = new StringBuilder();
        String board = BoardFormatter.boardDisplay(handle);

        stringBuilder.append(MessageFormatter.stateDisplay(handle));
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(board);
        if (unit != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.displayUnit(unit, handle));
        } else if (handle.getSavedPosition() != null) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.noUnitDisplay());
        }
        return Result.success(stringBuilder.toString());
    }
}
