package behaviourTree.leaves;

import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class CollectPowerPill extends Leaf {
	public CollectPowerPill(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {
		System.out.println("collect power pill");
		Game game = (Game)context.get("game");
		// Get next move on path to power pill
		int powerPillNode = (int)context.get("nearestPowerPill");
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		MOVE move = game.getNextMoveTowardsTarget(pacmanNode, powerPillNode, DM.PATH);
		// Update move
		context.put("move", move);
		return NodeState.SUCCESS;
	}

}
