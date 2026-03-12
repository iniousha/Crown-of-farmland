package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.BoardPrinter;

/**
 * this class represents the board command.
 * @author ucktt
 */
public class BoardCommand implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        String board = BoardPrinter.boardDisplay(handle);

        return Result.success(board);
    }
}
