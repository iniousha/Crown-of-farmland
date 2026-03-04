package edu.kit.kastel.view.commands;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.ai.AiTurn;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.Result;
import edu.kit.kastel.model.ai.Printer;

import java.util.List;

/**
 * this class represents the yield command.
 *
 * @author ucktt
 */
public class Yield implements Command<Game> {

    private final Integer optionalIdx;

    /**
     * constructs a yield instance using optional card index to discard.
     *
     * @param optionalIdx the optional index of the card to discard, or null if
     *                    no card is to be discarded
     */
    public Yield(Integer optionalIdx) {
        this.optionalIdx = optionalIdx;
    }

    @Override
    public Result execute(Game handle) {
        StringBuilder stringBuilder = new StringBuilder();
        Team currentTeam = handle.getCurrentTeam();
        List<RegularUnit> hand = currentTeam.getHand();
        handle.clearJustSelected();

        if (hand.size() == 5 && optionalIdx == null) {
            handle.setYieldFailed(true);
            return Result.error("player's hand is full!");
        } else if (hand.size() < 5 && optionalIdx != null) {
            handle.setYieldFailed(true);
            return Result.error("cannot discard when hand is not full!");
        } else {
            if (optionalIdx != null) {
                Unit removedUnit = currentTeam.removeUnitFromHand(optionalIdx);
                stringBuilder.append(Printer.discardedCardDisplay(currentTeam, removedUnit));
                stringBuilder.append(System.lineSeparator());
            }

            handle.nextTurn();
            if (handle.getCurrentTeam().isAiTeam()) {
                stringBuilder.append(Printer.turnDisplay(handle.getCurrentTeam()));
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(new AiTurn(handle, handle.getRandom()).executeTurn());
                handle.nextTurn();
            }

            stringBuilder.append(Printer.turnDisplay(handle.getCurrentTeam()));
            if (handle.isGameOver()) {
                Team winnerTeam = handle.getWinner();
                Team loserTeam = handle.getCurrentTeam();
                stringBuilder.append(Printer.deckEmptyDisplay(loserTeam));
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append(Printer.winnerDisplay(winnerTeam));
            }
            return Result.success(stringBuilder.toString());
        }
    }
}
