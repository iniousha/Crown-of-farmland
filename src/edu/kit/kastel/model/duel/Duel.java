package edu.kit.kastel.model.duel;

import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;

/**
 * this utility class provides methods relating the duel between two units.
 * @author ucktt
 */
public final class Duel {

    private Duel() {
    }

    /**
     * determines the outcome of a duel between a defending and an attacking unit.
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
}
