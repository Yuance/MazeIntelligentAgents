package model;

/** A state consisting the reward and isWall information for a cell**/
public class State {
	
	private double reward = 0.0;
	private boolean isWall = false;
	State(double reward) {
		this.reward = reward;
	}
	/**
	 * Getters and Settings
	 * */
	public double getReward() {
		return reward;
	}
	public boolean isWall() {
		return isWall;
	}
	void setWall(boolean isWall) {
		this.isWall = isWall;
	}
}
