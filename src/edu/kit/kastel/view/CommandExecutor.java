package edu.kit.kastel.view;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.parsing.Arguments;
import edu.kit.kastel.view.parsing.InvalidArgumentException;
import edu.kit.kastel.view.parsing.GameKeyword;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * this class manages and executes the commands.
 * @author ucktt
 */
public class CommandExecutor {

    private static final String ERROR_PREFIX = "ERROR: ";
    private static final String ERROR_UNKNOWN_COMMAND = ERROR_PREFIX + "unknown command";
    private static final String ERROR_TOO_MANY_ARGUMENTS = ERROR_PREFIX + "too many arguments";
    private static final String QUIT = "quit";
    private static final String WHITESPACE_PATTERN = "\\s+";
    private final GameKeyword[] gameKeywords;
    private final Scanner scanner;
    private final PrintStream output;
    private final PrintStream error;
    private Game game;
    private boolean isRunning;

    /**
     * constructs a command executor instance with the specified parameters.
     * @param reader the scanner used to read command input
     * @param output used for standard output
     * @param error used for error output
     */
    public CommandExecutor(Scanner reader, PrintStream output,
                           PrintStream error) {
        this.scanner = reader;
        this.output = output;
        this.error = error;
        this.gameKeywords = GameKeyword.values();
        this.isRunning = true;
    }

    /**
     * sets the game model.
     * @param game the game model instance
     */
    public void setModel(Game game) {
        this.game = game;
    }

    /**
     * checks whether the game is running.
     * @return true if game is running; false otherwise
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    /**
     * sets the running state to false and stops the executor.
     */
    private void stop() {
        this.isRunning = false;
    }

    /**
     * processes the next line of user input if execution is active.
     */
    public void handle() {
        if (this.isRunning && this.scanner.hasNextLine()) {
            String line = this.scanner.nextLine();
            String trimmed = line.trim();
            if (trimmed.equalsIgnoreCase(QUIT)) {
                stop();
                return;
            }
            if (trimmed.isEmpty()) {
                return;
            }
            String[] splitLine = trimmed.split(WHITESPACE_PATTERN);
            handleCommand(splitLine);
        }
    }

    private void handleCommand(String[] splitLine) {

        for (GameKeyword keyword : gameKeywords) {
            if (keyword.matches(splitLine)) {
                String[] arguments = new String[splitLine.length - keyword.words()];
                for (int i = 0; i < arguments.length; i++) {
                    arguments[i] = splitLine[i + keyword.words()];
                }
                Arguments args = new Arguments(arguments);
                Command<Game> command;
                try {
                    command = keyword.create(args);
                } catch (InvalidArgumentException e) {
                    this.error.println(ERROR_PREFIX + e.getMessage());
                    return;
                }
                if (!args.isExhausted()) {
                    this.error.println(ERROR_TOO_MANY_ARGUMENTS);
                    return;
                }
                Result gameResult = command.execute(this.game);
                if (gameResult == null || gameResult.getMessage() == null) {
                    return;
                }
                if (gameResult.getType() == ResultType.SUCCESS) {
                    this.output.println(gameResult.getMessage());
                } else {
                    error.println(ERROR_PREFIX + gameResult.getMessage());
                }
                return;
            }
        }
        this.error.println(ERROR_UNKNOWN_COMMAND);
    }
}
