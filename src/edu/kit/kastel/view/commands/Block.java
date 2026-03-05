package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.ai.Printer;
import edu.kit.kastel.view.fileio.BoardPrinter;

/**
 * this class represents the block command.
 * @author ucktt
 */
public class Block implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        Position position = handle.getSavedPosition();
        handle.clearJustSelected();

        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        if (position == null) {
            return Result.error("No field selected.");
        }

        Field field = handle.getFarmlandBoard().getField(position);
        Unit unit = field.getUnit();

        if (unit == null) {
            return Result.error("No unit on selected field.");
        }

        if (unit.getTeam() != handle.getCurrentTeam()) {
            return Result.error("Cannot block opponent's unit.");
        } else if (unit.isFarmerKing()) {
            return Result.error("unit is Farmer King.");
        } else if (unit.hasMoved() && !(((RegularUnit) unit).isBlocking())) {
            return Result.error("unit has already moved.");
        } else {
            ((RegularUnit) unit).startBlocking();
            unit.setHasMoved(true);
            stringBuilder.append(Printer.blockDisplay(unit, field));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(BoardPrinter.boardDisplay(handle));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(Printer.displayUnit(unit, handle));
            return Result.success(stringBuilder.toString());
        }
    }

}
