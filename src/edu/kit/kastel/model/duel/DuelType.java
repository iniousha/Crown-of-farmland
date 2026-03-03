package edu.kit.kastel.model.duel;

/**
 * this enum represents the three different types of duel in this game.
 * @author ucktt
 */
public enum DuelType {
    /**
     * the defending unit is in blocked.
     */
    BLOCKADE,
    /**
     * the farmerKing of the defending team is being attacked.
     */
    AGAINST_FARMER_KING,
    /**
     * the defending team is not blocked and the duel is standard.
     */
    STANDARD
}
