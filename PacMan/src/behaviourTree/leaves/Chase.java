package behaviourTree.leaves;

import java.util.ArrayList;
import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;

public class Chase extends Leaf {
	public Chase(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {
		try {
			Game game = (Game)context.get("game");
			// Get next move in chase path
			int pacmanNode = game.getPacmanCurrentNodeIndex();
			ArrayList<GhostDist> edible = (ArrayList<GhostDist>)context.get("edibleGhosts");
			GHOST nearestGhost = edible.get(0).ghost;
			int ghostNode = game.getGhostCurrentNodeIndex(nearestGhost);
			MOVE move = game.getNextMoveTowardsTarget(pacmanNode, ghostNode, DM.PATH);
			// Update move in context
			context.put("move", move);
			return NodeState.SUCCESS;
		} catch(Exception e) {
			System.out.println("Error in Chase node. Exception message follows:");
			System.out.println(e.getMessage());
			return NodeState.FAILURE;
		}
		
	}

}
