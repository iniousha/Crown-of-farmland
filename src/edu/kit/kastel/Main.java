package edu.kit.kastel;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.view.CommandExecutor;
import edu.kit.kastel.view.fileio.ProgramStart;
import edu.kit.kastel.view.fileio.ProgramStartException;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * this class offers the entry point for the Main.
 *
 * @author ucktt
 */
public final class Main {

    private Main() {
    }

    /**
     * the entry point for the Main. no command line arguments expected
     *
     * @param args the commandline arguments
     */
    public static void main(String[] args) {
        try {
            Game game = ProgramStart.initialize(args);
            Scanner scanner = new Scanner(System.in);
            PrintStream out = System.out;
            PrintStream err = System.err;

            CommandExecutor executor = new CommandExecutor(scanner, out, err);
            executor.setModel(game);

            while (executor.isRunning() && !game.isGameOver() && scanner.hasNextLine()) {
                executor.handleUserInput();
            }
        } catch (ProgramStartException e) {
            System.err.println(e.getMessage());
        }
    }
}