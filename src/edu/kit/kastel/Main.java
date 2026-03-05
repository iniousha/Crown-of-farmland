package edu.kit.kastel;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.CommandExecutor;
import edu.kit.kastel.view.fileio.ProgramStart;
import edu.kit.kastel.view.fileio.ProgramStartException;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * hfeed.
 *
 * @author ucktt
 */
public final class Main {

    private Main() {
    }

    /**
     * efhwe.
     *
     * @param args dfh
     */
    public static void main(String[] args) {
        try {
            Game game = ProgramStart.initialize(args);
            Scanner scanner = new Scanner(System.in);
            PrintStream out = System.out;
            PrintStream err = System.err;

            CommandExecutor executor = new CommandExecutor(scanner, out, err);
            executor.setModel(game);

            while (executor.isRunning() && scanner.hasNextLine()) {
                executor.handleUserInput();
            }
        } catch (ProgramStartException e) {
            System.err.println(e.getMessage());
        }
    }
}