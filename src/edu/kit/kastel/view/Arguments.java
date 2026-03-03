package edu.kit.kastel.view;

import edu.kit.kastel.model.board.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This classes has methods which parse arguments that the user has types as inputs.
 * @author ucktt
 */
public class Arguments {

    private static final String ERROR_TOO_FEW_ARGUMENTS = "Error, too few arguments";
    private static final String ERROR_NOT_NUMBER_FORMAT = "Error, '%s' must be an integer.";
    private static final String ERROR_NOT_IN_BOUNDS = "Error, position not in bounds.";
    private static final int INDEX_OFFSET = 1;
    private static final int POSITION_ARGUMENT_LENGTH = 2;
    private static final String ERROR_INVALID_FIELD_FORMAT = "Error, invalid field format.";
    private static final int COLUMN_CHAR_INDEX = 0;
    private static final int ROW_CHAR_INDEX = 1;
    private static final char ROW_OFFSET = '1';


    private final String[] arguments;
    private int argumentIndex;

    /**
     * constructs a new arguments instance.
     * @param arguments the arguments to parse
     */
    public Arguments(String[] arguments) {
        this.arguments = arguments.clone();
    }

    /**
     * Returns whether the provided arguments have all been taken.
     * @return true if all arguments have been taken.
     */
    public boolean isExhausted() {
        return argumentIndex >= arguments.length;
    }

    private String retrieveArgument() throws InvalidArgumentException {
        if (isExhausted()) {
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS);
        }
        return arguments[argumentIndex++];
    }

    /**
     * Parses the field as a Position.
     * @return the arguments as a position
     * @throws InvalidArgumentException if there is no argument to parse or if hte argument could not get parsed.
     */
    public Position parseField() throws InvalidArgumentException {
        String argument = retrieveArgument();

        if (argument.length() != POSITION_ARGUMENT_LENGTH) {
            throw new InvalidArgumentException(String.format(ERROR_INVALID_FIELD_FORMAT));
        }

        char firstChar = argument.charAt(COLUMN_CHAR_INDEX);
        char secondChar = argument.charAt(ROW_CHAR_INDEX);

        int column = Position.convertToInteger(firstChar);
        int row = secondChar - ROW_OFFSET;
        if (!Position.isInBounds(column, row)) {
            throw new InvalidArgumentException(String.format(ERROR_NOT_IN_BOUNDS));
        }
        return new Position(column, row);
    }

    /**
     * parses the one based index.
     * @return the zero based index
     * @throws InvalidArgumentException if there is no argument to parse
     *      * or if hte argument could not get parsed.
     */
    public int parseIdx() throws InvalidArgumentException {
        String argument = retrieveArgument().trim();
        try {
            return Integer.parseInt(argument) - INDEX_OFFSET;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(ERROR_NOT_NUMBER_FORMAT.formatted(argument));
        }
    }

    /**
     * parses the next argument as a one based index if available.
     * @return an {@code Optional} containing the parsed zero based index or {@code Optional.empty()} if no argument is available
     * @throws InvalidArgumentException if an argument cannot be parsed as an index.
     */
    public Optional<Integer> parseOptionalIdx() throws InvalidArgumentException {
        if (isExhausted()) {
            return Optional.empty();
        }
        return Optional.of(parseIdx());
    }

    /**
     * parses the arguments as one based indexes.
     * @return a {@code List} containing the parsed zero based indexes
     * @throws InvalidArgumentException if an argument cannot be parsed as an index.
     */
    public List<Integer> parseAllIndexes() throws InvalidArgumentException {
        List<Integer> indexes = new ArrayList<>();
        while (!isExhausted()) {
            indexes.add(parseIdx());
        }
        return indexes;
    }
}
