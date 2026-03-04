package edu.kit.kastel.view.fileio;

import edu.kit.kastel.model.unit.RegularUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * this utility class is for reading and parsing unit definitions from a text file.
 * @author ucktt
 */
public final class UnitReader {

    private static final String ERROR_FILE_NOT_FOUND = "ERROR: could not read file: %s";
    private static final String ERROR_INVALID_UNIT_FORMAT = "ERROR: invalid unit format in line %s.";
    private static final String ERROR_INVALID_NUMBER_FORMAT = "ERROR: invalid number format in line %s ";

    private UnitReader() {
    }

    /**
     * returns a list of regular units from the given file.
     * @param file the path to the file containing unit definitions
     * @return a list of parsed regular units
     * @throws ProgramStartException if the file cannot be read or contains invalid data
     */
    public static List<RegularUnit> read(String file) throws ProgramStartException {
        Optional<List<String>> lines = readFile(file);
        if (lines.isEmpty()) {
            throw new ProgramStartException(ERROR_FILE_NOT_FOUND.formatted(file));
        }

        List<RegularUnit> units = new ArrayList<>();
        for (String line : lines.get()) {
            System.out.println(line);
            String[] split = line.split(";", 4);
            if (split.length != 4) {
                throw new ProgramStartException(ERROR_INVALID_UNIT_FORMAT.formatted(line));
            }
            try {
                String unitQualifier = split[0];
                String unitRole = split[1];
                int attackPoint = Integer.parseInt(split[2]);
                int defencePoint = Integer.parseInt(split[3]);
                units.add(new RegularUnit(unitQualifier, unitRole, attackPoint, defencePoint));
            } catch (NumberFormatException e) {
                throw new ProgramStartException(ERROR_INVALID_NUMBER_FORMAT.formatted(line));
            }
        }
        return units;
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
} //regex101
