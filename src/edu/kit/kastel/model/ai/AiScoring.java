package edu.kit.kastel.model.ai;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * aslndf.
 * @author ucktt
 */
public class AiScoring {

    private final Game game;
    private final Team aiTeam;

    /**
     * d;dsi.
     * @param game sdihf
     * @param aiTeam adfh
     */
    public AiScoring(Game game, Team aiTeam) {
        this.game = game;
        this.aiTeam = aiTeam;
    }

    /**
     * asdk;ads.
     * @param farmerKingPosition ;fvhds
     * @param targetPosition ;dvhbsdiu
     * @return dhs
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
        if (fellowPresentUnit != null && fellowPresentUnit.getTeam() == aiTeam) {
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
                if (unitTeam == this.aiTeam) {
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
     * skd;jfhsa'h.
     * @param currentPosition dfvh
     * @param targetPosition dh
     * @return duhvf
     */
    //placeUnit helper methods:
    public int getScoreForUnit(Position currentPosition, Position targetPosition) {
        return -steps(currentPosition, targetPosition)
                + (2 * enemiesOfUnit(currentPosition))
                - fellowsOfUnit(currentPosition);
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
     * asdfhe.
     * @param unit sduhf
     * @return asdoihc
     */
    //moveUnits helper methods:
    public int getBlockScore(RegularUnit unit) {
        int maximumAttackPoint = getMaximumAttackPoints();
        return Math.max(1, (unit.getDefencePoints() - maximumAttackPoint) / 100);
    }

    /**
     * sdjf.
     * @param unit adfh
     * @return dfh
     */
    public int getEnPlaceScore(RegularUnit unit) {
        int maximumAttackPoint = getMaximumAttackPoints();
        return Math.max(0, (unit.getAttackPoints() - maximumAttackPoint) / 100);
    }

    private int getMaximumAttackPoints() {
        List<Integer> attackPoints = new ArrayList<>();
        List<Unit> enemyUnits = game.getFarmlandBoard().getUnitsForTeam(game.getOpponentTeam());
        for (Unit enemyUnit : enemyUnits) {
            if (!(enemyUnit instanceof FarmerKing)) {
                int attackPoint = ((RegularUnit) enemyUnit).getAttackPoints();
                attackPoints.add(attackPoint);
            }
            if (attackPoints.isEmpty()) {
                return 0;
            }
        }
        return Collections.max(attackPoints);
    }

    /**
     * 'dofh.
     * @param unit ;dfhv
     * @param neighbor adfh
     * @return dhv
     */
    public int getMovementScore(Unit unit, Position neighbor) {
        Field field = game.getFarmlandBoard().getField(neighbor);
        if (field.isEmpty()) {
            Position farmerKingPosition = game.getFarmlandBoard().findPosition(game.getOpponentTeam().getFarmerKing());
            return steps(neighbor, farmerKingPosition) - enemiesOfUnit(neighbor);
        } else {
            Unit neighborUnit = field.getUnit();
            if (neighborUnit.getTeam() == aiTeam) {
                return mergeAction((RegularUnit) unit, (RegularUnit) neighborUnit);
            } else {
                return duelAction(unit, neighborUnit);
            }
        }
    }

    private int duelAction(Unit unit, Unit neighborUnit) {
        if (neighborUnit instanceof FarmerKing) {
            return ((RegularUnit) unit).getAttackPoints();
        } else if (!neighborUnit.isFaceUp()) {
            return ((RegularUnit) unit).getAttackPoints() - 500;
        } else if (neighborUnit instanceof RegularUnit
                && ((RegularUnit) neighborUnit).isBlocking()) {
            return ((RegularUnit) unit).getAttackPoints() - ((RegularUnit) neighborUnit).getDefencePoints();
        } else {
            assert neighborUnit instanceof RegularUnit;
            return 2 * (((RegularUnit) unit).getAttackPoints() - ((RegularUnit) neighborUnit).getAttackPoints());
        }
    }

    private int mergeAction(RegularUnit unit, RegularUnit neighborUnit) {
        RegularUnit mergedUnit = unit.mergeWith(neighborUnit);
        if (mergedUnit != null) {
            return getMergePoint(unit, neighborUnit, mergedUnit);
        } else {
            int attackPointsB = neighborUnit.getAttackPoints();
            int defencePointsB = neighborUnit.getDefencePoints();
            return (-attackPointsB) + (-defencePointsB);
        }
    }

    private int getMergePoint(RegularUnit unit, RegularUnit neighborUnit, RegularUnit mergedUnit) {
        int attackPointsA = unit.getAttackPoints();
        int defencePointsB = neighborUnit.getDefencePoints();
        int attackPointsAB = mergedUnit.getAttackPoints();
        int defencePointsAB = mergedUnit.getDefencePoints();
        return attackPointsAB + defencePointsAB - attackPointsA - defencePointsB;
    }
}
