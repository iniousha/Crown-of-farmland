package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.fileio.BoardPrinter;
import edu.kit.kastel.model.ai.Printer;

/**
 * this class represents the state command.
 * @author ucktt
 */
public class State implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        handle.clearJustSelected();
        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }
        Position savedPosition = handle.getSavedPosition();
        Unit unit = null;
        if (savedPosition != null) {
            Field field = handle.getFarmlandBoard().getField(handle.getSavedPosition());
            unit = field.getUnit();
        }

        StringBuilder stringBuilder = new StringBuilder();
        String board = BoardPrinter.boardDisplay(handle);

        stringBuilder.append(Printer.stateDisplay(handle));
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(board);
        stringBuilder.append(System.lineSeparator());
        if (unit != null) {
            stringBuilder.append(Printer.displayUnit(unit, handle));
        }
        return Result.success(stringBuilder.toString());
    }
}
