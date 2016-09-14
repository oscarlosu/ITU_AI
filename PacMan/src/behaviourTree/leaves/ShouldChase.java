package behaviourTree.leaves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.internal.Maze;

public class ShouldChase extends Leaf {
	public ShouldChase(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {
		Game game = (Game)context.get("game");		
		// Get nearest ghost
		ArrayList<GhostDist> edible = SortedEdibleGhosts(game);
		context.put("edibleGhosts", edible);
		// Is nearest edible?
		int chaseDist = (int)context.get("chaseDist");
		return (edible.size() > 0 && edible.get(0).distance < chaseDist ? NodeState.SUCCESS : NodeState.FAILURE);
	}
	
	private ArrayList<GhostDist> SortedEdibleGhosts(Game game) {
		ArrayList<GhostDist> list = new ArrayList<GhostDist>();
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		// Sort edible ghosts
		for(GHOST ghost : GHOST.values()) {
			if(game.isGhostEdible(ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int dist = game.getShortestPathDistance(ghostNode, pacmanNode);
				list.add(new GhostDist(ghost, dist));
			}
		}
		// Sort them in ascending order
		Collections.sort(list);
		return list;
	}

}
