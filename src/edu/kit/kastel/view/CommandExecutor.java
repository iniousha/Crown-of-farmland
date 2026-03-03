package edu.kit.kastel.view;

import edu.kit.kastel.model.Game;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.Set;

/**
 * this class manages and executes the commands.
 * @author ucktt
 */
public class CommandExecutor {

    private static final String COMMAND_SEPARATOR = " ";
    private static final String ERROR_PREFIX = "Error: ";
    private static final String ERROR_UNKNOWN_COMMAND = ERROR_PREFIX + "unknown command";
    private static final String ERROR_TOO_MANY_ARGUMENTS = ERROR_PREFIX + "too many arguments provided.";
    private final Set<ModelKeyword> modelKeywords;
    private final Scanner scanner;
    private final PrintStream defaultStream;
    private final PrintStream errorStream;
    private Game service;
    private boolean isRunning;

    /**
     * constructs a command executor instance with the specified parameters.
     * @param inputSource the scanner used to read command input
     * @param defaultOutputStream the stream used for standard output
     * @param errorStream the stream used for error output
     */
    public CommandExecutor(Scanner inputSource, PrintStream defaultOutputStream,
                           PrintStream errorStream) {
        this.scanner = inputSource;
        this.defaultStream = defaultOutputStream;
        this.errorStream = errorStream;
        this.modelKeywords = EnumSet.allOf(ModelKeyword.class);
        this.isRunning = true;
    }

    /**
     * sets the game model.
     * @param game the game model instance
     */
    public void setModel(Game game) {
        this.service = game;
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
    public void stop() {
        this.isRunning = false;
    }

    /**
     * processes the next line of user input if execution is active.
     */
    public void handleUserInput() {
        if (this.isRunning && this.scanner.hasNextLine()) {
            handleLine(this.scanner.nextLine());
        }
    }

    private void handleLine(String line) {
        String trimmed = line.trim();
        if (trimmed.equalsIgnoreCase("quit")) {
            stop();
            return;
        }
        if (trimmed.isEmpty()) {
            return;
        }
        String[] splitLine = trimmed.split("\\s+");
        if (!findAndHandleCommand(this.service, this.modelKeywords, splitLine)) {
            this.errorStream.println(ERROR_UNKNOWN_COMMAND);
        }
    }

    private boolean findAndHandleCommand(Game service, Set<ModelKeyword> keywords, String[] command) {
        ModelKeyword keyword = retrieveKeyword(keywords, command);
        if (keyword != null) {
            String[] arguments = Arrays.copyOfRange(command, keyword.words(), command.length);
            handleCommand(service, arguments, keyword);
            return true;
        }
        return false;
    }

    private void handleCommand(Game service, String[] arguments, ModelKeyword keyword) {

        Arguments argumentsHolder = new Arguments(arguments);
        Command<Game> providedCommand;
        try {
            providedCommand = keyword.provide(argumentsHolder);
        } catch (InvalidArgumentException e) {
            this.errorStream.println(ERROR_PREFIX + e.getMessage());
            return;
        }

        if (!argumentsHolder.isExhausted()) {
            this.errorStream.println(ERROR_TOO_MANY_ARGUMENTS);
            return;
        }

        handleResult(providedCommand.execute(service));
    }

    private void handleResult(Result result) {
        if (result == null || result.getMessage() == null) {
            return;
        }
        PrintStream outputStream = switch (result.getType()) {
            case SUCCESS -> this.defaultStream;
            case FAILURE -> this.errorStream;
        };
        outputStream.println((result.getType().equals(ResultType.FAILURE) ? ERROR_PREFIX : "") + result.getMessage());
    }

    private static <T extends Keyword<?>> T retrieveKeyword(Collection<T> keywords, String[] command) {
        for (T keyword : keywords) {
            if (keyword.matches(command)) {
                return keyword;
            }
        }
        return null;
    }
}
