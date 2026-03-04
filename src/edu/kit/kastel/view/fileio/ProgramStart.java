package edu.kit.kastel.view.fileio;

import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Verbosity;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.view.SymbolSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * this utility class is responsible for initializing and starting the game.
 * @author ucktt
 */
public final class ProgramStart {

    private ProgramStart() {
    }

    /**
     * initializes the game and returns a game instance by parsing
     *     and processing the given program arguments.
     * @param arguments the command-line arguments
     * @return an initialized game instance
     * @throws ProgramStartException if the arguments are invalid or missing required values
     */
    public static Game initialize(String[] arguments) throws ProgramStartException {
        List<ArgumentValue> argumentValues = parseArgs(arguments);

        argumentValues.sort(Comparator.comparingInt(argumentValue -> argumentValue.key().getPriority()));
        return buildGame(argumentValues);
    }

    private static List<ArgumentValue> parseArgs(String[] arguments) throws ProgramStartException {
        List<ArgumentValue> list = new ArrayList<>();
        Set<ProgramArgument> seenKeys = new HashSet<>();

        for (String argument : arguments) {
            String[] split = argument.split("=", 2);
            if (split.length != 2) {
                throw new ProgramStartException("ERROR: invalid argument format" + argument);
            }
            String keyString = split[0];
            String value = split[1];

            ProgramArgument key = ProgramArgument.fromString(keyString);
            if (key == null) {
                throw new ProgramStartException("ERROR: invalid argument format" + argument);
            }

            if (!seenKeys.add(key)) {
                throw new ProgramStartException("ERROR: duplicate key" + keyString);
            }
            list.add(new ArgumentValue(key, value));
        }
        return list;
    }

    private static long parseSeed(String value) throws ProgramStartException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ProgramStartException("ERROR: invalid seed: " + value);
        }
    }

    private static Verbosity parseVerbosity(String value) throws ProgramStartException {
        if (value.equalsIgnoreCase("all")) {
            return Verbosity.ALL;
        } else if (value.equalsIgnoreCase("compact")) {
            return Verbosity.COMPACT;
        }
        throw new ProgramStartException("ERROR: invalid verbosity: " + value);
    }

    private static Game buildGame(List<ArgumentValue> argumentValues) throws ProgramStartException {
        Long seed = null;
        SymbolSet symbolSet = SymbolSet.defaultAscii();
        List<RegularUnit> availableUnits = null;
        Deck deck1 = null;
        Deck deck2 = null;
        String team1Name = "Player";
        String team2Name = "Enemy";
        Verbosity verbosity = Verbosity.ALL;

        for (ArgumentValue argumentValue : argumentValues) {
            switch (argumentValue.key()) {
                case SEED -> seed = ProgramStart.parseSeed(argumentValue.value());
                case BOARD -> symbolSet = SymbolSetReader.read(argumentValue.value());
                case UNITS -> availableUnits = UnitReader.read(argumentValue.value());
                case DECK -> {
                    deck1 = DeckReader.read(argumentValue.value(), availableUnits, true);
                    deck2 = DeckReader.read(argumentValue.value(), availableUnits, false);
                }
                case DECK1 -> deck1 = DeckReader.read(argumentValue.value(), availableUnits, true);
                case DECK2 -> deck2 = DeckReader.read(argumentValue.value(), availableUnits, true);
                case TEAM1 -> team1Name = argumentValue.value();
                case TEAM2 -> team2Name = argumentValue.value();
                case VERBOSITY -> verbosity = ProgramStart.parseVerbosity(argumentValue.value());
                default -> throw new ProgramStartException("ERROR: invalid argument: " + argumentValue.key());
            }
        }

        if (seed == null) {
            throw new ProgramStartException("ERROR: missing required argument: seed");
        }

        if (availableUnits == null) {
            throw new ProgramStartException("ERROR: missing required argument: units");
        }

        if (deck1 == null || deck2 == null) {
            throw new ProgramStartException("ERROR: missing required argument: deck or deck1 and deck2.");
        }

        Random random = new Random(seed);
        System.out.println("Use one of the following commands: select, board, move, flip, block, hand, place, show, yield, state, quit."
        );
        return new Game(team1Name, team2Name, deck1, deck2, random, verbosity, symbolSet);
    }

    /**
     * this record represents the parsed command-line argument with their corresponding keys.
     * @param key the program argument identifier
     * @param value the value associated with the argument
     */
    record ArgumentValue(ProgramArgument key, String value) {
    }

}
