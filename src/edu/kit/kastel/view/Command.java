package edu.kit.kastel.view;

/**
 * this interface represents a generic command.
 * @param <T> the type of the execution context
 * @author ucktt
 */
public interface Command<T> {
    /**
     * executes this command with the specified target.
     * @param handle the execution context
     * @return the result of the executed command
     */
    Result execute(T handle);
}
