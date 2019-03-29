package main;

import java.util.ArrayList;
import java.util.List;

import model.ActionUtilPair;
import model.ActionUtilPair.Action;
import model.Grid;
import model.State;

import util.ActionUtilHelper;
import util.Constants;
import util.FileIOHelper;
import util.FuncHelper;

public class PolicyIterationApp {

	public static Grid _gridWorld = null;
	
	public static void main(String[] args) {
		
		_gridWorld = new Grid();
		
		// Displays the Grid World, just for debugging purposes to ensure correctness
		_gridWorld.printGrid();
		
		State[][] _grid = _gridWorld.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + Constants.DISCOUNT);
		System.out.println("k: " + Constants.I + " (i.e. # of times simplified Bellman"
				+ " update is repeated to produce the next utility estimate)");
		
		// Perform policy iteration
		List<ActionUtilPair[][]> lstActionUtilPairs = policyIteration(_grid);
		
		// Output to csv file to plot utility estimates as a function of iteration
		FileIOHelper.writeToFile(lstActionUtilPairs, "policy_iteration_utilities");
		
		// Final item in the list is the optimal policy derived by policy iteration
		final ActionUtilPair[][] optimalPolicy =
				lstActionUtilPairs.get(lstActionUtilPairs.size() - 1);
		
		// Display the utilities of all the (non-wall) states
		ActionUtilHelper.displayUtilities(_grid, optimalPolicy);
		
		// Display the optimal policy
		System.out.println("\nOptimal Policy:");
		ActionUtilHelper.displayPolicy(optimalPolicy);
		
		// Display the utilities of all states
		System.out.println("\nUtilities of all states:");
		ActionUtilHelper.displayUtilitiesGrid(optimalPolicy);
	}
	
	/**
	 * Performs modified policy iteration
	 */
	public static List<ActionUtilPair[][]> policyIteration(final State[][] grid) {
		
		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
		for (int col = 0; col < Constants.NUM_COLS; col++) {
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				
				currUtilArr[col][row] = new ActionUtilPair();
				if (!grid[col][row].isWall()) {
					currUtilArr[col][row].setAction(Action.UP);
				}
			}
		}
		
		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();
		boolean bUnchanged = true;
		int numIterations = 0;
		
		do {
			
			// Append to list of ActionUtilPair a copy of the existing actions & utilities
			ActionUtilPair[][] currUtilArrCopy =
					new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
			FuncHelper.array2DCopy(currUtilArr, currUtilArrCopy);
			lstActionUtilPairs.add(currUtilArrCopy);
			
			// Policy estimation
			ActionUtilPair[][] policyActionUtil = produceUtilEst(currUtilArr, grid);
			
			bUnchanged = true;
			
			// For each state - Policy improvement
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				for (int col = 0; col < Constants.NUM_COLS; col++) {

					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
						continue;

					// Best calculated action based on maximizing utility
					ActionUtilPair bestActionUtil =
							FuncHelper.calcBestUtility(col, row, policyActionUtil, grid);
					
					// Action and the corresponding utlity based on current policy
					Action policyAction = policyActionUtil[col][row].getAction();
					ActionUtilPair pActionUtil = FuncHelper.calcFixedUtility(
							policyAction, col, row, policyActionUtil, grid);
					
					if((bestActionUtil.getUtil() > pActionUtil.getUtil())) {
						
						policyActionUtil[col][row].setAction(bestActionUtil.getAction());
						bUnchanged = false;
					}
				}
			}
			
			FuncHelper.array2DCopy(policyActionUtil, currUtilArr);
			
			numIterations++;
			
		} while (!bUnchanged);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return lstActionUtilPairs;
	}
	
	/**
	 * Simplified Bellman update to produce the next utility estimate
	 * 
	 * @param currUtilArr	Array of the current utility values
	 * @param grid			The Grid World
	 * @return				The next utility estimate of all the states
	 */
	public static ActionUtilPair[][] produceUtilEst(final ActionUtilPair[][] currUtilArr,
			final State[][] grid) {
		
		ActionUtilPair[][] currUtilArrCpy = new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
		for (int col = 0; col < Constants.NUM_COLS; col++) {
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				currUtilArrCpy[col][row] = new ActionUtilPair(
						currUtilArr[col][row].getAction(),
						currUtilArr[col][row].getUtil());
			}
		}
		
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
		for (int col = 0; col < Constants.NUM_COLS; col++) {
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}
		
		int k = 0;
		do {
			
			// For each state
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				for (int col = 0; col < Constants.NUM_COLS; col++) {
	
					// Not necessary to calculate for walls
					if (grid[col][row].isWall())
						continue;
	
					// Updates the utility based on the action stated in the policy
					Action action = currUtilArrCpy[col][row].getAction();
					newUtilArr[col][row] = FuncHelper.calcFixedUtility(action,
							col, row, currUtilArrCpy, grid);
				}
			}
			
			FuncHelper.array2DCopy(newUtilArr, currUtilArrCpy);
			
		} while(++k < Constants.I);
		
		return newUtilArr;
	}
}
