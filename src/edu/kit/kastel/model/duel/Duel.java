package edu.kit.kastel.model.duel;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.MessageFormatter;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.Team;
import edu.kit.kastel.model.unit.Unit;

/**
 * this utility class provides methods relating the duel between two units.
 *
 * @author ucktt
 */
public final class Duel {

    private Duel() {
    }

    /**
     * determines the outcome of a duel between a defending and an attacking unit.
     *
     * @param attacker the attacking unit
     * @param defender the defending unit
     * @return the result of the duel
     */
    public static DuelResult executeDuel(Unit attacker, Unit defender) {
        attacker.flip();
        if (!defender.isFarmerKing()) {
            defender.flip();
        }

        DuelType type = getDuelType(defender);
        int attackPointA = attacker.getAttackPoints();

        if (type == DuelType.BLOCKADE && !defender.isFarmerKing()) {
            int defencePointB = defender.getDefencePoints();
            if (attackPointA > defencePointB) {
                return new DuelResult(0, null,
                        true, false, true);
            }
            if (attackPointA < defencePointB) {
                int damage = defencePointB - attackPointA;
                return new DuelResult(damage, attacker.getTeam(), false, false, false
                );
            }
            return new DuelResult(0, null,
                    false, false, false
            );
        } else if (type == DuelType.AGAINST_FARMER_KING) {
            return new DuelResult(attackPointA, defender.getTeam(),
                    false, false, false
            );
        } else {
            int attackPointB = defender.getAttackPoints();
            if (attackPointA > attackPointB) {
                int damage = attackPointA - attackPointB;
                return new DuelResult(damage, defender.getTeam(),
                        true, false, true
                );
            }
            if (attackPointA < attackPointB) {
                int damage = attackPointB - attackPointA;
                return new DuelResult(damage, attacker.getTeam(),
                        false, true, false
                );
            }
            return new DuelResult(0,
                    null, true, true, false
            );
        }
    }

    private static DuelType getDuelType(Unit defender) {
        if (defender.isFarmerKing()) {
            return DuelType.AGAINST_FARMER_KING;
        }
        if (!defender.isFarmerKing() && defender.isBlocking()) {
            return DuelType.BLOCKADE;
        }
        return DuelType.STANDARD;
    }

    /**
     * returns a formatted string of the duel logic.
     *
     * @param duelResult          result of the duel
     * @param attacker        attacker unit
     * @param defender            defender unit
     * @param game                the game in which the duel logic takes place
     * @param attackerWasFaceDown whether the attacker was hidden
     * @param defenderWasFaceDown whether the defender was hidden
     * @param targetPosition      the position in which the duel takes place
     * @return formatted duel message
     */
    public static String duelExecutionDisplay(DuelResult duelResult, Unit attacker, Unit defender, Game game,
                                              boolean attackerWasFaceDown,
                                              boolean defenderWasFaceDown,
                                              Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        Field targetedField = game.getFarmlandBoard().getField(targetPosition);
        Field selectedField = game.getFarmlandBoard().getField(game.getSavedPosition());
        if (defender.isFarmerKing()) {

            stringBuilder.append(MessageFormatter.duelWithFarmerKingDisplay(attacker, defender, targetedField));
            stringBuilder.append(System.lineSeparator());
        } else if (!defender.isFarmerKing()) {
            stringBuilder.append(MessageFormatter.duelWithRegularUnitDisplay(attacker, defender, targetedField, defenderWasFaceDown));
            stringBuilder.append(System.lineSeparator());
        }
        stringBuilder.append(flipDisplay(attacker, defender,
                attackerWasFaceDown, defenderWasFaceDown, selectedField, targetedField));
        stringBuilder.append(eliminationDisplay(duelResult, attacker, defender, game, targetPosition));

        if (duelResult.damagedTeam() != null) {
            Team damagedTeam = duelResult.damagedTeam();
            int damage = duelResult.damage();
            damagedTeam.takeDamage(damage);
            stringBuilder.append(String.format("%s takes %d damage!", damagedTeam.getName(), damage));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.attackerMoves()) {
            stringBuilder.append(game.moveUnit(attacker, game.getSavedPosition(), targetPosition));
        } else {
            attacker.setHasMoved(true);
        }
        if (game.getCurrentTeam().getLifePoints() <= 0) {
            game.setWinner(game.getOpponentTeam());
            stringBuilder.append(String.format("%s's life points dropped to 0!", game.getCurrentTeam().getName()));
            stringBuilder.append(System.lineSeparator());
        } else if (game.getOpponentTeam().getLifePoints() <= 0) {
            game.setWinner(game.getCurrentTeam());
            stringBuilder.append(String.format("%s's life points dropped to 0!", game.getOpponentTeam().getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (game.isGameOver()) {
            Team winnerTeam = game.getWinner();
            if (game.getOpponentTeam().isDeckEmpty()) {
                stringBuilder.append(MessageFormatter.deckEmptyDisplay(game.getOpponentTeam()));
            }
            stringBuilder.append(MessageFormatter.winnerDisplay(winnerTeam));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private static String flipDisplay(Unit attacker, Unit defender,
                                      boolean attackerWasFaceDown, boolean defenderWasFaceDown,
                                      Field selectedField, Field targetedField) {
        StringBuilder stringBuilder = new StringBuilder();
        if (attackerWasFaceDown) {
            stringBuilder.append(MessageFormatter.flipDisplay(attacker, selectedField));
            stringBuilder.append(System.lineSeparator());
        }
        if (defenderWasFaceDown) {
            stringBuilder.append(MessageFormatter.flipDisplay(defender, targetedField));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private static String eliminationDisplay(DuelResult duelResult, Unit attacker,
                                             Unit defender, Game game, Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        if (duelResult.attackerEliminated()) {
            game.getFarmlandBoard().removeUnit(game.getSavedPosition());
            stringBuilder.append(MessageFormatter.unitEliminationDisplay(attacker));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.defenderEliminated()) {
            game.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(MessageFormatter.unitEliminationDisplay(defender));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
