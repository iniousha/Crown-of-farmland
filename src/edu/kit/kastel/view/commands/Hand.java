package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.MessageFormatter;

import java.util.List;

/**
 * this class represents the hand command.
 * @author ucktt
 */
public class Hand implements Command<Game> {

    @Override
    public Result execute(Game handle) {
        List<RegularUnit> hand = handle.hand();
        return Result.success(MessageFormatter.handToString(hand));
    }
}
