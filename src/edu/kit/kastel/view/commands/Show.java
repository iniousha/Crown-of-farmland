package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.view.Printer;

/**
 * this class represents the show command.
 * @author ucktt
 */
public class Show implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error("can only use hand or yield after failed yield");
        }

        if (handle.getSavedPosition() == null) {
            return Result.error("No field selected.");
        }
        Unit unit = handle.getUnitAt(handle.getSavedPosition());
        if (unit == null) {
            return Result.success(Printer.noUnitDisplay());
        } else {
            return Result.success(Printer.displayUnit(unit, handle));
        }
    }
}
