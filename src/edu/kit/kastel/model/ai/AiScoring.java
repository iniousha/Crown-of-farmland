package edu.kit.kastel.model.ai;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.board.Vector2D;
import edu.kit.kastel.model.merge.Merge;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

import java.util.List;

/**
 * this class provides scoring functions used in AiTurn class to evaluate different actions.
 *
 * @author ucktt
 */
public class AiScoring {

    private final Game game;
    private final Team aiTeam;

    /**
     * constructs an AI scoring instance for the specified game and team.
     *
     * @param game   the current game instance
     * @param aiTeam the AI team
     */
    public AiScoring(Game game, Team aiTeam) {
        this.game = game;
        this.aiTeam = aiTeam;
    }

    /**
     * returns the score for possible movements of the farmer king to the candidate position.
     *
     * @param farmerKingPosition current position of the farmer king
     * @param targetPosition candidate position to which the farmer king would move
     * @return score for each possible position
     */
    //moveFarmerKing helper methods:
    public int getScoreForFarmerKing(Position farmerKingPosition, Position targetPosition) {
        return fellowsOfFarmerKing(targetPosition)
                - (2 * enemiesOfFarmerKing(targetPosition))
                - distance(farmerKingPosition, targetPosition)
                - (3 * fellowPresent(targetPosition));
    }

    private int distance(Position currentPosition, Position targetPosition) {
        return currentPosition.equals(targetPosition) ? 0 : 1;
    }

    private int fellowPresent(Position selectedPosition) {
        Field field = game.getFarmlandBoard().getField(selectedPosition);
        Unit fellowPresentUnit = field.getUnit();
        if (fellowPresentUnit != null
                && fellowPresentUnit.getTeam() == aiTeam
                && !(fellowPresentUnit instanceof FarmerKing)) {
            return 1;
        }
        return 0;
    }

    private int fellowsOfFarmerKing(Position currentPosition) {
        int count = 0;
        FarmlandBoard board = game.getFarmlandBoard();
        List<Field> fields = board.getAllFields();
        for (Field field : fields) {
            Position position = field.getPosition();
            if (currentPosition.isAdjacentTo(position, true) && !field.isEmpty()) {
                Unit unitInField = field.getUnit();
                Team unitTeam = unitInField.getTeam();
                if (unitTeam == this.aiTeam && !(unitInField instanceof FarmerKing)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int enemiesOfFarmerKing(Position currentPosition) {
        int count = 0;
        FarmlandBoard board = game.getFarmlandBoard();
        List<Field> fields = board.getAllFields();
        for (Field field : fields) {
            Position position = field.getPosition();
            if (currentPosition.isAdjacentTo(position, true) && !field.isEmpty()) {
                Unit unitInField = field.getUnit();
                Team unitTeam = unitInField.getTeam();
                if (unitTeam != this.aiTeam) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * returns the score for placing the unit from hand on the candidate position.
     *
     * @param candidatePosition the position to evaluate for placement
     * @param enemyFarmerKingPosition  position of the enemy's farmer king
     * @return score for the given candidate position
     */
    //placeUnit helper methods:
    public int getPlacementScoreForUnit(Position candidatePosition, Position enemyFarmerKingPosition) {
        return -steps(candidatePosition, enemyFarmerKingPosition)
                + (2 * enemiesOfUnit(candidatePosition))
                - fellowsOfUnit(candidatePosition);
    }

    private int steps(Position currentPosition, Position targetPosition) {
        return currentPosition.distanceTo(targetPosition);
    }

    private int enemiesOfUnit(Position currentPosition) {
        int count = 0;
        FarmlandBoard board = game.getFarmlandBoard();
        List<Field> fields = board.getAllFields();
        for (Field field : fields) {
            Position position = field.getPosition();
            if (currentPosition.isAdjacentTo(position, false) && !field.isEmpty()) {
                Unit unitInField = field.getUnit();
                Team unitTeam = unitInField.getTeam();
                if (unitTeam != this.aiTeam) {
                    count++;
                }
            }
        }
        return count;
    }

    private int fellowsOfUnit(Position currentPosition) {
        int count = 0;
        FarmlandBoard board = game.getFarmlandBoard();
        List<Field> fields = board.getAllFields();
        for (Field field : fields) {
            Position position = field.getPosition();
            if (currentPosition.isAdjacentTo(position, false) && !field.isEmpty()) {
                Unit unitInField = field.getUnit();
                Team unitTeam = unitInField.getTeam();
                if (unitTeam == this.aiTeam) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * returns the score for moving the specified unit.
     *
     * @param unitToPlace the unit whose movement score is being calculated
     * @param unitInFieldPosition the candidate position
     * @return the movement score for the candidate position
     */
    //moveUnits helper methods:
    public int getMovementScore(Unit unitToPlace, Position unitInFieldPosition) {
        Field field = game.getFarmlandBoard().getField(unitInFieldPosition);
        if (field.isEmpty()) {
            Position enemyFarmerKingPosition = game.getFarmlandBoard().findPosition(game.getOpponentTeam().getFarmerKing());
            return (10 * steps(unitInFieldPosition, enemyFarmerKingPosition)) - enemiesOfUnit(unitInFieldPosition);
        } else {
            Unit unitInField = field.getUnit();
            if (unitInField.getTeam() == aiTeam
                    && !(unitInField.isFarmerKing())
                    && !(unitToPlace.isFarmerKing())) {
                return mergeAction((RegularUnit) unitToPlace, (RegularUnit) unitInField);
            } else if (unitInField.getTeam() != aiTeam
                    && !(unitToPlace.isFarmerKing())) {
                return duelAction(unitToPlace, unitInField);
            } else if (unitInField.getTeam() == aiTeam
                    && unitInField.isFarmerKing()) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * returns the blocking score for the given unit.
     *
     * @param unit the unit whose blocking score is being calculated
     * @return the blocking score, returns 0 if the unit was farmer king
     */
    public int getBlockScore(Unit unit) {
        if (unit instanceof FarmerKing) {
            return 0;
        }
        Position unitPosition = game.getFarmlandBoard().findPosition(unit);
        int maximumAttackPoint = getMaximumAttackPoints(unitPosition);
        return Math.max(1, (((RegularUnit) unit).getDefencePoints() - maximumAttackPoint) / 100);
    }

    /**
     * returns the score for the situation where unit stays in place "enPlaceMovement".
     *
     * @param unit the unit whose enPlace movement score is being calculated
     * @return enPlace movement score for the specified unit
     */
    public int getEnPlaceScore(Unit unit) {
        if (unit instanceof FarmerKing) {
            return 0;
        }
        Position unitPosition = game.getFarmlandBoard().findPosition(unit);
        int maximumAttackPoint = getMaximumAttackPoints(unitPosition);
        return Math.max(0, (((RegularUnit) unit).getAttackPoints() - maximumAttackPoint) / 100);
    }

    private int getMaximumAttackPoints(Position unitPosition) {
        int max = 0;
        for (Vector2D direction : Vector2D.getFourDirections()) {
            Position neighbor = unitPosition.move(direction);
            if (!Position.isInBounds(neighbor.column(), neighbor.row())) {
                continue;
            }
            Field field = game.getFarmlandBoard().getField(neighbor);
            if (!field.isEmpty() && field.getUnit().getTeam() != aiTeam
                    && !field.getUnit().isFarmerKing()) {
                int attackPoints = ((RegularUnit) field.getUnit()).getAttackPoints();
                if (attackPoints > max) {
                    max = attackPoints;
                }
            }
        }
        return max;
    }

    private int duelAction(Unit attacker, Unit defender) {
        if (defender.isFarmerKing()) {
            return ((RegularUnit) attacker).getAttackPoints();
        } else if (!defender.isFaceUp()) {
            return ((RegularUnit) attacker).getAttackPoints() - 500;
        } else if (!defender.isFarmerKing()
                && ((RegularUnit) defender).isBlocking()) {
            return ((RegularUnit) attacker).getAttackPoints() - ((RegularUnit) defender).getDefencePoints();
        } else if (!defender.isFarmerKing() && !attacker.isFarmerKing()) {
            return 2 * (((RegularUnit) attacker).getAttackPoints() - ((RegularUnit) defender).getAttackPoints());
        }
        return 0;
    }

    private int mergeAction(RegularUnit unit, RegularUnit neighborUnit) {
        Merge merge = new Merge(unit, neighborUnit);
        RegularUnit mergedUnit = merge.mergeWith();
        if (mergedUnit != null) {
            return getMergePoint(unit, mergedUnit);
        } else {
            int attackPointsB = neighborUnit.getAttackPoints();
            int defencePointsB = neighborUnit.getDefencePoints();
            return (-attackPointsB) + (-defencePointsB);
        }
    }

    private int getMergePoint(RegularUnit unit, RegularUnit mergedUnit) {
        int attackPointsA = unit.getAttackPoints();
        int defencePointsA = unit.getDefencePoints();
        int attackPointsAB = mergedUnit.getAttackPoints();
        int defencePointsAB = mergedUnit.getDefencePoints();
        return attackPointsAB + defencePointsAB - attackPointsA - defencePointsA;
    }
}
