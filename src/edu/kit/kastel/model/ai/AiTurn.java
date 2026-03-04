package edu.kit.kastel.model.ai;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.WeightedRandom;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.board.Vector2D;
import edu.kit.kastel.model.duel.Duel;
import edu.kit.kastel.model.duel.DuelResult;
import edu.kit.kastel.model.merge.Merge;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;
import edu.kit.kastel.view.fileio.BoardPrinter;

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
     * @return returns a string formatted logic of AI's turn
     */
    public String executeTurn() {
        String sb = moveFarmerKing()
                + placeUnit()
                + moveUnits();
        endTurn();
        return sb;
    }

    private String moveFarmerKing() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Integer, List<Position>> scoredPositions = new HashMap<>();
        Set<Position> alreadyScored = new HashSet<>();
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
            if (alreadyScored.contains(targetPosition)) {
                continue;
            }
            alreadyScored.add(targetPosition);
            scoredPositions.computeIfAbsent(score, s -> new ArrayList<>())
                    .add(targetPosition);
        }

        int maxScore = Collections.max(scoredPositions.keySet());
        List<Position> selectedPositions = scoredPositions.get(maxScore);
        targetPosition = select(selectedPositions);
        stringBuilder.append(game.moveUnit(farmerKing, farmerKingPosition, targetPosition));
        stringBuilder.append(BoardPrinter.boardDisplay(game));
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(Printer.displayUnit(farmerKing, game));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }

    private String placeUnit() {
        StringBuilder stringBuilder = new StringBuilder();
        List<Unit> units = game.getFarmlandBoard().getUnitsForTeam(aiTeam);
        Map<Integer, List<Position>> scoredPositions = new HashMap<>();
        Set<Position> alreadyScored = new HashSet<>();
        FarmerKing enemyFarmerKing = game.getOpponentTeam().getFarmerKing();
        Position enemyFarmerKingPosition = game.getFarmlandBoard().findPosition(enemyFarmerKing);
        for (Unit unit : units) {
            Position currentPosition = game.getFarmlandBoard().findPosition(unit);
            List<Position> neighbors = game.getFarmlandBoard().getNeighbors(currentPosition, true);

            for (Position neighbor : neighbors) {
                if (alreadyScored.contains(neighbor)) {
                    continue;
                }
                Field field = game.getFarmlandBoard().getField(neighbor);
                if (!field.isEmpty() && field.getUnit().getTeam() != aiTeam) {
                    continue;
                }
                if (!field.isEmpty() && field.getUnit() instanceof FarmerKing) {
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
        stringBuilder.append(executePlacement(selectedIndex, targetPosition));
        stringBuilder.append(System.lineSeparator());
        game.setSavedPosition(targetPosition);
        stringBuilder.append(BoardPrinter.boardDisplay(game));
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(Printer.displayUnit(game.getFarmlandBoard().getField(targetPosition).getUnit(), game));
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }

    private String executePlacement(int selectedUnitIndex, Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        RegularUnit unitToPlace = aiTeam.getHand().get(selectedUnitIndex);
        aiTeam.getHand().remove(selectedUnitIndex);
        unitToPlace.setTeam(aiTeam);

        Field field = game.getFarmlandBoard().getField(targetPosition);
        Unit unitInField = field.getUnit();

        if (unitInField == null) {
            game.getFarmlandBoard().placeUnit(unitToPlace, targetPosition);
            stringBuilder.append(Printer.noMergeDisplay(aiTeam, unitToPlace, game.getFarmlandBoard().getField(targetPosition)));
        } else {
            Merge merge = new Merge(unitToPlace, (RegularUnit) unitInField);
            stringBuilder.append(merge.mergeResult(unitInField, unitToPlace, targetPosition, game));
        }
        aiTeam.setHasPlaced(true);

        if (game.getFarmlandBoard().getUnitsForTeam(aiTeam).size() > 5) {
            game.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(Printer.sixthUnitDisplay(aiTeam, unitToPlace, field));
        }
        return stringBuilder.toString();
    }

    private String moveUnits() {
        game.clearJustSelected();
        StringBuilder stringBuilder = new StringBuilder();
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
                continue;
            }
            Position winningUnitPosition = game.getFarmlandBoard().findPosition(winningUnit);

            boolean isPositive = false;
            List<Integer> moveScores = getMoveScores(winningUnit, winningUnitPosition);

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
                Position targetPosition = winningUnitPosition.move(Vector2D.getFourDirections().get(index));
                stringBuilder.append(executeMove(winningUnit, winningUnitPosition, targetPosition));
            } else if (index == 4) {
                ((RegularUnit) winningUnit).startBlocking();
                stringBuilder.append(Printer.blockDisplay(winningUnit, game.getFarmlandBoard().getField(winningUnitPosition)));
            } else {
                stringBuilder.append(game.moveUnit(winningUnit, winningUnitPosition, winningUnitPosition));
            }
            stringBuilder.append(BoardPrinter.boardDisplay(game));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(Printer.displayUnit(winningUnit, game));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String executeMove(Unit winningUnit, Position selectedPosition, Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        Field targetField = game.getFarmlandBoard().getField(targetPosition);
        if (targetField.isEmpty()) {
            stringBuilder.append(game.moveUnit(winningUnit, selectedPosition, targetPosition));
        } else {
            Unit unitInField = targetField.getUnit();
            if (unitInField.getTeam() == aiTeam
                    && !(winningUnit instanceof FarmerKing)
                    && !(unitInField instanceof FarmerKing)) {
                Merge merge = new Merge((RegularUnit) winningUnit, (RegularUnit) unitInField);
                stringBuilder.append(merge.mergeResult(unitInField, (RegularUnit) winningUnit, targetPosition, game));
            } else if (unitInField.getTeam() != aiTeam && !(winningUnit instanceof FarmerKing)) {
                boolean attackerWasFaceDown = !winningUnit.isFaceUp();
                boolean defenderWasFaceDown = (unitInField instanceof RegularUnit) && !unitInField.isFaceUp();
                System.err.println("defenderWasFaceDown: " + defenderWasFaceDown + " isFaceUp: " + unitInField.isFaceUp());

                DuelResult duelResult = Duel.executeDuel((RegularUnit) winningUnit, unitInField);
                stringBuilder.append(Duel.duelExecutionDisplay(duelResult, winningUnit, unitInField,
                        game, attackerWasFaceDown, defenderWasFaceDown, targetPosition));
            }
        }
        winningUnit.setHasMoved(true);
        return stringBuilder.toString();
    }

    private List<Integer> getMoveScores(Unit winningUnit, Position position) {
        List<Vector2D> directions = Vector2D.getFourDirections();
        List<Integer> moveScores = new ArrayList<>();
        for (Vector2D direction : directions) {
            Position neighbor = position.move(direction);
            if (!Position.isInBounds(neighbor.column(), neighbor.row())) {
                moveScores.add(0);
            } else {
                moveScores.add(scoring.getMovementScore(winningUnit, neighbor));
            }
        }
        moveScores.add(scoring.getBlockScore(winningUnit));
        moveScores.add(scoring.getEnPlaceScore(winningUnit));
        return moveScores;
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
            int blockScore = scoring.getBlockScore(unit);
            int enPlaceScore = scoring.getEnPlaceScore(unit);
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
