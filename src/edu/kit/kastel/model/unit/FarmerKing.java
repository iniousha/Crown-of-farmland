package edu.kit.kastel.model.unit;

/**
 * this class represents the farmerKing of a team.
 * @author ucktt
 */
public class FarmerKing extends Unit {

    private static final String FIRST_NAME = "Farmer";
    private static final String LAST_NAME = "King";

    /**
     * constructs a farmerKing unit starting faceUp.
     */
    public FarmerKing() {
        super(FIRST_NAME, LAST_NAME, true);
    }

    @Override
    public boolean isFarmerKing() {
        return true;
    }

    @Override
    public int getAttackPoints() {
        return 0;
    }

    @Override
    public int getDefencePoints() {
        return 0;
    }

    @Override
    public void endBlocking() {
        // FarmerKing cannot block, no action needed
    }

    @Override
    public void startBlocking() {
        // FarmerKing cannot block, no action needed
    }
}
