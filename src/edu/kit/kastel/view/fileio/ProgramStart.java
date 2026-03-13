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
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * this utility class is responsible for initializing and starting the game.
 *
 * @author ucktt
 */
public final class ProgramStart {

    private static final String PROGRAM_STARTING_MESSAGE = "Use one of the following commands: select,"
            + " board, move, flip, block, hand, place, show, yield, state, quit.";
    private static final int MAXIMUM_TEAM_NAME_LENGTH = 14;

    private ProgramStart() {
    }

    /**
     * initializes the game and returns a game instance by parsing
     * and processing the given program arguments.
     *
     * @param arguments the command-line arguments
     * @return an initialized game instance
     * @throws ProgramStartException if the arguments are invalid or missing required values
     */
    public static Game initialize(String[] arguments) throws ProgramStartException {
        List<ArgumentValue> argumentValues = parseArgs(arguments);

        argumentValues.sort(Comparator.comparingInt(argumentValue -> argumentValue.key().getPriority()));
        argumentIsValid(argumentValues);
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
                throw new ProgramStartException("ERROR: invalid argument format: " + argument);
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

    private static void argumentIsValid(List<ArgumentValue> argumentValues) throws ProgramStartException {
        Set<ProgramArgument> seenKeys = new HashSet<>();
        for (ArgumentValue argumentValue : argumentValues) {
            if (!seenKeys.add(argumentValue.key())) {
                throw new ProgramStartException("ERROR: duplicate key" + argumentValue.key());
            }
        }
        boolean hasDeck = seenKeys.contains(ProgramArgument.DECK);
        boolean hasDeck1 = seenKeys.contains(ProgramArgument.DECK1);
        boolean hasDeck2 = seenKeys.contains(ProgramArgument.DECK2);

        if (!seenKeys.contains(ProgramArgument.SEED)) {
            throw new ProgramStartException("ERROR: missing seed.");
        }
        if (!seenKeys.contains(ProgramArgument.UNITS)) {
            throw new ProgramStartException("ERROR: missing units file.");
        }
        if (!(hasDeck || (hasDeck1 && hasDeck2))) {
            throw new ProgramStartException("ERROR: invalid deck file.");
        }
        if (hasDeck && (hasDeck1 || hasDeck2)) {
            throw new ProgramStartException("ERROR: invalid deck file.");
        }
    }

    private static String parseTeamName(String value) throws ProgramStartException {
        if (value.length() > MAXIMUM_TEAM_NAME_LENGTH) {
            throw new ProgramStartException("ERROR: team1 name too long");
        }
        return value;
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
                case SEED -> seed = parseSeed(argumentValue.value());
                case BOARD -> symbolSet = SymbolSetReader.read(argumentValue.value());
                case UNITS -> availableUnits = UnitReader.read(argumentValue.value());
                case DECK -> {
                    deck1 = DeckReader.read(argumentValue.value(), availableUnits, true);
                    deck2 = DeckReader.read(argumentValue.value(), availableUnits, false);
                }
                case DECK1 -> deck1 = DeckReader.read(argumentValue.value(), availableUnits, true);
                case DECK2 -> deck2 = DeckReader.read(argumentValue.value(), availableUnits, true);
                case TEAM1 -> team1Name = argumentValue.value();
                case TEAM2 -> team2Name = parseTeamName(argumentValue.value());
                case VERBOSITY -> verbosity = parseVerbosity(argumentValue.value());
                default -> throw new ProgramStartException("ERROR: invalid argument: " + argumentValue.key());
            }
        }

        Random random = new Random(Objects.requireNonNull(seed));
        System.out.println(PROGRAM_STARTING_MESSAGE);
        return new Game(team1Name, team2Name, Objects.requireNonNull(deck1), Objects.requireNonNull(deck2), random, verbosity, symbolSet);
    }

    /**
     * this record represents the parsed command-line argument with their corresponding keys.
     *
     * @param key   the program argument identifier
     * @param value the value associated with the argument
     */
    private record ArgumentValue(ProgramArgument key, String value) {
    }
}
