package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.MessageFormatter;

/**
 * this class represents the show command.
 * @author ucktt
 */
public class Show implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        if (handle.hasYieldFailed()) {
            return Result.error(MessageFormatter.failedYieldDisplay());
        }
        if (handle.getSavedPosition() == null) {
            return Result.error(MessageFormatter.noFieldSelectionDisplay());
        }
        Unit unit = handle.getUnitAt(handle.getSavedPosition());
        if (unit == null) {
            return Result.success(MessageFormatter.noUnitDisplay());
        } else {
            return Result.success(MessageFormatter.displayUnit(unit, handle));
        }
    }
}
