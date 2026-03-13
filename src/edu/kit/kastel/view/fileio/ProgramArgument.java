package edu.kit.kastel.view.fileio;

/**
 * this enum represents command-line arguments and their parsing priorities.
 * @author ucktt
 */
public enum ProgramArgument {
    /**
     * seed value used for randomization.
     */
    SEED(1),
    /**
     * board configuration file.
     */
    BOARD(2),
    /**
     * unit definition file.
     */
    UNITS(3),
    /**
     * deck file used for both teams.
     */
    DECK(4),
    /**
     * deck file for team 1.
     */
    DECK1(4),
    /**
     * deck file for team 2.
     */
    DECK2(4),
    /**
     * name of team 1.
     */
    TEAM1(5),
    /**
     * name of team 2.
     */
    TEAM2(6),
    /**
     * verbosity type for output display.
     */
    VERBOSITY(7);

    private final int priority;

    ProgramArgument(int priority) {
        this.priority = priority;
    }

    /**
     * returns the priority of this program argument.
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * returns the program argument related to the given identifier.
     * @param identifier the string representation of the argument
     * @return the matching program argument, or null if no matches exist
     */
    public static ProgramArgument fromString(String identifier) {
        for (ProgramArgument programArgument : values()) {
            if (programArgument.toString().equals(identifier)) {
                return programArgument;
            }
        }
        return null;
    }
}
