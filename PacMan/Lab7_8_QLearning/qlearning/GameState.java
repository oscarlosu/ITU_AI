package qlearning;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GameState {
	public int distToInky;
	public int distToPinky;
	public int distToBlinky;
	public int distToSue;
	
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
	
	public int distToPill;
	public int distToPowerPill;
	
	public GameState() {
		
	}
	
	public GameState(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();
		// Ghosts
		distToInky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.INKY));
		distToPinky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.PINKY));
		distToBlinky = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		distToSue = game.getShortestPathDistance(pacman, game.getGhostCurrentNodeIndex(GHOST.SUE));
		
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
		distToPill = minDist;
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
		distToPowerPill = minDist;
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
		return null;
	}
	
	public int hashCode() {
		String s = "";
		s += Integer.toString(distToInky);
		s += Integer.toString(distToPinky);
		s += Integer.toString(distToBlinky);
		s += Integer.toString(distToSue);
		
		// Not completely sure, but this order 
		// (having variable length fields grouped) 
		// might avoid some hash collisions
		s += Integer.toString(distToPill);
		s += Integer.toString(distToPowerPill);
		
		s += Integer.toString(dirToInky.ordinal());
		s += Integer.toString(dirToPinky.ordinal());
		s += Integer.toString(dirToBlinky.ordinal());
		s += Integer.toString(dirToSue.ordinal());		
		
		s += Integer.toString(dirToPill.ordinal());
		s += Integer.toString(dirToPowerPill.ordinal());
		
		s += Integer.toString(isEdibleInky ? 1 : 0);
		s += Integer.toString(isEdiblePinky ? 1 : 0);
		s += Integer.toString(isEdibleBlinky ? 1 : 0);
		s += Integer.toString(isEdibleSue ? 1 : 0);	
		
		return s.hashCode();
	}
}
