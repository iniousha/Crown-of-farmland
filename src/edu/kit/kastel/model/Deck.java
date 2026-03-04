package edu.kit.kastel.model;

import edu.kit.kastel.model.unit.RegularUnit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * this class represents the deck owned by each team.
 * @author ucktt
 */
public class Deck {

    private final Deque<RegularUnit> regularUnits;

    /**
     * constructs a deck instance using the given parameter.
     * @param regularUnits the regular units to initialize the deck with
     */
    public Deck(Deque<RegularUnit> regularUnits) {
        this.regularUnits = new ArrayDeque<>(regularUnits);
    }

    /**
     * draws a card from the top of the deck.
     * @return the regular unit drawn from the top
     */
    public RegularUnit drawFromTop() {
        return regularUnits.pollFirst();
    }

    /**
     * returns the number of regular units in the deck.
     * @return the deck size
     */
    public int getDeckSize() {
        return regularUnits.size();
    }

    /**
     * shuffles the deck in a random order.
     * @param random random number generator used for shuffling
     */
    public void shuffle(Random random) {
        List<RegularUnit> units = new ArrayList<>(regularUnits);
        Collections.shuffle(units, random);
        regularUnits.clear();
        regularUnits.addAll(units);
    }

    /**
     * checks whether the deck is empty.
     * @return true if there are no card in the deck; false otherwise
     */
    public boolean isEmpty() {
        return regularUnits.isEmpty();
    }
}

