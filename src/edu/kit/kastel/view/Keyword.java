package edu.kit.kastel.view;

/**
 * this interface represents a keyword that can recognize and provide commands.
 * @param <T> the type of the execution context
 * @author ucktt
 */
public interface Keyword<T> extends CommandProvider<T> {
    /**
     * checks whether this keyword matches any of the command strings.
     * @param command the command to match
     * @return  true if the command matches this keyword; false otherwise
     */
    boolean matches(String[] command);

    /**
     * returns the number of words that make up this keyword.
     * @return the keyword length
     */
    int words();
}
