package algo;

import java.util.ArrayList;
import java.util.List;
import model.Grid;
import model.State;
import util.Constants;

public class ValueIteration {

	public static void main(String[] args) {
		
		Grid iterationGrid = new Grid();
		iterationGrid.printGrid();

		// print the parameters
		System.out.println("Discount: " + Constants.DISCOUNT);
		System.out.println("Rmax: " + Constants.R_MAX);
		System.out.println("Epsilon: " + Constants.EPSILON);
		
		// Start value iteration
		valueIteration(iterationGrid);

		// Output to csv file to plot the whole iteration history
        iterationGrid.writeGridResultsToFile(iterationGrid, "grid_value_history");
        iterationGrid.writePlainResultsToFile(iterationGrid, "plain_value_history");
        // Print the result
        System.out.println("Final Utilities:");
        iterationGrid.printUtility(iterationGrid.getUtility());
        System.out.println("Final Optimal Policy:");
        iterationGrid.printPolicy(iterationGrid.getAction());
	}
	
	private static void valueIteration(final Grid grid) {

	    State[][] states = grid.getStates();
		List<double[][]> utilityResults = new ArrayList<>();
		List<int[][]> actionResults = new ArrayList<>();

		double convergenceValue = Constants.EPSILON * ((1.000 - Constants.DISCOUNT) / Constants.DISCOUNT);
        System.out.printf("Convergence value: %.5f %n", convergenceValue);
        double delta = 0.0;
		int numIterations = 0;

		do {
		    //maximum change delta
			delta = 0.0;

            double[][] utilityArr = new double[Constants.NUM_COLS][Constants.NUM_ROWS];;
            int[][] actionArr = new int[Constants.NUM_COLS][Constants.NUM_ROWS];
            Grid.array2DCopy(grid.getUtility(), utilityArr);
            Grid.array2DCopy(grid.getAction(), actionArr);

			// Append to lists of Action,Utility, a copy of the existing actions & utilities
            double[][] curUtilityArr = new double[Constants.NUM_COLS][Constants.NUM_ROWS];
            int[][] curActionArr = new int[Constants.NUM_COLS][Constants.NUM_ROWS];
            Grid.array2DCopy(utilityArr, curUtilityArr);
            Grid.array2DCopy(actionArr, curActionArr);
            utilityResults.add(curUtilityArr);
            actionResults.add(curActionArr);

			// For each state
			for(int col = 0; col < Constants.NUM_COLS; col++) {
		        for(int row = 0; row < Constants.NUM_ROWS; row++) {

		        	// Skip the wall
		        	if(states[col][row].isWall())
		        		continue;

		        	//Start from Up: 0, Right: 1, Down: 2, Left: 3, get the best Utility
                    double actionUtility = 0.0;
                    double maxUtility = -Double.MAX_VALUE;
                    int maxAction = 0;
                    for (int i = 0; i < 4; i++) {

                        actionUtility = grid.calcActionUtility(col, row, i);
                        if (actionUtility > maxUtility) {
                            maxUtility = actionUtility;
                            maxAction = i;
                        }
                    }
                    double currUtility = utilityArr[col][row];
                    double diff = Math.abs(maxUtility - currUtility);
                    // Update maximum delta
                    if (diff > delta){
                        delta = diff;
                    }
                    utilityArr[col][row] = maxUtility;
                    actionArr[col][row] = maxAction;
		        }
			}
            grid.setAction(actionArr);
            grid.setUtility(utilityArr);
			++numIterations;

			// max-norm less than the convergence value, terminate
		} while (delta >= convergenceValue);

		grid.setActionResults(actionResults);
		grid.setUtilityResults(utilityResults);

		System.out.printf("%nNumber of iterations: %d%n", numIterations);
	}
}
