package edu.kit.kastel.model.unit;

/**
 * this class represents the farmerKing of a team.
 * @author ucktt
 */
public class FarmerKing extends Unit {

    /**
     * constructs a farmerKing unit starting faceUp.
     */
    public FarmerKing() {
        super("Farmer", "King", true);
    }

    @Override
    public boolean isFarmerKing() {
        return true;
    }
}
