package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.MessageFormatter;
import edu.kit.kastel.model.BoardFormatter;

/**
 * this class represents the block command.
 * @author ucktt
 */
public class Block implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        Position position = handle.getSavedPosition();

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
        } else if ((handle.isBlockedThisTurn())) {
            return Result.error("unit is already Blocking this Turn.");
        } else {
            unit.startBlocking();
            handle.setBlockedThisTurn(true);
            unit.setHasMoved(true);
            stringBuilder.append(MessageFormatter.blockDisplay(unit, field));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(BoardFormatter.boardDisplay(handle));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(MessageFormatter.displayUnit(unit, handle));
            return Result.success(stringBuilder.toString());
        }
    }

}
