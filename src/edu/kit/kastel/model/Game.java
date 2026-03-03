package edu.kit.kastel.model;

import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.MergeResult;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.SymbolSet;

import java.util.List;
import java.util.Random;

/**
 * this class represents the core game logic.
 * @author ucktt
 */
public class Game {

    private final Team team1;
    private final Team team2;
    private final Random random;
    private final Verbosity verbosity;
    private Team currentTeam;
    private final FarmlandBoard farmlandBoard;
    private Team winner;
    private Position savedPosition;
    private boolean yieldHasFailed;
    private boolean justSelected;

    /**
     * constructs the game with the specified parameters.
     * @param team1Name name of the first team
     * @param team2Name name of the second team
     * @param deck1 deck of the first team
     * @param deck2 deck of the second team
     * @param random random number used for randomized decisions
     * @param verbosity determining whether the board display should be compact or fully displayed
     * @param symbolSet the symbol set used to build the board with
     */
    public Game(String team1Name,
                String team2Name,
                Deck deck1,
                Deck deck2,
                Random random,
                Verbosity verbosity,
                SymbolSet symbolSet) {
        this.random = random;
        this.verbosity = verbosity;
        this.winner = null;

        deck1.shuffle(random);
        deck2.shuffle(random);

        this.team1 = new Team(team1Name, deck1);
        this.team2 = new Team(team2Name, deck2);

        Position team1King = new Position(3, 0);
        Position team2King = new Position(3, 6);

        this.farmlandBoard = new FarmlandBoard(symbolSet);

        farmlandBoard.placeUnit(team1.getFarmerKing(), team1King);
        farmlandBoard.placeUnit(team2.getFarmerKing(), team2King);

        this.currentTeam = team1;
        currentTeam.drawCard();
    }

    /**
     * checks whether yield has failed.
     * @return true if yield has failed; false otherwise
     */
    public boolean hasYieldFailed() {
        return yieldHasFailed;
    }

    /**
     * sets whether yield has failed.
     * @param yieldFailed true if yield has failed; false otherwise
     */
    public void setYieldFailed(boolean yieldFailed) {
        yieldHasFailed = yieldFailed;
    }

    /**
     * returns the current team whose turn it is.
     * @return the current team
     */
    public Team getCurrentTeam() {
        return this.currentTeam;
    }

    /**
     * returns the opposing team of the current team.
     * @return the opponent team
     */
    public Team getOpponentTeam() {
        return currentTeam == team1 ? team2 : team1;
    }

    /**
     * returns the game board.
     * @return the farmland game board
     */
    public FarmlandBoard getFarmlandBoard() {
        return this.farmlandBoard;
    }

    /**
     * ends the current turn and prepares the next turn.
     */
    public void nextTurn() {
        List<Unit> allUnits = this.farmlandBoard.getUnitsForTeam(this.currentTeam);
        for (Unit unit : allUnits) {
            unit.setHasMoved(false);
            this.currentTeam.setHasPlaced(false);
        }
        setYieldFailed(false);
        toggleTurn();
    }

    private void toggleTurn() {
        currentTeam = (currentTeam == team1) ? team2 : team1;
        Team opponentTeam = getOpponentTeam();
        if (currentTeam.isDeckEmpty()) {
            setWinner(opponentTeam);
        } else {
            currentTeam.drawCard();
        }
    }

    private void setWinner(Team winner) {
        this.winner = winner;
    }
    /**
     * checks whether the game is over.
     * @return true if the winner object is not null; false otherwise
     */
    public boolean isGameOver() {
        return winner != null;
    }

    /**
     * returns the winning team of the game.
     * @return the winner team, or null if the game is not over
     */
    public Team getWinner() {
        return this.winner;
    }

    /**
     * returns a list containing the regular units in the current team's hand.
     * @return the current team's hand
     */
    public List<RegularUnit> hand() {
        Team currentTeam = getCurrentTeam();
        return currentTeam.getHand();
    }

    /**
     * returns the currently saved position on the board.
     * @return the saved position
     */
    public Position getSavedPosition() {
        return this.savedPosition;
    }

    /**
     * sets the saved position on the board and marks it as just selected.
     * @param newPosition the new position to be set
     */
    public void setSavedPosition(Position newPosition) {
        this.savedPosition = newPosition;
        this.justSelected = true;
    }

    /**
     * checks whether the position is just selected.
     * @return true if the position is newly selected; false otherwise
     */
    public boolean isJustSelected() {
        return justSelected;
    }

    /**
     * changes the just selected state to false.
     */
    public void clearJustSelected() {
        this.justSelected = false;
    }

    /**
     * returns the unit at the specified position.
     * @param position the specified position
     * @return the unit in the given position
     */
    public Unit getUnitAt(Position position) {
        Field field = getFarmlandBoard().getField(position);
        setSavedPosition(position);
        return field.getUnit();
    }

    /**
     * returns the verbosity of the current board.
     * @return the current verbosity setting
     */
    public Verbosity getVerbosity() {
        return verbosity;
    }

    /**
     * returns the random number generator used by this game.
     * @return the random number generator
     */
    public Random getRandom() {
        return this.random;
    }

    /**
     * returns the merge result between two units of the same team.
     * @param unitInField unit positioned on the selected field
     * @param unitToPlace unit to place on the selected field
     * @param position position of the selected field
     * @return the result of the merge between the two units.
     */
    public MergeResult mergeAction(Unit unitInField, RegularUnit unitToPlace, Position position) {
        RegularUnit mergedUnit = ((RegularUnit) unitInField).mergeWith(unitToPlace);
        FarmlandBoard board = getFarmlandBoard();
        Field field = getFarmlandBoard().getField(position);

        if (mergedUnit != null) {
            board.placeUnit(mergedUnit, position);
            return new MergeResult(true, unitInField, unitToPlace, field);
        } else {
            board.removeUnit(position);
            board.placeUnit(unitToPlace, position);
            return new MergeResult(false, unitInField, unitToPlace, field);
        }
    }

    /**
     * moves the unit from the current position to the target position.
     * @param unitToPlace unit that is to change fields
     * @param currentPosition the current position of the unit
     * @param targetPosition the target position of the unit
     */
    public void moveUnit(Unit unitToPlace, Position currentPosition, Position targetPosition) {
        farmlandBoard.removeUnit(currentPosition);
        farmlandBoard.placeUnit(unitToPlace, targetPosition);
        this.savedPosition = targetPosition;
    }
}
