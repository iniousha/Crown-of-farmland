package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.fileio.BoardPrinter;
import edu.kit.kastel.view.fileio.Printer;

/**
 * this class represents the select class.
 * @author ucktt
 */
public class Select implements Command<Game> {

    private final Position position;

    /**
     * constructs a select instance with the given parameter.
     * @param position the specified position used to select
     */
    public Select(Position position) {
        this.position = position;
    }

    @Override
    public Result execute(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        Unit unit = handle.getUnitAt(this.position);
        String board = BoardPrinter.boardDisplay(handle);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(board);
        stringBuilder.append(System.lineSeparator());

        if (unit == null) {
            stringBuilder.append(Printer.noUnitDisplay());
        } else {
            stringBuilder.append(Printer.displayUnit(unit, handle));
        }
        return Result.success(stringBuilder.toString());
    }
}
