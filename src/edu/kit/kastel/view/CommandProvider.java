package edu.kit.kastel.view;

import edu.kit.kastel.view.parsing.Arguments;
import edu.kit.kastel.view.parsing.InvalidArgumentException;

/**
 * this interface provides commands based on parsed input arguments.
 * @param <T> the type of execution context
 * @author ucktt
 */
public interface CommandProvider<T> {
    /**
     * creates  command from the given argument.
     * @param arguments the parsed command arguments
     * @return the built command
     * @throws InvalidArgumentException if the arguments are invalid
     */
    Command<T> provide(Arguments arguments) throws InvalidArgumentException;
}
