package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility class providing methods for weighted random selection.
 * @author ucktt
 */
public final class WeightedRandom {

    private WeightedRandom() {
    }

    /**
     * selects an index randomly based on the list containing the weights.
     *
     * @param weights the list of weights
     * @param random  random number generator
     * @return the selected index
     */
    public static int weightedRandomSelection(List<Integer> weights, Random random) {
        int totalWeight = 0;
        int count = 0;
        for (Integer weight : weights) {
            totalWeight += Math.max(0, weight);
        }

        if (totalWeight <= 0) {
            return 0;
        }
        int randomNumber = random.nextInt(1, totalWeight + 1);

        for (int i = 0; i < weights.size(); i++) {
            count += Math.max(0, weights.get(i));
            if (randomNumber <= count) {
                return i;
            }
        }

        return 0;
    }

    /**
     * performs inverse weighted random selection.
     *
     * @param weights the list containing the weights
     * @param random  random number generator
     * @return an index selected inversely proportional to its weight
     */
    public static int inverseWeightedRandomSelection(List<Integer> weights, Random random) {
        int maxWeight = Collections.max(weights);
        List<Integer> newWeights = new ArrayList<>();
        for (Integer weight : weights) {
            newWeights.add(maxWeight - weight);
        }
        return weightedRandomSelection(newWeights, random);
    }
}
