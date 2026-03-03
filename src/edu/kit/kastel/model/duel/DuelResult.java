package edu.kit.kastel.model.duel;

import edu.kit.kastel.model.unit.Team;

/**
 * this record represents the duel result between two units.
 * @param damage the damage point that the damaged team has to take
 * @param damagedTeam the team of the unit that receives the damage
 * @param defenderEliminated true if the defending unit was eliminated; false if not.
 * @param attackerEliminated true if the attacking unit was eliminated; false if not.
 * @param attackerMoves true if the attacker is allowed to move after the duel; false if not
 * @author ucktt
 */
public record DuelResult(
        int damage,
        Team damagedTeam,
        boolean defenderEliminated,
        boolean attackerEliminated,
        boolean attackerMoves
) {

    /**
     * checks if the defending unit is eliminated.
     * @return true if the defending unit was eliminated; false if not
     */
    public boolean defenderEliminated() {

        return defenderEliminated;
    }

    /**
     * checks if the attacking unit is eliminated.
     * @return true if the attacking unit was eliminated; false if not
     */
    public boolean attackerEliminated() {
        return attackerEliminated;
    }

    /**
     * the damage point that the affected team has to take.
     * @return the damage point
     */
    public int damage() {
        return damage;
    }
}
