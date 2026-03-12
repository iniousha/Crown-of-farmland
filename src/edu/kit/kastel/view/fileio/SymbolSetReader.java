package edu.kit.kastel.view.fileio;

import edu.kit.kastel.view.SymbolSet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * this utility class is for reading and parsing the given symbol set from a text file.
 * @author ucktt
 */
public final class SymbolSetReader {

    private static final String ERROR_FILE_NOT_FOUND = "ERROR: could not read file: %s";
    private static final String ERROR_INVALID_SYMBOL_SET_LENGTH = "ERROR: invalid symbol set length. expected 29 but found: %d.";
    private static final int MAXIMUM_SYMBOL_STRING_LENGTH = 29;

    private SymbolSetReader() {
    }

    /**
     * returns a symbol set from the given file.
     * @param file the path to the file containing symbol set definitions
     * @return the parsed symbol set
     * @throws ProgramStartException if the file cannot be read or contains invalid data
     */
    public static SymbolSet read(String file) throws ProgramStartException {
        Optional<List<String>> lines = readFile(file);
        if (lines.isEmpty() || lines.get().isEmpty()) {
            throw new ProgramStartException(ERROR_FILE_NOT_FOUND.formatted(file));
        }
        String symbolString = lines.get().getFirst();
        System.out.println(symbolString);
        if (symbolString.length() != MAXIMUM_SYMBOL_STRING_LENGTH) {
            throw new ProgramStartException(ERROR_INVALID_SYMBOL_SET_LENGTH.formatted(symbolString.length()));
        }
        return new SymbolSet(symbolString);

    }

    private static Optional<List<String>> readFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
