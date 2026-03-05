package edu.kit.kastel.model.duel;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.ai.Printer;
import edu.kit.kastel.model.board.Field;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
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
    public static DuelResult executeDuel(RegularUnit attacker, Unit defender) {
        attacker.flip();
        if (defender instanceof RegularUnit) {
            defender.flip();
        }

        DuelType type = getDuelType(defender);
        int attackPointA = attacker.getAttackPoints();

        if (type == DuelType.BLOCKADE && defender instanceof RegularUnit) {
            int defencePointB = ((RegularUnit) defender).getDefencePoints();
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
        }

        if (type == DuelType.AGAINST_FARMER_KING) {
            return new DuelResult(attackPointA, defender.getTeam(),
                    false, false, false
            );
        }

        if (type == DuelType.STANDARD) {
            assert defender instanceof RegularUnit;
            int attackPointB = ((RegularUnit) defender).getAttackPoints();
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
        return null;
    }

    /**
     * returns the type of the duel the two units are partaking in.
     *
     * @param defender the defending unit
     * @return the type of the duel
     */
    public static DuelType getDuelType(Unit defender) {
        if (defender instanceof FarmerKing) {
            return DuelType.AGAINST_FARMER_KING;
        }
        if (defender instanceof RegularUnit && ((RegularUnit) defender).isBlocking()) {
            return DuelType.BLOCKADE;
        }
        return DuelType.STANDARD;
    }

    /**
     * returns a formatted string of the duel logic.
     * @param duelResult result of the duel
     * @param attackerUnit attacker unit
     * @param defender defender unit
     * @param game the game in which the duel logic takes place
     * @param attackerWasFaceDown whether the attacker was hidden
     * @param defenderWasFaceDown whether the defender was hidden
     * @param targetPosition the position in which the duel takes place
     * @return formatted duel message
     */
    public static String duelExecutionDisplay(DuelResult duelResult, Unit attackerUnit,
                                              Unit defender, Game game,
                                              boolean attackerWasFaceDown,
                                              boolean defenderWasFaceDown,
                                              Position targetPosition) {
        StringBuilder stringBuilder = new StringBuilder();
        RegularUnit attacker = (RegularUnit) attackerUnit;
        Field targetedField = game.getFarmlandBoard().getField(targetPosition);
        Field selectedField = game.getFarmlandBoard().getField(game.getSavedPosition());
        if (defender instanceof FarmerKing) {
            stringBuilder.append(Printer.duelWithFarmerKingDisplay(attacker, defender, targetedField));
        } else if (defender instanceof RegularUnit) {
            stringBuilder.append(Printer.duelWithRegularUnitDisplay(attacker, defender, targetedField, defenderWasFaceDown));
            stringBuilder.append(System.lineSeparator());
        }
        if (attackerWasFaceDown) {
            stringBuilder.append(String.format("%s (%d/%d) was flipped on %s!",
                    attacker.getName(), attacker.getAttackPoints(),
                    attacker.getDefencePoints(), selectedField.getPosition().toString()));
            stringBuilder.append(System.lineSeparator());
        }
        if (defenderWasFaceDown) {
            stringBuilder.append(String.format("%s (%d/%d) was flipped on %s!",
                    defender.getName(), ((RegularUnit) defender).getAttackPoints(),
                    ((RegularUnit) defender).getDefencePoints(), targetedField.getPosition().toString()));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.attackerEliminated()) {
            game.getFarmlandBoard().removeUnit(game.getSavedPosition());
            stringBuilder.append(String.format("%s was eliminated!", attacker.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.defenderEliminated()) {
            game.getFarmlandBoard().removeUnit(targetPosition);
            stringBuilder.append(String.format("%s was eliminated!", defender.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.damagedTeam() != null) {
            Team damagedTeam = duelResult.damagedTeam();
            int damage = duelResult.damage();
            damagedTeam.takeDamage(damage);
            stringBuilder.append(String.format("%s takes %d damage!", damagedTeam.getName(), damage));
            stringBuilder.append(System.lineSeparator());
        }
        if (duelResult.attackerMoves()) {
            stringBuilder.append(game.moveUnit(attacker, game.getSavedPosition(), targetPosition));
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
            stringBuilder.append(String.format("%s wins!", winnerTeam.getName()));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
