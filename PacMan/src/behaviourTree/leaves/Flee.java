package behaviourTree.leaves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import behaviourTree.Leaf;
import behaviourTree.NodeState;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public class Flee extends Leaf {
	public Flee(HashMap<String, Object> context) {
		this.context = context;
	}
	@Override
	public NodeState Process() {		
		System.out.println("flee: ");
		Game game = (Game)context.get("game");
		// Get next node on path away from ghost
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		ArrayList<GhostDist> threats = (ArrayList<GhostDist>)context.get("threats");
		for(GhostDist gd : threats) {
			System.out.println(gd.ghost + " " + gd.distance);
		}

		// Try to avoid ghost paths to pacman	
		ArrayList<Integer> good = NonObstructedNeighbours(game, threats);
		MOVE move;
		if(false && good.size() > 0) {		
			// Score results and move to best
			int best = 0;
			float bestScore = Float.MIN_VALUE;
			for(int i = 0; i < good.size(); ++i) {
				float score = GetFleeNodeScore(threats, game, good.get(i));
				if(score > bestScore) {
					bestScore = score;
					best = i;
				}
			}
			move = game.getNextMoveTowardsTarget(pacmanNode, good.get(best), DM.PATH);
		} else {
			// Estimate which node takes pacman further from ghosts
			int searchDepth = (int)context.get("searchDepth");

			FleeNode pacman = new FleeNode(pacmanNode, null);
			pacman.score = GetFleeNodeScore(threats, game, pacmanNode);
			FleeNode target = GetBestNeighbour(game, pacman, searchDepth, threats);
			// Go backwards until we reach a node adjacent to pacman
			while(target.previous != null && target.previous.index != pacmanNode) {
				target = target.previous;
			}
			move = game.getNextMoveTowardsTarget(pacmanNode, target.index, DM.PATH);
		}
		System.out.println("Chosen " + move);
		context.put("move", move);
		return NodeState.SUCCESS;
	}
	
	private class FleeNode implements Comparable<FleeNode> {
		public int index;
		public float score;
		public FleeNode previous;
		
		public FleeNode(int index, FleeNode previous) {
			this.index = index;
			this.previous = previous;
		}

		@Override
		public int compareTo(FleeNode other) {
			if(score < other.score) {
				return -1;
			} else {
				return 1;
			}
		}
	}
	
	private ArrayList<Integer> NonObstructedNeighbours(Game game, ArrayList<GhostDist> threats) {
		// Find paths from ghosts to pacman
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		ArrayList<Integer> obstructedNodes = new ArrayList<Integer>();
		for(GhostDist gd : threats) {
			int[] path = game.getShortestPath(game.getGhostCurrentNodeIndex(gd.ghost), pacmanNode);
			for(int index : path) {
				if(game.getShortestPathDistance(pacmanNode, index) >= game.getShortestPathDistance(game.getGhostCurrentNodeIndex(gd.ghost), index)) {
					obstructedNodes.add(index);
				}				
			}
		}
		// Find neighbour not in obstructed nodes list
		ArrayList<Integer> goodNeighbours = new ArrayList<Integer>();
		int neighbours[] = game.getNeighbouringNodes(pacmanNode);
		for(int neighbour : neighbours) {
			boolean good = true;
			for(int badNode : obstructedNodes) {
				if(badNode == neighbour) {
					good = false;
					break;
				}
			}
			if(good) {
				goodNeighbours.add(neighbour);
			}
		}
		return goodNeighbours;
	}
	
	private FleeNode GetBestNeighbour(Game game, FleeNode current, int searchDepth, ArrayList<GhostDist> threats) {
		int[] neighbours = game.getNeighbouringNodes(current.index);
		FleeNode best = current;
		if(searchDepth > 0) {
			for(int neighbourIndex : neighbours) {
				// Build SearchNode, calculating score
				FleeNode neighbour = new FleeNode(neighbourIndex, current);
				neighbour.score = GetFleeNodeScore(threats, game, neighbourIndex);
				// Don't explore if you ran into ghost already
				if(neighbour.score > 0) {
					FleeNode bestChild = GetBestNeighbour(game, neighbour, searchDepth - 1, threats);
					if(bestChild.score > best.score) {
						best = bestChild;
					}
				}
			}
		}
		return best;
		
	}
	
	private float GetFleeNodeScore(ArrayList<GhostDist> threats, Game game, int nodeIndex) {
		float score = 0;
		for(GhostDist gd : threats) {
			float d =  /*(1.0f/ (float)gd.distance) * */game.getShortestPathDistance(game.getGhostCurrentNodeIndex(gd.ghost), nodeIndex);
			if(d == 0) {
				return 0;
			}
			score += d;
		}
		//score /= (float)threats.size();
		return score;
	}

}
