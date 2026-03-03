package edu.kit.kastel.view.fileio;

/**
 * exception thrown when an error occurs during program startup.
 * @author ucktt
 */
public class ProgramStartException extends Exception {
    /**
     * constructs a ProgramStartException with the specified message.
     * @param message message describing the error
     */
    public ProgramStartException(String message) {
        super(message);
    }
}
