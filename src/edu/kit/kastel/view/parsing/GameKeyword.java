package edu.kit.kastel.view.parsing;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.Command;
import edu.kit.kastel.view.CommandCreator;
import edu.kit.kastel.view.commands.Block;
import edu.kit.kastel.view.commands.BoardCommand;
import edu.kit.kastel.view.commands.Flip;
import edu.kit.kastel.view.commands.Hand;
import edu.kit.kastel.view.commands.Move;
import edu.kit.kastel.view.commands.Place;
import edu.kit.kastel.view.commands.Select;
import edu.kit.kastel.view.commands.Show;
import edu.kit.kastel.view.commands.State;
import edu.kit.kastel.view.commands.Yield;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This enum represents all available commands for the Krone von Ackarland game.
 * @author ucktt
 */
public enum GameKeyword implements Keyword<Game> {

    /**
     * the keyword for the {@link Select select} command.
     */
    SELECT(arguments -> new Select(arguments.parseField())),

    /**
     * the keyword for the {@link BoardCommand board} command.
     */
    BOARD(arguments -> new BoardCommand()),
    /**
     * the keyword for the {@link Move move} command.
     */
    MOVE(arguments -> new Move(arguments.parseField())),
    /**
     * the keyword for the {@link Flip flip} command.
     */
    FLIP(arguments -> new Flip()),
    /**
     * the keyword for the {@link Block block} command.
     */
    BLOCK(arguments -> new Block()),
    /**
     * the keyword for the {@link Show show} command.
     */
    SHOW(arguments -> new Show()),
    /**
     * the keyword for the {@link Hand hand} command.
     */
    HAND(arguments -> new Hand()),
    /**
     * the keyword for the {@link Place place} command.
     */
    PLACE(arguments -> {
        List<Integer> indexes = new ArrayList<>();
        indexes.add(arguments.parseIdx());
        indexes.addAll(arguments.parseAllIndexes());
        return new Place(indexes);
    }),
    /**
     * the keyword for the {@link State state} command.
     */
    STATE(arguments -> new State()),
    /**
     * the keyword for the {@link Yield yield} command.
     */
    YIELD(arguments -> {
        Optional<Integer> optional = arguments.parseOptionalIdx();
        return new Yield(optional.orElse(null));
    });


    private static final String VALUE_NAME_DELIMITER = "_";
    private final CommandCreator<Game> commandCreator;

    GameKeyword(CommandCreator<Game> provider) {
        this.commandCreator = provider;
    }

    @Override
    public Command<Game> create(Arguments arguments) throws InvalidArgumentException {
        return this.commandCreator.create(arguments);
    }

    @Override
    public boolean matches(String[] command) {
        String[] keywordParts = name().split(VALUE_NAME_DELIMITER);
        if (command.length < keywordParts.length) {
            return false;
        }
        for (int i = 0; i < keywordParts.length; i++) {
            if (!keywordParts[i].equalsIgnoreCase(command[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int words() {
        return name().split(VALUE_NAME_DELIMITER).length;
    }
}
