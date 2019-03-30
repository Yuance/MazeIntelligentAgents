package util;

public class Constants {
	// Size of the Grid -> modify here to enable maze with different size
	public static final int NUM_COLS = 6;
	public static final int NUM_ROWS = 6;
	// direction
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	// Transition model
	public static final double INTENT_PROB = 0.8;
	public static final double L_INTENT_PROB = 0.1;
	public static final double R_INTENT_PROB = 0.1;
	// Reward functions
	public static final double WHITE_REWARD = -0.040;
	public static final double GREEN_REWARD = +1.000;
	public static final double BROWN_REWARD = -1.000;
	public static final double WALL_REWARD = 0.000;
	// Grid Information
    public static final String MAP_PATH = "src/map/extendedgrid.txt";
    // Rmax
    public static final double R_MAX = 1.0;
    // Constant C
    private static final double C = 0.1;
    // error amount, epsilon
    public static final double EPSILON = C * R_MAX;
    // Discount factor
    public static final double DISCOUNT =  0.99;
	// number of times simplified Bellman update is executed
	public static final int I = 10;
}
