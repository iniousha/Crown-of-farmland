package edu.kit.kastel.model;

import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

import java.util.List;

/**
 * this utility class helps with printing the output using the provided static methods.
 *
 * @author ucktt
 */
public final class MessageFormatter {

    private static final String HAND_CARD_FORMAT = "[%d] %s (%d/%d)";
    private static final String HIDDEN_UNIT_FORMAT = "??? (Team Enemy)%nATK: ???%nDEF: ???";
    private static final String UNIT_DISPLAY_FORMAT = "%s (Team %s)%nATK: %d%nDEF: %d";
    private static final String FARMER_KING_DISPLAY_FORMAT = "%s's Farmer King";
    private static final int LINE_WIDTH = 31;
    private static final String NO_UNIT_DISPLAY_FORMAT = "<no unit>";
    private static final String FLIP_DISPLAY_FORMAT = "%s (%d/%d) was flipped on %s!";
    private static final String BLOCK_DISPLAY_FORMAT = "%s (%s) blocks!";
    private static final String WINNER_DISPLAY_FORMAT = "%s wins!";
    private static final String DISCARD_DISPLAY_FORMAT = "%s discarded %s (%d/%d).";
    private static final String TURN_DISPLAY_FORMAT = "It is %s's turn!";
    private static final String PLACE_DISPLAY_FORMAT = "%s places %s on %s.";
    private static final String SUCCESSFUL_MERGE_FORMAT = "%s and %s on %s join forces!%nSuccess!";
    private static final String FAILED_MERGE_DISPLAY = "%s and %s on %s join forces!%nUnion failed. %s was eliminated.";
    private static final String SIXTH_UNIT_DISPLAY_FORMAT = "%s was eliminated!";
    private static final String NO_LONGER_BLOCKS_FORMAT = "%s no longer blocks.";
    private static final String MOVE_DISPLAY_FORMAT = "%s moves to %s.";
    private static final String DUEL_FARMER_KING_FORMAT = "%s (%d/%d) attacks %s on %s!";
    private static final String DUEL_REGULAR_UNIT_FORMAT = "%s (%d/%d) attacks %s (%d/%d) on %s!";

    private MessageFormatter() {
    }

    /**
     * returns the formatted string representation of the given hand.
     *
     * @param hand the given hand to format
     * @return a string displaying the hand contents
     */
    public static String handToString(List<RegularUnit> hand) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            String unitName = hand.get(i).getName();
            int attackPoint = hand.get(i).getAttackPoints();
            int defencePoint = hand.get(i).getDefencePoints();
            stringBuilder.append(String.format(HAND_CARD_FORMAT, i + 1, unitName, attackPoint, defencePoint));
            if (i < hand.size() - 1) {
                stringBuilder.append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    /**
     * returns a formatted string representation of the given unit.
     *
     * @param unit the given unit to format
     * @param game the current game context used for formatting
     * @return a string displaying the unit
     */
    public static String displayUnit(Unit unit, Game game) {
        if (unit instanceof FarmerKing) {
            return displayFarmerKing((FarmerKing) unit);
        } else {
            return displayRegularUnit((RegularUnit) unit, game);
        }
    }

    private static String displayRegularUnit(RegularUnit currentUnit, Game game) {
        Team unitTeam = currentUnit.getTeam();
        Team currentTeam = game.getCurrentTeam();
        String unitName = currentUnit.getName();
        String teamName = unitTeam.getName();
        int attackPoints = currentUnit.getAttackPoints();
        int defencePoints = currentUnit.getDefencePoints();

        if (!currentUnit.isFaceUp() && unitTeam != currentTeam) {
            return String.format(HIDDEN_UNIT_FORMAT);
        } else {
            return String.format(UNIT_DISPLAY_FORMAT, unitName, teamName, attackPoints, defencePoints);
        }
    }

    private static String displayFarmerKing(FarmerKing farmerKing) {
        String teamName = farmerKing.getTeam().getName();
        return String.format(FARMER_KING_DISPLAY_FORMAT, teamName);
    }

    /**
     * returns a formatted string representation of no unit.
     *
     * @return the formatted representation for an empty field
     */
    public static String noUnitDisplay() {
        return NO_UNIT_DISPLAY_FORMAT;
    }

    private static int padding(String left, String right) {
        return LINE_WIDTH - left.length() - right.length() - 2;
    }

    /**
     * returns the string format representing the state of the game.
     *
     * @param game the game to be stated
     * @return string representation of the state of the game
     */
    //do multiple private methods and build string formatted sentences later
    public static String stateDisplay(Game game) {
        StringBuilder stringBuilder = new StringBuilder();

        Team currentTeam = game.getCurrentTeam();
        Team opponentTeam = game.getOpponentTeam();

        //team names
        String currentTeamName = currentTeam.getName();
        String opponentTeamName = opponentTeam.getName();
        int teamSpace = padding(currentTeamName, opponentTeamName);
        stringBuilder.append("  ").append(currentTeamName).append(" ".repeat(teamSpace)).append(opponentTeamName)
                .append(System.lineSeparator());

        //life points
        String currentTeamLifePoints = currentTeam.getLifePoints() + "/8000 LP";
        String opponentTeamLifePoints = opponentTeam.getLifePoints() + "/8000 LP";
        int lifePointsSpace = padding(currentTeamLifePoints, opponentTeamLifePoints);
        stringBuilder.append("  ").append(currentTeamLifePoints).append(" ".repeat(lifePointsSpace)).append(opponentTeamLifePoints)
                .append(System.lineSeparator());

        //deck count
        String currentTeamDeck = "DC: " + currentTeam.getDeck().getDeckSize() + "/40";
        String opponentTeamDeck = "DC: " + opponentTeam.getDeck().getDeckSize() + "/40";
        int deckSpace = padding(currentTeamDeck, opponentTeamDeck);
        stringBuilder.append("  ").append(currentTeamDeck).append(" ".repeat(deckSpace)).append(opponentTeamDeck)
                .append(System.lineSeparator());

        //board count
        String currentTeamUnit = "BC: " + game.getFarmlandBoard().unitCount(currentTeam) + "/5";
        String opponentTeamUnit = "BC: " + game.getFarmlandBoard().unitCount(opponentTeam) + "/5";
        int unitSpace = padding(currentTeamUnit, opponentTeamUnit);
        stringBuilder.append("  ").append(currentTeamUnit).append(" ".repeat(unitSpace)).append(opponentTeamUnit);

        return stringBuilder.toString();
    }

    /**
     * returns a formatted string representing a unit being flipped face up on a field.
     *
     * @param unit  the unit that was flipped
     * @param field the specified field on which the unit was flipped
     * @return the formatted string representing the flip event
     */
    public static String flipDisplay(Unit unit, Field field) {
        int attackPoints = ((RegularUnit) unit).getAttackPoints();
        int defencePoints = ((RegularUnit) unit).getDefencePoints();

        return String.format(FLIP_DISPLAY_FORMAT, unit.getName(), attackPoints, defencePoints, field.getPosition().toString());
    }

    /**
     * returns a formatted string representing a unit being blocked on a field.
     *
     * @param unit  the unit that wa blocked
     * @param field the specified field on which the unit was blocked
     * @return the formatted string representing the block event
     */
    public static String blockDisplay(Unit unit, Field field) {
        return String.format(BLOCK_DISPLAY_FORMAT, unit.getName(), field.getPosition().toString());
    }

    /**
     * returns a formatted string representing the winner team.
     *
     * @param team the team that won the game.
     * @return the formatted string representing hte winner team
     */
    public static String winnerDisplay(Team team) {
        String teamName = team.getName();
        return String.format(WINNER_DISPLAY_FORMAT, teamName);
    }

    /**
     * returns a formatted string indicating that the specifies team's deck is empty.
     *
     * @param team the team whose deck is empty
     * @return a formatted message
     */
    public static String deckEmptyDisplay(Team team) {
        return team.getName() + " has no cards left in the deck!";
    }

    /**
     * returns a formatted string indicating that a team discarded a unit.
     *
     * @param team the team that discarded their unit
     * @param unit the discarded unit
     * @return the formatted message
     */
    public static String discardedCardDisplay(Team team, Unit unit) {
        String teamName = team.getName();
        String unitName = unit.getName();
        int attackPoints = ((RegularUnit) unit).getAttackPoints();
        int defencePoints = ((RegularUnit) unit).getDefencePoints();
        return String.format(DISCARD_DISPLAY_FORMAT, teamName, unitName, attackPoints, defencePoints);
    }

    /**
     * returns a formatted string indicating whose turn it is.
     *
     * @param team the team whose turn is active
     * @return the formatted message
     */
    public static String turnDisplay(Team team) {
        return String.format(TURN_DISPLAY_FORMAT, team.getName());
    }

    /**
     * returns a formatted string indicating that a team placed a unit without merging.
     *
     * @param team  the team that placed the unit
     * @param unit  the unit that was placed
     * @param field the field where the unit was placed
     * @return the formatted message
     */
    public static String placeDisplay(Team team, Unit unit, Field field) {
        String teamName = team.getName();
        String unitName = unit.getName();
        String fieldName = field.getPosition().toString();
        return String.format(PLACE_DISPLAY_FORMAT, teamName, unitName, fieldName);
    }

    /**
     * returns a formatted string indicating that a successful merge between units
     * of the same team on the specified field happened.
     *
     * @param unit1 the first unit involved in the merge
     * @param unit2 the second unit involved in the merge
     * @param field the field on which the merge happened
     * @return the formatted message
     */
    public static String successfulMergeDisplay(Unit unit1, Unit unit2, Field field) {
        String unit1Name = unit1.getName();
        String unit2Name = unit2.getName();
        String fieldName = field.getPosition().toString();
        return String.format(SUCCESSFUL_MERGE_FORMAT, unit2Name, unit1Name, fieldName);
    }

    /**
     * returns a formatted string indicating that a failed merge between units
     * of the same team on the specified field happened.
     *
     * @param unit1 the first unit involved in the merge
     * @param unit2 the second unit involved in the merge
     * @param field the field on which the merge happened
     * @return the formatted message
     */
    public static String failedMergeDisplay(Unit unit1, Unit unit2, Field field) {
        String unit1Name = unit1.getName();
        String unit2Name = unit2.getName();
        String fieldName = field.getPosition().toString();
        return String.format(FAILED_MERGE_DISPLAY, unit2Name, unit1Name, fieldName, unit2Name);
    }

    /**
     * returns a formatted string indicating that a team tried to place their sixth unit on a field.
     *
     * @param unit the unit that was being placed
     * @return the formatted message
     */
    public static String sixthUnitDisplay(Unit unit) {
        String unitName = unit.getName();
        return String.format(SIXTH_UNIT_DISPLAY_FORMAT, unitName);
    }

    /**
     * returns a formatted string indicating that the specified unit no longer blocks.
     *
     * @param unit the unit that no longer blocks
     * @return the formatted message
     */
    public static String noLongerBlockDisplay(Unit unit) {
        return String.format(NO_LONGER_BLOCKS_FORMAT, unit.getName());
    }

    /**
     * returns a formatted string indicating that the specified unit has moved to the specified field.
     *
     * @param unit  the unit that moved
     * @param field the target field
     * @return the formatted message
     */
    public static String moveDisplay(Unit unit, Field field) {
        return String.format(MOVE_DISPLAY_FORMAT, unit.getName(), field.getPosition().toString());
    }

    /**
     * returns a formatted string indicating that a duel with the farmer king was initiated.
     *
     * @param unit1 the attacking unit
     * @param unit2 the farmer king unit
     * @param field the field on which the duel happened
     * @return the formatted message
     */
    public static String duelWithFarmerKingDisplay(Unit unit1, Unit unit2, Field field) {
        String unit1Name = unit1.getName();
        int attackPoints1 = ((RegularUnit) unit1).getAttackPoints();
        int defencePoints1 = ((RegularUnit) unit1).getDefencePoints();
        String unit2Name = unit2.getName();
        String fieldName = field.getPosition().toString();

        return String.format(DUEL_FARMER_KING_FORMAT, unit1Name, attackPoints1, defencePoints1, unit2Name, fieldName);
    }

    /**
     * returns a formatted string representing a duel between two opponent regular units.
     *
     * @param unit1 the attacking unit
     * @param unit2 the defencing unit
     * @param field the field on which the duel happened
     * @param defenderWasFaceDown whether the defender was faced down
     * @return the formatted message
     */
    public static String duelWithRegularUnitDisplay(Unit unit1, Unit unit2, Field field, boolean defenderWasFaceDown) {
        String unit1Name = unit1.getName();
        int attackPoints1 = ((RegularUnit) unit1).getAttackPoints();
        int defencePoints1 = ((RegularUnit) unit1).getDefencePoints();

        String unit2Name = unit2.getName();
        int attackPoints = ((RegularUnit) unit2).getAttackPoints();
        int defencePoint2 = ((RegularUnit) unit2).getDefencePoints();
        String fieldName = field.getPosition().toString();
        if (defenderWasFaceDown && unit1.getTeam() != unit2.getTeam()) {
            return String.format("%s (%d/%d) attacks ??? on %s!", unit1Name, attackPoints1,
                    defencePoints1, fieldName);
        } else {
            return String.format(DUEL_REGULAR_UNIT_FORMAT, unit1Name, attackPoints1,
                    defencePoints1, unit2Name, attackPoints, defencePoint2, fieldName);
        }
    }
}
