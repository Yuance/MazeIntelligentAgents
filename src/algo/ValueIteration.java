package main;

import java.util.ArrayList;
import java.util.List;

import model.ActionUtilPair;
import model.Grid;
import model.State;
import util.ActionUtilHelper;
import util.Constants;
import util.FileIOHelper;
import util.FuncHelper;

public class ValueIterationApp {

	public static Grid _gridWorld = null;
	
	public static void main(String[] args) {
		
		_gridWorld = new Grid();
		
		// Displays the Grid World, just for debugging purposes to ensure correctness
		_gridWorld.printGrid();
		
		State[][] _grid = _gridWorld.getGrid();
		
		// Displays the settings currently used
		System.out.println("Discount: " + Constants.DISCOUNT);
		System.out.println("Rmax: " + Constants.R_MAX);
		System.out.println("Constants 'c': " + Constants.C);
		System.out.println("Epsilon (c * Rmax): " + Constants.EPSILON);
		
		// Perform value iteration
		List<ActionUtilPair[][]> lstActionUtilPairs = valueIteration(_grid);
		
		// Output to csv file to plot utility estimates as a function of iteration
		FileIOHelper.writeToFile(lstActionUtilPairs, "value_iteration_utilities");
		
		// Final item in the list is the optimal policy derived by value iteration
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
	
	public static List<ActionUtilPair[][]> valueIteration(final State[][] grid) {
		
		ActionUtilPair[][] currUtilArr = new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
		ActionUtilPair[][] newUtilArr = new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
		for (int col = 0; col < Constants.NUM_COLS; col++) {
			for (int row = 0; row < Constants.NUM_ROWS; row++) {
				newUtilArr[col][row] = new ActionUtilPair();
			}
		}
		
		List<ActionUtilPair[][]> lstActionUtilPairs = new ArrayList<>();
		
		// For using span semi-norm instead of max norm
		double deltaMax = Double.MIN_VALUE;
		double deltaMin = Double.MAX_VALUE;
		
		double convergenceCriteria =
				Constants.EPSILON * ((1.000 - Constants.DISCOUNT) / Constants.DISCOUNT);
		System.out.printf("Convergence criteria: %.5f "
				+ "(i.e. the span semi-norm must be < this value)%n", convergenceCriteria);
		int numIterations = 0;
		
		do {
			
			FuncHelper.array2DCopy(newUtilArr, currUtilArr);
			deltaMax = Double.MIN_VALUE;
			deltaMin = Double.MAX_VALUE;
			
			// Append to list of ActionUtilPair a copy of the existing actions & utilities
			ActionUtilPair[][] currUtilArrCopy =
					new ActionUtilPair[Constants.NUM_COLS][Constants.NUM_ROWS];
			FuncHelper.array2DCopy(currUtilArr, currUtilArrCopy);
			lstActionUtilPairs.add(currUtilArrCopy);
			
			// For each state
			for(int row = 0; row < Constants.NUM_ROWS ; row++) {
		        for(int col = 0; col < Constants.NUM_COLS ; col++) {
		        	
		        	// Not necessary to calculate for walls
		        	if(grid[col][row].isWall())
		        		continue;
		        	
		        	newUtilArr[col][row] =
		        			FuncHelper.calcBestUtility(col, row, currUtilArr, grid);
		        	
		        	double newUtil = newUtilArr[col][row].getUtil();
		        	double currUtil = currUtilArr[col][row].getUtil();
		        	double sDelta = Math.abs(newUtil - currUtil);
		        	
		        	// Update maximum delta & minimum delta, if necessary
		        	deltaMax = Math.max(deltaMax, sDelta);
		        	deltaMin = Math.min(deltaMin, sDelta);
		        }
			}
			
			++numIterations;
			
			// Terminating condition: Span semi-norm less than the convergence criteria
		} while ((deltaMax - deltaMin) >= convergenceCriteria);
		
		System.out.printf("%nNumber of iterations: %d%n", numIterations);
		return lstActionUtilPairs;
	}
}
