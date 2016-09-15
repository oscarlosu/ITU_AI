package behaviourTree.leaves;

import java.util.ArrayList;
import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.internal.Maze;

public class IsPowerPillReachable extends Leaf {
	public IsPowerPillReachable(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {
		Game game = (Game)context.get("game");
		// Find nearest power pill
		int powerPills[] = game.getActivePowerPillsIndices();
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int nearestPowerPill = -1; // Default
		int nearestDist = Integer.MAX_VALUE;
		for(int i = 0; i < powerPills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanNode, powerPills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPowerPill = powerPills[i];
			}
		}
		// Fail if no power pills or ghosts nearer than pill		
		ArrayList<GhostDist> threats = (ArrayList<GhostDist>)context.get("threats");
		
		GHOST nearestGhost = threats.get(0).ghost;
		int nearestGhostNode = game.getGhostCurrentNodeIndex(nearestGhost);
		int ghostToPacman = threats.get(0).distance;
		if(powerPills.length == 0 || ghostToPacman < nearestDist) {
			return NodeState.FAILURE;
		}
		// Save nearest power pill
		context.put("nearestPowerPill", nearestPowerPill);
		return NodeState.SUCCESS;
	}

}
