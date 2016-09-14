package behaviourTree.leaves;

import pacman.game.Constants.GHOST;

public class GhostDist implements Comparable<GhostDist> {
	public GHOST ghost;
	public int distance;
	
	
	public GhostDist(GHOST ghost, int dist) {
		this.ghost = ghost;
		this.distance = dist;
	}
	@Override
    public int compareTo(GhostDist other) {
        if (distance < other.distance) {
            return -1;
        } else {
            return 1;
        }
    }
}
