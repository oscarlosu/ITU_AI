package behaviourTree.leaves;

import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CollectNearestPill extends Leaf {
	public CollectNearestPill(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {
		Game game = (Game) context.get("game");
		// Find nearest pill
		int pills[] = game.getActivePillsIndices();
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int nearestDist = Integer.MAX_VALUE;
		int nearestPill = -1;
		for(int i = 0; i < pills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanNode, pills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPill = pills[i];
			}
		}
		// Get move on path to nearest pill
		MOVE move;
		move = game.getNextMoveTowardsTarget(pacmanNode, nearestPill, DM.PATH);
		context.put("move", move);
		return NodeState.SUCCESS;
	}

}
