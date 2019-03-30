package model;

import util.Constants;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;

public class Grid {

    // state of a cell includes attributes: reward, isWall
	private State[][] states;
    private double utility[][];
	private int action[][];
	private List<double[][]> utilityResults = null;
	private List<int[][]> actionResults = null;
	public Grid() {

		initializeStates();
        initializeUtility();
        initializeAction();
	}
    /**
     * Getters and Setters
    **/
    public State[][] getStates() {
        return states;
    }
    public double[][] getUtility() {
        return utility;
    }
    public void setUtility(double[][] utility) {
        this.utility = utility;
    }
    public void setStates(State[][] states) {
        this.states = states;
    }
    public int[][] getAction() {
        return action;
    }
    public void setAction(int[][] action) {
        this.action = action;
    }
    public List<double[][]> getUtilityResults() {
        return utilityResults;
    }
    public void setUtilityResults(List<double[][]> utilityResults) {
        this.utilityResults = utilityResults;
    }
    public List<int[][]> getActionResults() {
        return actionResults;
    }
    public void setActionResults(List<int[][]> actionResults) {
        this.actionResults = actionResults;
    }
	/**
	 * Initialize the States, Utility, Action
     * */
    private void initializeUtility() {
        utility = new double[Constants.NUM_COLS][Constants.NUM_ROWS];
        for(int i=0; i < Constants.NUM_COLS; i++)
            for (int j=0; j < Constants.NUM_ROWS; j++)
                utility[i][j] = 0.0;
    };
    private void initializeAction(){
        action = new int[Constants.NUM_COLS][Constants.NUM_ROWS];
        for(int i=0; i < Constants.NUM_COLS; i++)
            for (int j=0; j < Constants.NUM_ROWS; j++)
                //deault to UP
                action[i][j] = 0;
    };
	private void initializeStates() {

        states = new State[Constants.NUM_COLS][Constants.NUM_ROWS];
		// Initialize all grids to reward of -0.040
		for(int row = 0; row < Constants.NUM_ROWS ; row++) {
	        for(int col = 0; col < Constants.NUM_COLS ; col++) {
	        	states[col][row] = new State(Constants.WHITE_REWARD);
	        }
	    }
		// Read the map text file to create a states with correct state, a state only has its reward value and whether it is a wall
        // P = +1, M = -1, E = White(Empty), W = Wall
        try {
            BufferedReader mapReader = new BufferedReader(new FileReader(Constants.MAP_PATH));
            for (int i = 0; i < Constants.NUM_ROWS; i++) {
                String line = mapReader.readLine();
                String[] numberStrings = line.trim().split("");
                for (int j = 0; j < Constants.NUM_COLS; j++) {
                    switch (numberStrings[j]) {
                        case "P":
                            states[j][i] = new State(Constants.GREEN_REWARD);
                            break;
                        case "M":
                            states[j][i] = new State(Constants.BROWN_REWARD);
                            break;
                        case "W":
                            states[j][i] = new State(Constants.WALL_REWARD);
                            states[j][i].setWall(true);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
	}
    /**
     * Calculates the utility for attempting to move to a specified direction
     *
     * @param col			Column in the grid
     * @param row			Row in the grid
     * @param direction     Direction to move in the grid
     * @return				The utility for moving to the specified direction
     */
    public double calcActionUtility(int col, int row, int direction) {

        double actionUtility = 0.000;
        int intended, left=0, right=0;
        switch (direction){
            case Constants.UP:
                left = Constants.LEFT;
                right = Constants.RIGHT;
                break;
            case  Constants.LEFT:
                left = Constants.DOWN;
                right = Constants.UP;
                break;
            case  Constants.RIGHT:
                left = Constants.UP;
                right = Constants.DOWN;
                break;
            case Constants.DOWN:
                left = Constants.RIGHT;
                right = Constants.LEFT;
        }
        intended = direction;

        // Direction intended to move
        actionUtility += Constants.INTENT_PROB * move(col, row, intended);

        // Direction intended to move, but go left right angle instead
        actionUtility += Constants.L_INTENT_PROB * move(col, row, left);

        // Direction intended to move, but go right right angle instead
        actionUtility += Constants.R_INTENT_PROB * move(col, row, right);

        // Final utility
        actionUtility = states[col][row].getReward() + Constants.DISCOUNT * actionUtility;

        return actionUtility;
    }
    private double move(int col, int row, int direction){

        switch (direction) {

            case Constants.UP:
                return (row - 1 >= 0 && !states[col][row - 1].isWall()) ?
                        utility[col][row - 1] : utility[col][row];
            case Constants.RIGHT:
                return (col + 1 < Constants.NUM_COLS && !states[col + 1][row].isWall()) ?
                        utility[col + 1][row] : utility[col][row];
            case Constants.LEFT:
                return (col - 1 >= 0 && !states[col - 1][row].isWall()) ?
                        utility[col - 1][row] : utility[col][row];
            case Constants.DOWN:
                return (row + 1 < Constants.NUM_ROWS && !states[col][row + 1].isWall()) ?
                        utility[col][row + 1] : utility[col][row];
        }
        return 0.0;
    }
    /**
     * Display the Grid in the forms of state
     * W meas wall
     * +1 and -1 and empty indites the rest
     */
    public void printGrid() {

        StringBuilder gridPrint = new StringBuilder();

        gridPrint.append("|");
        for(int col = 0; col < Constants.NUM_COLS ; col++) {
            gridPrint.append("----|");
        }
        gridPrint.append("\n");

        for(int row = 0; row < Constants.NUM_ROWS ; row++) {
            gridPrint.append("|");
            for(int col = 0; col < Constants.NUM_COLS ; col++) {

                State state = states[col][row];
                if(state.isWall()) {
                    // print the wall
                    gridPrint.append(String.format(" %-2s |", "W"));
                }
                else if(state.getReward() != Constants.WHITE_REWARD) {
                    // print the reward cell if it is a non-white cell
                    gridPrint.append(String.format(" %+1.0f |", state.getReward()));
                }
                else {
                    // empty cell
                    gridPrint.append(String.format("%4s|", ""));
                }
            }
            // the last row
            gridPrint.append("\n|");
            for(int col = 0; col < Constants.NUM_COLS ; col++) {
                gridPrint.append("----|");
            }
            gridPrint.append("\n");
        }
        System.out.println(gridPrint.toString());
    }
    /**
     * Print the Utility amount in Grid
     * @param utilityArr    A 2D utility Array for printing
     */
    public void printUtility(double[][] utilityArr){

        StringBuilder gridPrinter = utilityPrinterBuilder(utilityArr);
        System.out.println(gridPrinter.toString());
    }
    /**
     * Print the Policy Action in Grid
     * @param actionArr     A 2D action Array for printing
     */
    public void printPolicy(int[][] actionArr){

        StringBuilder policyPrinter = policyPrinterBuilder(actionArr);
        System.out.println(policyPrinter.toString());
    }
    public void writeGridResultsToFile(Grid grid, String fileName) {

        String utilityWriter = "";
        String actionWriter = "";
        StringBuilder singleUtilityBuilder = null;
        StringBuilder singleActionBuilder = null;
        String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        List<double[][]> utilityList = grid.getUtilityResults();
        List<int[][]> actionList = grid.getActionResults();
        int k = 0;
        while (k < grid.getUtilityResults().size()) {

            singleUtilityBuilder = grid.utilityPrinterBuilder(utilityList.get(k));
            singleActionBuilder = grid.policyPrinterBuilder(actionList.get(k));
            utilityWriter = utilityWriter.concat("\n" + singleUtilityBuilder.toString());
            actionWriter = actionWriter.concat("\n" + singleActionBuilder.toString());
            k++;
        }
        writeToFile(utilityWriter.trim(), "data\\" + fileName + "_utility.txt");
        writeToFile(actionWriter.trim(), "data\\" + fileName + "_action.txt");
    }
    public void writePlainResultsToFile(Grid grid, String fileName) {

        StringBuilder utilityWriter = new StringBuilder();
        StringBuilder actionWriter = new StringBuilder();
        String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        List<double[][]> utilityList = grid.getUtilityResults();
        List<int[][]> actionList = grid.getActionResults();
        int k = 0;
        while (k < grid.getUtilityResults().size()) {

            double[][] utilityArr = utilityList.get(k);
            int[][] actionArr = actionList.get(k);

            for (int i=0;i<Constants.NUM_COLS; i++){
                for (int j=0;j<Constants.NUM_ROWS; j++) {
                    utilityWriter.append(utilityArr[i][j]);
                    if (states[i][j].isWall())
                        actionWriter.append("W");
                    else actionWriter.append(actionPrinter(actionArr[i][j]));
                    utilityWriter.append(",");
                    actionWriter.append(",");
                }
            }

            utilityWriter.append("\n");
            actionWriter.append("\n");
            k++;
        }
        writeToFile(utilityWriter.toString().trim(), "data\\" + fileName + "_utility.csv");
        writeToFile(actionWriter.toString().trim(), "data\\" + fileName + "_action.csv");
    }
    private void writeToFile(String content, String fileName) {
        try
        {
            FileWriter fw = new FileWriter(new File(fileName), false);

            fw.write(content);
            fw.close();

            System.out.println("\nSuccessfully saved results to \"" + fileName + "\".");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    private StringBuilder utilityPrinterBuilder(double[][] utilityArr){

        StringBuilder gridPrinter = new StringBuilder();

        gridPrinter.append("|");
        for(int col = 0; col < Constants.NUM_COLS ; col++) {
            gridPrinter.append("--------|");
        }
        gridPrinter.append("\n");

        String pattern = "00.000";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        // print row by row
        for (int row = 0; row < Constants.NUM_ROWS; row++) {

            // print an empty line for beauty
            gridPrinter.append("|");
            for(int col = 0; col < Constants.NUM_COLS ; col++) {
                gridPrinter.append("--------|".replace('-', ' '));
            }
            gridPrinter.append("\n|");

            // print the Utility number to each cell, with 6 significant digits
            for (int col = 0; col < Constants.NUM_COLS; col++) {

                gridPrinter.append(String.format(" %s |",
                        decimalFormat.format(utilityArr[col][row]).substring(0, 6)));
            }

            gridPrinter.append("\n|");

            // print another empty line for beauty
            for(int col = 0; col < Constants.NUM_COLS ; col++) {
                gridPrinter.append("--------|".replace('-', ' '));
            }

            gridPrinter.append("\n|");

            // print the boundary
            for(int col = 0; col < Constants.NUM_COLS ; col++) {
                gridPrinter.append("--------|");
            }
            gridPrinter.append("\n");
        }
        return gridPrinter;
    }
    private StringBuilder policyPrinterBuilder(int[][] actionArr){
        StringBuilder policyPrinter = new StringBuilder();

        // print the up boundary
        policyPrinter.append("|");
        for(int col = 0; col < Constants.NUM_COLS ; col++) {
            policyPrinter.append("---|");
        }
        policyPrinter.append("\n");

        // print row by row
        for (int row = 0; row < Constants.NUM_ROWS; row++) {

            // print the action string
            policyPrinter.append("|");
            for (int col = 0; col < Constants.NUM_COLS; col++) {
                if (states[col][row].isWall())
                    policyPrinter.append(" W |");
                else {
                    policyPrinter.append(String.format(" %s |", actionPrinter(actionArr[col][row])));
                }
            }
            policyPrinter.append("\n|");

            // print the boundary
            for(int col = 0; col < Constants.NUM_COLS ; col++) {
                policyPrinter.append("---|");
            }
            policyPrinter.append("\n");
        }
        return policyPrinter;
    }
    private String actionPrinter(int action){

        String result = "";
        switch (action){
            case 0:
                result = "^";
                break;
            case 1:
                result = ">";
                break;
            case 2:
                result = "v";
                break;
            case 3:
                result = "<";
        }

        return result;
    }
    /**2DArrayCopyTool*/
    public static void array2DCopy(double[][] src, double[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
    }
    public static void array2DCopy(int[][] src, int[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
    }
}
