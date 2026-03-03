package edu.kit.kastel.model.unit;

import edu.kit.kastel.model.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a team in the gme.
 * @author ucktt
 */
public class Team {

    private final String name;
    private final FarmerKing farmerKing;
    private final Deck deck;
    private final List<RegularUnit> hand;
    private int lifePoints;
    private boolean hasSetPlace;
    private boolean isAiTeam;

    /**
     * constructs a team using the specified name and deck.
     * @param name the name of the team
     * @param deck the deck owned by the team and from which the cards are drawn
     */
    public Team(String name, Deck deck) {
        this.name = name;
        this.farmerKing = new FarmerKing();
        this.farmerKing.setTeam(this);
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.lifePoints = 8000;

        for (int i = 0; i < 4; i++) {
            RegularUnit unit = deck.drawFromTop();
            unit.setTeam(this);
            hand.add(unit);
        }
    }

    /**
     * draws a card from the top of the deck and adds it to the team's hand.
     */
    public void drawCard() {
        if (!deck.isEmpty() && hand.size() < 5) {
            RegularUnit unit = deck.drawFromTop();
            unit.setTeam(this);
            hand.add(unit);
        }
    }

    /**
     * checks whether the team is an AI team.
     * @return true if the team is AI; false otherwise
     */
    public boolean isAiTeam() {
        return this.isAiTeam;
    }

    /**
     * returns the name of the team.
     * @return name of the team
     */
    public String getName() {
        return name;
    }

    /**
     * returns the farmerKing of this team.
     * @return the farmerKing of this team
     */
    public FarmerKing getFarmerKing() {
        return farmerKing;
    }

    /**
     * checks whether this team's deck is empty.
     * @return true if the deck is empty; false if not
     */
    public boolean isDeckEmpty() {
        return this.deck.isEmpty();
    }

    /**
     * checks whether this team has placed a unit this turn.
     * @return true if the team has set a player on the board this turn; false otherwise
     */
    public boolean hasSetPlace() {
        return this.hasSetPlace;
    }

    /**
     * sets whether this team has placed a unit on the game board during this turn.
     * @param hasSetPlace true if the team has placed a unit this turn; false otherwise
     */
    public void setHasPlaced(boolean hasSetPlace) {
        this.hasSetPlace = hasSetPlace;
    }

    /**
     * returns this team's life points.
     * @return the life points of this team
     */
    public int getLifePoints() {
        return this.lifePoints;
    }

    /**
     * returns a list containing the regular units currently in the team's hand.
     * @return list containing the regular units in hand
     */
    public List<RegularUnit> getHand() {
        return this.hand;
    }

    /**
     * returns this team's deck.
     * @return this team's deck
     */
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * removes and returns the unit with the given one based index from the team's hand.
     * @param idx the given one based index of the unit to remove
     * @return the unit that is to be removed
     */
    public Unit removeUnitFromHand(int idx) {
        int zeroBasedIdx = idx - 1;
        Unit unit = hand.get(zeroBasedIdx);
        hand.remove(zeroBasedIdx);
        return unit;
    }

    /**
     * reduces this team's life points by the given damage point.
     * @param damage the damage point to apply
     */
    public void takeDamage(int damage) {
        this.lifePoints = getLifePoints() - damage;
    }
}

