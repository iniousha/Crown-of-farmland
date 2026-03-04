package edu.kit.kastel.model.ai;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.board.FarmlandBoard;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.merge.Merge;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * aslndf.
 *
 * @author ucktt
 */
public class AiScoring {

    private final Game game;
    private final Team aiTeam;

    /**
     * d;dsi.
     *
     * @param game   sdihf
     * @param aiTeam adfh
     */
    public AiScoring(Game game, Team aiTeam) {
        this.game = game;
        this.aiTeam = aiTeam;
    }

    /**
     * asdk;ads.
     *
     * @param farmerKingPosition ;fvhds
     * @param targetPosition     ;dvhbsdiu
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
     * skd;jfhsa'h.
     *
     * @param currentPosition dfvh
     * @param targetPosition  dh
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
     *
     * @param unit sduhf
     * @return asdoihc
     */
    //moveUnits helper methods:
    public int getBlockScore(Unit unit) {
        if (unit instanceof FarmerKing) {
            return 0;
        }
        int maximumAttackPoint = getMaximumAttackPoints();
        return Math.max(1, (((RegularUnit) unit).getDefencePoints() - maximumAttackPoint) / 100);
    }

    /**
     * sdjf.
     *
     * @param unit adfh
     * @return dfh
     */
    public int getEnPlaceScore(Unit unit) {
        if (unit instanceof FarmerKing) {
            return 0;
        }
        int maximumAttackPoint = getMaximumAttackPoints();
        return Math.max(0, (((RegularUnit) unit).getAttackPoints() - maximumAttackPoint) / 100);
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
     *
     * @param unitToPlace         ;dfhv
     * @param unitInFieldPosition adfh
     * @return dhv
     */
    public int getMovementScore(Unit unitToPlace, Position unitInFieldPosition) {
        Field field = game.getFarmlandBoard().getField(unitInFieldPosition);
        if (field.isEmpty()) {
            Position enemyFarmerKingPosition = game.getFarmlandBoard().findPosition(game.getOpponentTeam().getFarmerKing());
            return (10 * steps(unitInFieldPosition, enemyFarmerKingPosition)) - enemiesOfUnit(unitInFieldPosition);
        } else {
            Unit unitInField = field.getUnit();
            if (unitInField.getTeam() == aiTeam
                    && !(unitInField instanceof FarmerKing)
                    && !(unitToPlace instanceof FarmerKing)) {
                return mergeAction((RegularUnit) unitToPlace, (RegularUnit) unitInField);
            } else if (unitInField.getTeam() != aiTeam
                    && !(unitInField instanceof FarmerKing)
                    && !(unitToPlace instanceof FarmerKing)) {
                return duelAction(unitToPlace, unitInField);
            } else if (unitInField.getTeam() == aiTeam
                    && field.getUnit() instanceof FarmerKing) {
                return 0;
            }
        }
        return 0;
    }

    private int duelAction(Unit attacker, Unit defender) {
        if (defender instanceof FarmerKing) {
            return ((RegularUnit) attacker).getAttackPoints();
        } else if (!defender.isFaceUp()) {
            return ((RegularUnit) attacker).getAttackPoints() - 500;
        } else if (defender instanceof RegularUnit
                && ((RegularUnit) defender).isBlocking()) {
            return ((RegularUnit) attacker).getAttackPoints() - ((RegularUnit) defender).getDefencePoints();
        } else if (defender instanceof RegularUnit && attacker instanceof RegularUnit) {
            return 2 * (((RegularUnit) attacker).getAttackPoints() - ((RegularUnit) defender).getAttackPoints());
        }
        return 0;
    }

    private int mergeAction(RegularUnit unit, RegularUnit neighborUnit) {
        Merge merge = new Merge(unit, neighborUnit);
        RegularUnit mergedUnit = merge.mergeWith();
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
