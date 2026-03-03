package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.fileio.BoardPrinter;
import edu.kit.kastel.view.fileio.Printer;

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

        StringBuilder stringBuilder = new StringBuilder();
        String board = BoardPrinter.boardDisplay(handle);

        stringBuilder.append(Printer.stateDisplay(handle));
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(board);

        return Result.success(stringBuilder.toString());
    }
}
