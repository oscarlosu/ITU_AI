package qlearning;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GameState {
	private final int distThresholds[] = {10, 20, 30, 40, 50};
	private enum DISTANCE {
		CLOSE,
		NEAR,
		MEDIUM,
		FAR,
		AWAY,
		LOST
	}
	public DISTANCE distToInky;
	public DISTANCE distToPinky;
	public DISTANCE distToBlinky;
	public DISTANCE distToSue;
	
	public MOVE dirToInky;
	public MOVE dirToPinky;
	public MOVE dirToBlinky;
	public MOVE dirToSue;
	
	public boolean isEdibleInky;
	public boolean isEdiblePinky;
	public boolean isEdibleBlinky;
	public boolean isEdibleSue;
	
	public MOVE dirToPill;
	public MOVE dirToPowerPill;
	
	public DISTANCE distToPill;
	public DISTANCE distToPowerPill;
	
	public GameState() {
		
	}
	
	public GameState(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();
		// Ghosts
		int numberDistToInky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.INKY));
		distToInky = getDistance(numberDistToInky);
		int numberDistToPinky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.PINKY));
		distToPinky = getDistance(numberDistToPinky);
		int numberDistToBlinky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		distToBlinky = getDistance(numberDistToBlinky);
		int numberDistToSue = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.SUE));
		distToSue = getDistance(numberDistToSue);
		
		dirToInky = game.getNextMoveTowardsTarget(pacman, game.getGhostCurrentNodeIndex(GHOST.INKY), DM.PATH);
		dirToPinky = game.getNextMoveTowardsTarget(pacman, game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.PATH);
		dirToBlinky = game.getNextMoveTowardsTarget(pacman, game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.PATH);
		dirToSue = game.getNextMoveTowardsTarget(pacman, game.getGhostCurrentNodeIndex(GHOST.SUE), DM.PATH);
		
		isEdibleInky = game.isGhostEdible(GHOST.INKY);
		isEdiblePinky = game.isGhostEdible(GHOST.PINKY);
		isEdibleBlinky = game.isGhostEdible(GHOST.BLINKY);
		isEdibleSue = game.isGhostEdible(GHOST.SUE);
		
		// Pill
		int pills[] = game.getActivePillsIndices();
		int minDist = Integer.MAX_VALUE;
		int nearestPill = 0;
		for(int i = 0; i < pills.length; ++i) {
			int dist = game.getShortestPathDistance(pacman, pills[i]);
			if(dist < minDist) {
				minDist = dist;
				nearestPill = pills[i];
			}
		}		
		dirToPill = game.getNextMoveTowardsTarget(pacman, nearestPill, DM.PATH);
		distToPill = getDistance(minDist);
		// Power pill
		int powerPills[] = game.getActivePillsIndices();
		minDist = Integer.MAX_VALUE;
		int nearestPowerPill = 0;
		for(int i = 0; i < pills.length; ++i) {
			int dist = game.getShortestPathDistance(pacman, pills[i]);
			if(dist < minDist) {
				minDist = dist;
				nearestPowerPill = pills[i];
			}
		}		
		dirToPowerPill = game.getNextMoveTowardsTarget(pacman, nearestPowerPill, DM.PATH);
		distToPowerPill = getDistance(minDist);
	}
	
	private DISTANCE getDistance(int numberDist) {
		// Default to furthest
		DISTANCE dist = DISTANCE.values()[DISTANCE.values().length - 1];
		for(int i = 0; i < distThresholds.length; ++i) {
			if(numberDist <= distThresholds[i]) {
				dist = DISTANCE.values()[i];
				break;
			}
		}
		return dist;
	}
	
	public GameState clone() {
		GameState copy = new GameState();
		copy.distToInky = this.distToInky;
		copy.distToPinky = this.distToPinky;
		copy.distToBlinky = this.distToBlinky;
		copy.distToSue = this.distToSue;
		
		copy.dirToInky = this.dirToInky;
		copy.dirToPinky = this.dirToPinky;
		copy.dirToBlinky = this.dirToBlinky;
		copy.dirToSue = this.dirToSue;
		
		copy.isEdibleInky = this.isEdibleInky;
		copy.isEdiblePinky = this.isEdiblePinky;
		copy.isEdibleBlinky = this.isEdibleBlinky;
		copy.isEdibleSue = this.isEdibleSue;
		
		copy.dirToPill = this.dirToPill;
		copy.dirToPowerPill = this.dirToPowerPill;
		
		copy.distToPill = this.distToPill;
		copy.distToPowerPill = this.distToPowerPill;
		return copy;
	}
	
	public int hashCode() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(distToInky.ordinal());
		builder.append(distToPinky.ordinal());
		builder.append(distToBlinky.ordinal());
		builder.append(distToSue.ordinal());
		
		// Not completely sure, but this order 
		// (having variable length fields grouped) 
		// might avoid some hash collisions
		builder.append(distToPill.ordinal());
		builder.append(distToPowerPill.ordinal());
		
		builder.append(dirToInky.ordinal());
		builder.append(dirToPinky.ordinal());
		builder.append(dirToBlinky.ordinal());
		builder.append(dirToSue.ordinal());		
		
		builder.append(dirToPill.ordinal());
		builder.append(dirToPowerPill.ordinal());
		
		
		builder.append(isEdibleInky ? 1 : 0);
		builder.append(isEdiblePinky ? 1 : 0);
		builder.append(isEdibleBlinky ? 1 : 0);
		builder.append(isEdibleSue ? 1 : 0);
		
		return builder.toString().hashCode();
	}
}
