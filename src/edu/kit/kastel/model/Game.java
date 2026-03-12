package edu.kit.kastel.model;

import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.merge.Merge;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.SymbolSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * this class represents the core game logic.
 *
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
    private boolean blockedThisTurn;

    /**
     * constructs the game with the specified parameters.
     *
     * @param team1Name name of the first team
     * @param team2Name name of the second team
     * @param deck1     deck of the first team
     * @param deck2     deck of the second team
     * @param random    random number used for randomized decisions
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

        this.team1 = new Team(team1Name, deck1, false);
        this.team2 = new Team(team2Name, deck2, true);

        Position team1King = new Position(3, 0);
        Position team2King = new Position(3, 6);

        this.farmlandBoard = new FarmlandBoard(symbolSet);

        farmlandBoard.placeUnit(team1.getFarmerKing(), team1King);
        farmlandBoard.placeUnit(team2.getFarmerKing(), team2King);

        this.currentTeam = team1;
        team1.drawCard();
        team2.drawCard();
    }

    /**
     * checks whether yield has failed.
     *
     * @return true if yield has failed; false otherwise
     */
    public boolean hasYieldFailed() {
        return yieldHasFailed;
    }

    /**
     * sets whether yield has failed.
     *
     * @param yieldFailed true if yield has failed; false otherwise
     */
    public void setYieldHasFailed(boolean yieldFailed) {
        yieldHasFailed = yieldFailed;
    }

    /**
     * returns the current team whose turn it is.
     *
     * @return the current team
     */
    public Team getCurrentTeam() {
        return this.currentTeam;
    }

    /**
     * checks whether the unit started blocking this turn.
     * @return true if blocking happened this turn; false otherwise
     */
    public boolean isBlockedThisTurn() {
        return blockedThisTurn;
    }

    /**
     * sets whether the unit has been blocked during this turn.
     * @param blockedThisTurn true if unit was blocked this turn; false otherwise
     */
    public void setBlockedThisTurn(boolean blockedThisTurn) {
        this.blockedThisTurn = blockedThisTurn;
    }
    /**
     * returns the opposing team of the current team.
     *
     * @return the opponent team
     */
    public Team getOpponentTeam() {
        return currentTeam == team1 ? team2 : team1;
    }

    /**
     * returns the game board.
     *
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
            this.currentTeam.setHasSetPlace(false);
        }
        setYieldHasFailed(false);
        setBlockedThisTurn(false);
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

    /**
     * sets the winner to the specified team.
     * @param winner the specified team to be the winner
     */
    public void setWinner(Team winner) {
        this.winner = winner;
    }

    /**
     * checks whether the game is over.
     *
     * @return true if the winner object is not null; false otherwise
     */
    public boolean isGameOver() {
        return winner != null;
    }

    /**
     * returns the winning team of the game.
     *
     * @return the winner team, or null if the game is not over
     */
    public Team getWinner() {
        return this.winner;
    }

    /**
     * returns a list containing the regular units in the current team's hand.
     *
     * @return the current team's hand
     */
    public List<RegularUnit> hand() {
        Team currentTeam = getCurrentTeam();
        return currentTeam.getHand();
    }

    /**
     * returns the currently saved position on the board.
     *
     * @return the saved position
     */
    public Position getSavedPosition() {
        return this.savedPosition;
    }

    /**
     * sets the saved position on the board and marks it as just selected.
     *
     * @param newPosition the new position to be set
     */
    public void setSavedPosition(Position newPosition) {
        this.savedPosition = newPosition;
    }

    /**
     * returns the unit at the specified position.
     * @param position specified position to find the unit at
     * @return the unit
     */
    public Unit getUnitAt(Position position) {
        Field field = getFarmlandBoard().getField(position);
        setSavedPosition(position);
        return field.getUnit();
    }

    /**
     * returns the verbosity of the current board.
     *
     * @return the current verbosity setting
     */
    public Verbosity getVerbosity() {
        return verbosity;
    }

    /**
     * returns the random number generator used by this game.
     *
     * @return the random number generator
     */
    public Random getRandom() {
        return this.random;
    }


    /**
     * moves the unit from the current position to the target position.
     *
     * @param unitToPlace     unit that is to change fields
     * @param currentPosition the current position of the unit
     * @param targetPosition  the target position of the unit
     * @return formatted message of move execution
     */
    public String moveUnit(Unit unitToPlace, Position currentPosition, Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        Field targetedField = getFarmlandBoard().getField(targetPosition);
        farmlandBoard.removeUnit(currentPosition);
        farmlandBoard.placeUnit(unitToPlace, targetPosition);
        this.savedPosition = targetPosition;
        unitToPlace.setHasMoved(true);
        stringBuilder.append(MessageFormatter.moveDisplay(unitToPlace, targetedField));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }

    /**
     * ends bocking for the selected unit.
     * @param selectedUnit the specified unit to be unblocked
     * @return formatted message indicating that blocking has ended for the given unit
     */
    public String executeEndBlocking(Unit selectedUnit) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!selectedUnit.isFarmerKing() && selectedUnit.isBlocking()) {
            stringBuilder.append(MessageFormatter.noLongerBlockDisplay(selectedUnit));
            stringBuilder.append(System.lineSeparator());
            selectedUnit.endBlocking();
        }
        return stringBuilder.toString();
    }

    /**
     * places one or more units from hand onto the specified position on the board.
     * @param indexes the zero-based indexes of the units in hand to place
     * @param position the specified position on which the unit is placed
     * @return formatted message describing the placement outcome
     */
    public String placeUnitsFromHand(List<Integer> indexes, Position position) {
        StringBuilder stringBuilder = new StringBuilder();

        FarmlandBoard board = getFarmlandBoard();
        Field field = board.getField(position);
        List<RegularUnit> hand = getCurrentTeam().getHand();
        Map<Integer, RegularUnit> indexedUnits = new HashMap<>();
        Team currentTeam = getCurrentTeam();
        for (int index : indexes) {
            indexedUnits.put(index, hand.get(index));
        }
        List<Integer> sortedIndexes = new ArrayList<>(indexes);
        sortedIndexes.sort(Collections.reverseOrder());
        for (int index : sortedIndexes) {
            hand.remove(index);
        }
        for (int index : indexes) {
            RegularUnit unitToPlace = indexedUnits.get(index);
            unitToPlace.setTeam(getCurrentTeam());

            Unit unitInField = field.getUnit();

            if (unitInField == null) {
                board.placeUnit(unitToPlace, position);
                stringBuilder.append(MessageFormatter.placeDisplay(currentTeam, unitToPlace, field));
                stringBuilder.append(System.lineSeparator());
            } else if (unitInField.getTeam() == currentTeam) {
                stringBuilder.append(MessageFormatter.placeDisplay(currentTeam, unitToPlace, field));
                stringBuilder.append(System.lineSeparator());
                Merge merge = new Merge(unitToPlace, unitInField);
                stringBuilder.append(merge.mergeResult(unitInField, unitToPlace, position, this));
            }
        }
        currentTeam.setHasSetPlace(true);

        if (board.unitCount(currentTeam) > 5) {
            Unit placedUnit = field.getUnit();
            board.removeUnit(position);
            stringBuilder.append(MessageFormatter.unitEliminationDisplay(placedUnit));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    /**
     * executes the in place action.
     * @param selectedUnit the unit performing this action
     * @param targetPosition the position on which this action takes place
     * @return the formatted message if the unit stays in place
     */
    public String executeEnPlace(Unit selectedUnit, Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        Position selectedPosition = this.getSavedPosition();
        Field targetedField = this.getFarmlandBoard().getField(targetPosition);
        if (selectedPosition.equals(targetPosition)) {
            selectedUnit.setHasMoved(true);
            stringBuilder.append(MessageFormatter.moveDisplay(selectedUnit, targetedField));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
