package edu.kit.kastel.model.ai;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.WeightedRandom;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.board.Vector2D;
import edu.kit.kastel.model.duel.Duel;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class implements the logic for when it is AI's turn.
 *
 * @author ucktt
 */
public class AiTurn {

    private final Game game;
    private final Random random;
    private final Team aiTeam;
    private final AiScoring scoring;

    /**
     * Constructs an AI turn.
     *
     * @param game   game in which it is AI's turn
     * @param random random used for weighted random methods
     */
    public AiTurn(Game game, Random random) {
        this.game = game;
        this.random = random;
        this.aiTeam = game.getCurrentTeam();
        this.scoring = new AiScoring(game, aiTeam);

    }

    /**
     * This method executes the AI logic when it is AI's turn.
     */
    public void executeTurn() {
        moveFarmerKing();
        placeUnit();
        moveUnits();
        endTurn();
        game.nextTurn();
    }

    private void moveFarmerKing() {
        Map<Integer, List<Position>> scoredPositions = new HashMap<>();
        FarmerKing farmerKing = aiTeam.getFarmerKing();
        Position farmerKingPosition = game.getFarmlandBoard().findPosition(farmerKing);
        Position targetPosition;

        for (Vector2D direction : Vector2D.getDirections()) {
            targetPosition = farmerKingPosition.move(direction);

            if (!Position.isInBounds(targetPosition.column(), targetPosition.row())) {
                continue;
            }

            Field field = game.getFarmlandBoard().getField(targetPosition);
            if (!field.isEmpty() && field.getUnit().getTeam() != aiTeam) {
                continue;
            }

            int score = scoring.getScoreForFarmerKing(farmerKingPosition, targetPosition);
            scoredPositions.computeIfAbsent(score, s -> new ArrayList<>())
                    .add(targetPosition);
        }

        int maxScore = Collections.max(scoredPositions.keySet());
        List<Position> selectedPositions = scoredPositions.get(maxScore);
        targetPosition = select(selectedPositions);
        game.moveUnit(farmerKing, farmerKingPosition, targetPosition);
    }

    private void placeUnit() {
        List<Unit> units = game.getFarmlandBoard().getUnitsForTeam(aiTeam);
        Map<Integer, List<Position>> scoredPositions = new HashMap<>();
        Set<Position> alreadyScored = new HashSet<>();
        FarmerKing enemyFarmerKing = game.getOpponentTeam().getFarmerKing();
        Position enemyFarmerKingPosition = game.getFarmlandBoard().findPosition(enemyFarmerKing);
        for (Unit unit : units) {
            Position currentPosition = game.getFarmlandBoard().findPosition(unit);
            List<Position> neighbors = game.getFarmlandBoard().getNeighbors(currentPosition, false);

            for (Position neighbor : neighbors) {
                if (alreadyScored.contains(neighbor)) {
                    continue;
                }
                Field field = game.getFarmlandBoard().getField(neighbor);
                if (!field.isEmpty() && field.getUnit().getTeam() != aiTeam) {
                    continue;
                }

                int score = scoring.getScoreForUnit(neighbor, enemyFarmerKingPosition);
                alreadyScored.add(neighbor);
                scoredPositions.computeIfAbsent(score, key -> new ArrayList<>())
                        .add(neighbor);
            }
        }
        int maxScore = Collections.max(scoredPositions.keySet());
        List<Position> selectedPositions = scoredPositions.get(maxScore);
        Position targetPosition = select(selectedPositions);
        int selectedIndex = getSelectedUnitIndex();
        executePlacement(selectedIndex, targetPosition);
    }

    private void executePlacement(int selectedUnitIndex, Position targetPosition) {
        RegularUnit unitToPlace = aiTeam.getHand().get(selectedUnitIndex);
        aiTeam.getHand().remove(selectedUnitIndex);
        unitToPlace.setTeam(aiTeam);
        unitToPlace.flip();

        Field field = game.getFarmlandBoard().getField(targetPosition);
        Unit unitInField = field.getUnit();

        if (unitInField == null) {
            game.getFarmlandBoard().placeUnit(unitToPlace, targetPosition);
        } else {
            game.mergeAction(unitInField, unitToPlace, targetPosition);
        }
        aiTeam.setHasPlaced(true);

        if (game.getFarmlandBoard().getUnitsForTeam(aiTeam).size() > 5) {
            game.getFarmlandBoard().removeUnit(targetPosition);
        }
    }

    private void moveUnits() {
        while (true) {
            List<Unit> aiUnits = game.getFarmlandBoard().getUnitsForTeam(aiTeam);
            List<Unit> unmovedUnits = new ArrayList<>();
            for (Unit unit : aiUnits) {
                if (!unit.hasMoved()) {
                    unmovedUnits.add(unit);
                }
            }
            aiUnits = unmovedUnits;

            if (aiUnits.isEmpty()) {
                break;
            }

            Unit winningUnit = getWinningUnit(aiUnits);

            if (winningUnit instanceof FarmerKing) {
                winningUnit.setHasMoved(true);
                continue;
            }

            Position winningUnitPosition = game.getFarmlandBoard().findPosition(winningUnit);

            List<Position> neighbors = game.getFarmlandBoard().getNeighbors(winningUnitPosition, false);

            List<Integer> moveScores = new ArrayList<>();
            boolean isPositive = false;

            for (Position neighbor : neighbors) {
                moveScores.add(scoring.getMovementScore(winningUnit, neighbor));
            }

            moveScores.add(scoring.getBlockScore((RegularUnit) winningUnit));
            moveScores.add(scoring.getEnPlaceScore((RegularUnit) winningUnit));

            for (int moveScore : moveScores) {
                if (moveScore > 0) {
                    isPositive = true;
                    break;
                }
            }
            if (!isPositive) {
                ((RegularUnit) winningUnit).startBlocking();
                winningUnit.setHasMoved(true);
                continue;
            }

            int index = WeightedRandom.weightedRandomSelection(moveScores, random);

            if (index < 4) {
                Position targetPosition = neighbors.get(index);
                executeMove(winningUnit, winningUnitPosition, targetPosition);
            } else if (index == 4) {
                ((RegularUnit) winningUnit).startBlocking();
            } else {
                game.moveUnit(winningUnit, winningUnitPosition, winningUnitPosition);
                winningUnit.setHasMoved(true);
            }
        }
    }

    private void executeMove(Unit winningUnit, Position selectedPosition, Position targetPosition) {
        Field targetField = game.getFarmlandBoard().getField(targetPosition);
        if (targetField.isEmpty()) {
            game.moveUnit(winningUnit, selectedPosition, targetPosition);
        } else {
            Unit unitInField = targetField.getUnit();
            if (unitInField.getTeam() == aiTeam && !(winningUnit instanceof FarmerKing)) {
                game.mergeAction(unitInField, (RegularUnit) winningUnit, targetPosition);
            } else if (unitInField.getTeam() != aiTeam && !(winningUnit instanceof FarmerKing)) {

                Duel.executeDuel((RegularUnit) winningUnit, unitInField);
            }
        }
        winningUnit.setHasMoved(true);
    }

    private Unit getWinningUnit(List<Unit> aiUnits) {
        Map<Integer, Set<Unit>> scoredUnits = new HashMap<>();
        for (Unit unit : aiUnits) {
            Position unitPosition = game.getFarmlandBoard().findPosition(unit);
            List<Position> neighbors = game.getFarmlandBoard().getNeighbors(unitPosition, false);

            int totalScore = 0;
            for (Position neighbor : neighbors) {
                int score = scoring.getMovementScore(unit, neighbor);
                totalScore += score;
            }
            int blockScore = scoring.getBlockScore((RegularUnit) unit);
            int enPlaceScore = scoring.getEnPlaceScore((RegularUnit) unit);
            totalScore += blockScore + enPlaceScore;
            scoredUnits.computeIfAbsent(totalScore, key -> new LinkedHashSet<>())
                    .add(unit);
        }
        int maxScore = Collections.max(scoredUnits.keySet());
        Set<Unit> best = scoredUnits.get(maxScore);
        return select(new ArrayList<>(best));
    }

    private int getSelectedUnitIndex() {
        List<RegularUnit> hand = aiTeam.getHand();
        List<Integer> weights = new ArrayList<>();
        for (RegularUnit regularUnit : hand) {
            int attackPoint = regularUnit.getAttackPoints();
            weights.add(attackPoint);
        }
        return WeightedRandom.weightedRandomSelection(weights, random);
    }

    private <T> T select(List<T> candidates) {
        if (candidates.size() == 1) {
            return candidates.getFirst();
        }
        List<Integer> weights = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i++) {
            weights.add(1);
        }
        int index = WeightedRandom.weightedRandomSelection(weights, random);
        return candidates.get(index);
    }

    private void endTurn() {
        List<RegularUnit> hand = aiTeam.getHand();
        if (hand.size() == 5) {
            List<Integer> weights = new ArrayList<>();
            for (RegularUnit regularUnit : hand) {
                weights.add(regularUnit.getAttackPoints() + regularUnit.getDefencePoints());
            }
            int index = WeightedRandom.inverseWeightedRandomSelection(weights, random);
            aiTeam.removeUnitFromHand(index + 1);
        }
    }
}
