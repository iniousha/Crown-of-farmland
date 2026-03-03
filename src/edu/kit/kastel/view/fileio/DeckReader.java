package edu.kit.kastel.view.fileio;

import edu.kit.kastel.model.Deck;
import edu.kit.kastel.model.unit.RegularUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * this utility class is for reading and parsing deck definitions from a text file.
 * @author ucktt
 */
public final class DeckReader {

    private static final String ERROR_FILE_NOT_FOUND = "Error, could not read file: %s";
    private static final String ERROR_DECK_SIZE_MISMATCH = "Error, invalid deck file: expected %d lines, but found %d lines in %s.";
    private static final String ERROR_INVALID_DECK_SIZE = "Error, deck must contain exactly 40 units, but found: %s ";
    private static final String ERROR_LINE_PARSE = "Error, could not parse line as Integer: %s.";

    private DeckReader() {
    }

    /**
     * returns a deck from the given file using the given list of available units.
     * @param file the path to the file containing deck definitions
     * @param units the list of available units to build the deck from
     * @return a parsed deck containing 40 units
     * @throws ProgramStartException if the file cannot be read or contains invalid data
     */
    /**
     *      * returns a deck from the given file using the given list of available units.
     * @param file the path to the file containing deck definitions
     * @param units the list of available units to build the deck from
     * @param print checks whether the deck should be printed
     * @return a parsed deck containing 40 units
     * @throws ProgramStartException if the file cannot be read or contains invalid data
     */
    public static Deck read(String file, List<RegularUnit> units, boolean print) throws ProgramStartException {
        Optional<List<String>> lines = readFile(file);

        if (lines.isEmpty()) {
            throw new ProgramStartException(ERROR_FILE_NOT_FOUND.formatted(file));
        }
        List<String> lineList = lines.get();

        if (print) {
            for (String line : lineList) {
                System.out.println(line);
            }
        }
        if (lineList.size() != units.size()) {
            throw new ProgramStartException(ERROR_DECK_SIZE_MISMATCH.formatted(units.size(), lineList.size(), file));
        }

        Deque<RegularUnit> deck = getRegularUnits(units, lineList);

        if (deck.size() != 40) {
            throw new ProgramStartException(ERROR_INVALID_DECK_SIZE.formatted(deck.size()));
        }
        return new Deck(deck);
    }

    private static Deque<RegularUnit> getRegularUnits(List<RegularUnit> units, List<String> lineList) throws ProgramStartException {
        Deque<RegularUnit> deck = new ArrayDeque<>();

        for (int i = 0; i < lineList.size(); i++) {
            try {
                int count = Integer.parseInt(lineList.get(i));
                RegularUnit unit = units.get(i);
                for (int j = 0; j < count; j++) {
                    deck.add(unit);
                }
            } catch (NumberFormatException e) {
                throw new ProgramStartException(ERROR_LINE_PARSE.formatted(lineList.get(i)));
            }
        }
        return deck;
    }


    private static Optional<List<String>> readFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}

