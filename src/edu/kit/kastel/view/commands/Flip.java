package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.ai.Printer;

/**
 * this class represents the flip command.
 * @author ucktt
 */
public class Flip implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        Position position = handle.getSavedPosition();
        handle.clearJustSelected();

        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        if (position == null) {
            return Result.error("No field selected.");
        }

        Field field = handle.getFarmlandBoard().getField(handle.getSavedPosition());
        Unit unit = field.getUnit();

        if (unit == null) {
            return Result.error("No unit on selected field.");
        }
        if (unit.getTeam() != handle.getCurrentTeam()) {
            return Result.error("Cannot flip opponent's unit.");
        }
        if (unit.hasMoved()) {
            return Result.error("unit has already moved.");
        } else if (unit.isFaceUp() || unit.isFarmerKing()) {
            return Result.error("unit is face up.");
        } else {
            unit.flip();
            return Result.success(Printer.flipDisplay(unit, field));

        }
    }
}
