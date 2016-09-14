package behaviourTree.leaves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import behaviourTree.Leaf;
import behaviourTree.Node;
import behaviourTree.NodeState;
import pacman.entries.pacman.BehaviourTreeController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.internal.Maze;

public class IsThreatNear extends Leaf {
	public IsThreatNear(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {	
		Game game = (Game)context.get("game");
		int dangerDist = (int)context.get("dangerDist");
		// Look for ghosts closer to pacman than nearDist
		// Find threats
		ArrayList<GhostDist> threats = SortedThreats(game, dangerDist);
		// Save nearest ghost to context
		context.put("threats", threats);
		return (threats.size() > 0 ? NodeState.SUCCESS : NodeState.FAILURE);
	}
	
	private boolean IsGhostActive(Game game, GHOST ghost) {
		return !game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) == 0;
	}
	
	private ArrayList<GhostDist> SortedThreats(Game game, int dangerDist) {
		ArrayList<GhostDist> list = new ArrayList<GhostDist>();
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		// Find ghosts within danger distance
		for(GHOST ghost : GHOST.values()) {
			if(IsGhostActive(game, ghost)) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int dist = game.getShortestPathDistance(ghostNode, pacmanNode);
				if(dist < dangerDist) {
					list.add(new GhostDist(ghost, dist));
				}
			}
		}
		// Sort them in ascending order
		Collections.sort(list);
		return list;
	}
}
