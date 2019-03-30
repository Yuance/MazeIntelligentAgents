package algo;

import java.util.ArrayList;
import java.util.List;

import model.Grid;
import model.State;

import util.Constants;

public class PolicyIteration {

	public static void main(String[] args) {

		Grid iterationGrid = new Grid();
		
		// print the Grid
		iterationGrid.printGrid();
		
		// print the parameters
		System.out.println("Discount: " + Constants.DISCOUNT);
		System.out.println("I: " + Constants.I + " times simplified Bellman"
				+ " update is repeated to produce the next utility estimate");
		
		// Perform policy iteration
		policyIteration(iterationGrid);
		// Output to csv file to plot utility estimates as a function of iteration
        iterationGrid.writeGridResultsToFile(iterationGrid, "grid_policy_history");
        iterationGrid.writePlainResultsToFile(iterationGrid, "plain_policy_history");

        // Display the utilities of all the (non-wall) states
        System.out.println("\nFinal Utilities:");
        iterationGrid.printUtility(iterationGrid.getUtility());

        // Display the optimal policy
        System.out.println("\nFinal Optimal Policy:");
        iterationGrid.printPolicy(iterationGrid.getAction());
	}
	
	/**
	 * Modified policy iteration
	 */
	private static void policyIteration(Grid grid) {

        State[][] states = grid.getStates();
        List<double[][]> utilityResults = new ArrayList<>();
        List<int[][]> actionResults = new ArrayList<>();

		boolean policyUnchanged;
		int numIterations = 0;
		
		do {
            double[][] utilityArr = new double[Constants.NUM_COLS][Constants.NUM_ROWS];;
            Grid.array2DCopy(grid.getUtility(), utilityArr);
            int[][] actionArr = grid.getAction();

            // Append to lists of Action,Utility, a copy of the existing actions & utilities
            double[][] curUtilityArr = new double[Constants.NUM_COLS][Constants.NUM_ROWS];
            int[][] curActionArr = new int[Constants.NUM_COLS][Constants.NUM_ROWS];
            Grid.array2DCopy(utilityArr, curUtilityArr);
            Grid.array2DCopy(actionArr, curActionArr);
            utilityResults.add(curUtilityArr);
            actionResults.add(curActionArr);

            /* Step 1: Policy estimation
            *   Simplified Bellman update
            * */
            int i = 0;
            do {
                // For each state
                for (int row = 0; row < Constants.NUM_ROWS; row++) {
                    for (int col = 0; col < Constants.NUM_COLS; col++) {

                        // Skip walls
                        if (states[col][row].isWall())
                            continue;

                        // Updates the utility based on the action stated in the policy
                        utilityArr[col][row] = grid.calcActionUtility(col, row, actionArr[col][row]);
                    }
                }
                grid.setUtility(utilityArr);
                //until the Iteration times meet
            } while(++i < Constants.I);

			policyUnchanged = true;

			/*
			* Step 2: policy improvement
			* */

			// For each state - Policy improvement
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				for (int col = 0; col < Constants.NUM_COLS; col++) {

					// Not necessary to calculate for walls
					if (states[col][row].isWall())
						continue;

					// Best calculated action based on maximizing utility
                    double actionUtility, fixedActionUtility;
                    double maxUtility = -Double.MAX_VALUE;
                    int maxAction = 0;
                    
                    for (int dir = 0; dir < 4; dir++) {

                        actionUtility = grid.calcActionUtility(col, row, dir);
                        if (actionUtility > maxUtility) {
                            maxUtility = actionUtility;
                            maxAction = dir;
                        }
                    }
					
					// Action and the corresponding utility based on current policy
					fixedActionUtility = grid.calcActionUtility(col, row, actionArr[col][row]);
					if(maxUtility > fixedActionUtility) {
						
					    actionArr[col][row] = maxAction;
						policyUnchanged = false;
					}
				}
			}
			numIterations++;
		} while (!policyUnchanged);
		grid.setUtilityResults(utilityResults);
		grid.setActionResults(actionResults);
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
	}
	
}
