package classes;

import util.Constant;

public class GridWorld {

	private State[][] _grid = null;
	
	public GridWorld() {
		
		_grid = new State[Constant.NUM_COLS][Constant.NUM_ROWS];
		buildGrid();
		
		duplicateGrid();
	}
	
	/** Returns the actual grid, i.e. a 2-D states array **/
	public State[][] getGrid() {
		return _grid;
	}
	
	/**
	 * Initialize the Grid World
	 */
	public void buildGrid() {
		
		// All grids (even walls) starts with reward of -0.040
		for(int row = 0; row < Constant.NUM_ROWS ; row++) {
	        for(int col = 0; col < Constant.NUM_COLS ; col++) {
	        	
	        	_grid[col][row] = new State(Constant.WHITE_REWARD);
	        }
	    }
		
		// Set all the green squares (+1.000)
		String[] greenSquaresArr = Constant.GREEN_SQUARES.split(Constant.GRID_DELIM);
		for(String greenSquare : greenSquaresArr) {
			
			greenSquare = greenSquare.trim();
			String [] gridInfo = greenSquare.split(Constant.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);
			
			_grid[gridCol][gridRow].setReward(Constant.GREEN_REWARD);
		}
		
		// Set all the brown squares (-1.000)
		String[] brownSquaresArr = Constant.BROWN_SQUARES.split(Constant.GRID_DELIM);
		for (String brownSquare : brownSquaresArr) {

			brownSquare = brownSquare.trim();
			String[] gridInfo = brownSquare.split(Constant.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			_grid[gridCol][gridRow].setReward(Constant.BROWN_REWARD);
		}
		
		// Set all the walls (0.000 and unreachable, i.e. stays in the same place as before)
		String[] wallSquaresArr = Constant.WALLS_SQUARES.split(Constant.GRID_DELIM);
		for (String wallSquare : wallSquaresArr) {

			wallSquare = wallSquare.trim();
			String[] gridInfo = wallSquare.split(Constant.COL_ROW_DELIM);
			int gridCol = Integer.parseInt(gridInfo[0]);
			int gridRow = Integer.parseInt(gridInfo[1]);

			_grid[gridCol][gridRow].setReward(Constant.WALL_REWARD);
			_grid[gridCol][gridRow].setAsWall(true);
		}
	}
	
	/**
	 * Used to 'expand' the maze
	 */
	public void duplicateGrid() {
		
		for(int row = 0; row < Constant.NUM_ROWS ; row++) {
	        for(int col = 0; col < Constant.NUM_COLS ; col++) {
	        	
				if (row >= 6 || col >= 6) {
					int trueRow = row % 6;
					int trueCol = col % 6;

					_grid[col][row].setReward(_grid[trueCol][trueRow].getReward());
					_grid[col][row].setAsWall(_grid[trueCol][trueRow].isWall());
				}
	        }
	    }
	}
	
	/**
	 * Display the Grid World
	 */
	public void displayGrid() {
		
		StringBuilder sb = new StringBuilder();
		
        sb.append("|");
        for(int col = 0; col < Constant.NUM_COLS ; col++) {
        	sb.append("----|");
        }
        sb.append("\n");
        
		for(int row = 0; row < Constant.NUM_ROWS ; row++) {
	        sb.append("|");
	        for(int col = 0; col < Constant.NUM_COLS ; col++) {
	        	
	        	State state = _grid[col][row];
	        	if(state.isWall()) {
	                sb.append(String.format(" %-2s |", "WW"));
	        	}
	        	else if(state.getReward() != Constant.WHITE_REWARD) {
	        		sb.append(String.format(" %+1.0f |", state.getReward()));
	        	}
	        	else {
	        		sb.append(String.format("%4s|", ""));
	        	}
	        }
	        
	        sb.append("\n|");
	        for(int col = 0; col < Constant.NUM_COLS ; col++) {
	        	sb.append("----|");
	        }
	        sb.append("\n");
	    }
		
		System.out.println(sb.toString());
	}
}
