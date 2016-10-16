package oslo.tacticalAStar;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import neuralNetwork.NeuralNetwork;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import pacman.game.util.IO;

public class TacticalAStar {
	@Expose
	private double maxGameCost;
	@Expose
	private double edibleGhostCost;
	@Expose
	private double edibleGhostInfluenceDecay;
	@Expose
	private double edibleGhostDistanceDecline;
	@Expose
	private double powerPillCost;
	@Expose
	private double powerPillCostGrowth;
	@Expose
	private double ppPenalty;
	@Expose
	private double ppReward;
	@Expose
	private double pillCost;
	@Expose
	private double pillCostDecline;
	@Expose
	private double ghostCost;
	@Expose
	private double ghostInfluenceDecay;
	
	
	
	
	
	private N[] graph;
	
	private int bestNode = -1;
	private double bestCost = Double.MAX_VALUE;
	
	
	// Defaults
	public static double defaultMaxGameCost = 100;
	public static double defaultEdibleGhostCost = 25;
	public static double defaultEdibleGhostInfluenceDecay = 0.01;
	public static double defaultEdibleGhostDistanceDecline = 0.01;
	public static double defaultPowerPillCost = 5;
	public static double defaultPpPenalty = 100;
	public static double defaultPpReward = 100;
	public static double defaultPowerPillCostGrowth = 0.1;
	public static double defaultPillCost = 50;
	public static double defaultPillCostDecline = 0.1;
	public static double defaultGhostCost = 10;
	public static double defaultGhostInfluenceDecay = 0.01;
	public static int targetChoiceAreasDepth = 5;
	
	
	public TacticalAStar() {
	}
	
	public TacticalAStar(double maxGameCost, double edibleGhostCost, double edibleGhostInfluenceDecay, double edibleGhostDistanceDecline, double powerPillCost, double ppPenalty, double ppReward, double powerPillCostGrowth, double pillCost, double pillCostDecline, double ghostCost, double ghostInfluenceDecay) {
		this.maxGameCost = maxGameCost;
		this.edibleGhostCost = edibleGhostCost;
		this.edibleGhostInfluenceDecay = edibleGhostInfluenceDecay;
		this.powerPillCost = powerPillCost;
		this.ppPenalty = ppPenalty;
		this.ppReward = ppReward;
		this.powerPillCostGrowth = powerPillCostGrowth;
		this.pillCost = pillCost;
		this.pillCostDecline = pillCostDecline;
		this.ghostCost = ghostCost;
		this.ghostInfluenceDecay = ghostInfluenceDecay;
		this.edibleGhostDistanceDecline = edibleGhostDistanceDecline;		
	}
	
	public double sigmoidFilter(double x) {
		return (1.0 / (1.0 + Math.pow(Math.E, - x)));
	}
	
	public double calculateGameCost(Game game, int nodeIndex) {
		double gameCost = 0;
		// Ghost cost
		double badGhosts = 0;
		double goodGhosts = 0;
		for(GHOST g : GHOST.values()) {
			int ghostIndex = game.getGhostCurrentNodeIndex(g);
			
//			if(ghostIndex == nodeIndex) {
//				System.out.println(g + " at " + ghostIndex);
//			}
			boolean edible = game.isGhostEdible(g);
			if(edible) {
				double distToGhost = game.getShortestPathDistance(nodeIndex, ghostIndex);
				double distToPacman = game.getShortestPathDistance(nodeIndex, game.getPacmanCurrentNodeIndex());
				//double edibleGhostContribution = - influenceCost(edibleGhostCost, edibleGhostInfluenceDecay, distToGhost) / (distToPacman * edibleGhostDistanceDecline);
				double edibleGhostContribution = - influenceCost(influenceCost(edibleGhostCost, edibleGhostInfluenceDecay, distToGhost), edibleGhostDistanceDecline, distToPacman);
				goodGhosts += edibleGhostContribution;
			} else if (game.getGhostLairTime(g) <= 0) {
				double distToGhost = game.getShortestPathDistance(nodeIndex, ghostIndex);
				double ghostContribution = influenceCost(ghostCost, ghostInfluenceDecay, distToGhost);
				badGhosts += ghostContribution;
			}
		}
		// Power pills
		double powerPills = 0;
		int powerPillIndex = game.getPowerPillIndex(nodeIndex);
		if(powerPillIndex != -1 && game.isPowerPillStillAvailable(powerPillIndex)) {
			//powerPills = - Math.pow(powerPillCost, badGhosts * powerPillCostGrowth);
			if(badGhosts < ppPenalty) {
				powerPills = ppPenalty - badGhosts;
			} else {
				powerPills = - ppReward * badGhosts;
				System.out.println("reward");
			}
			
		}
		// Pills
		double pills = 0;
		int pillIndex = game.getPillIndex(nodeIndex);
		if(pillIndex != -1 && game.isPillStillAvailable(pillIndex)) {
			//pills = - Math.pow(pillCost, - badGhosts * pillCostDecline);
			//pills = - pillCost / Math.pow(Math.E, badGhosts * pillCostDecline);
			pills = - pillCost;
		}
		// Intersections
		
		gameCost = badGhosts + goodGhosts + pills + powerPills;
		gameCost = maxGameCost * sigmoidFilter(gameCost);
		// Debugging
		double v = gameCost / maxGameCost;
		if(v < 0 || v > 1) {
			System.out.println("node " + nodeIndex + "value" + v);
		}
		v = Math.max(Math.min(v, 1), 0);
		int red = (int)(255 * v);
		int green = (int)(255 * (1 -v));
		int blue = 0;
		
		GameView.addPoints(game,new Color(red, green, blue), nodeIndex);

		
		return gameCost;
	}
	
	private double influenceCost(double sourceCost, double decayRate, double distance) {		
		double cost = sourceCost / Math.pow(Math.E, decayRate * distance);	
		return cost;
	}
	
	public void ChooseTarget(Game game) {		
		// Evaluate power pill nodes, junctions, nearest pill and compare with last best
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		
		// Update previous best node cost
		if(this.bestNode != -1) {
			double nodeCost = game.getShortestPathDistance(pacmanNode, this.bestNode) + calculateGameCost(game, this.bestNode);
			bestCost = nodeCost;
		}		
		// Power pills
		int powerPills[] = game.getActivePowerPillsIndices();
		EvaluateNodeCollection(powerPills, game);
		// Junctions
		int junctions[] = game.getJunctionIndices();
		EvaluateNodeCollection(junctions, game);
		// Nearest pill
		int pills[] = game.getActivePillsIndices();
		int nearestDist = Integer.MAX_VALUE;
		int nearestPill = -1;
		for(int i = 0; i < pills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanNode, pills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPill = pills[i];
			}
		}
		if(nearestPill != -1) {
			double nodeCost = game.getShortestPathDistance(pacmanNode, nearestPill) + calculateGameCost(game, nearestPill);
			if(nodeCost < bestCost) {
				bestCost = nodeCost;
				bestNode = nearestPill;
			}
		}
		// Area around pacman
		Set<Integer> visited = new HashSet<Integer>();		
		counter = 0;
		EvaluateNeighbourhood(pacmanNode, visited, game, targetChoiceAreasDepth);		
		// Area around ghosts
		for(GHOST g : GHOST.values()) {
			int nodeIndex = game.getGhostCurrentNodeIndex(g);
			counter = 0;
			if(nodeIndex >= 0) {
				EvaluateNeighbourhood(nodeIndex, visited, game, targetChoiceAreasDepth);		
			}			
		}		

		// Debugging
		GameView.addPoints(game,new Color(0, 0, 255), bestNode);
	}
	
	private void EvaluateNodeCollection(int[] collection, Game game) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		for(Integer node : collection) {
			double nodeCost = game.getShortestPathDistance(pacmanNode, node) + calculateGameCost(game, node);
			if(nodeCost < bestCost) {
				bestCost = nodeCost;
				bestNode = node;
			}
		}
	}
	
	int counter;
	private void EvaluateNeighbourhood(int node, Set<Integer> visited, Game game, int depth) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		for(int neighbour : game.getNeighbouringNodes(node)) {
			// Only evaluate nodes that haven't been checked before
			if(!visited.contains(neighbour)) {
				double gameCost = calculateGameCost(game, neighbour);
				double nodeCost = game.getShortestPathDistance(pacmanNode, neighbour) + gameCost;
				if(nodeCost < bestCost) {
					bestCost = nodeCost;
					bestNode = neighbour;
				}
				visited.add(neighbour);
				// Recursion
				if(depth > 0) {
					EvaluateNeighbourhood(neighbour, visited, game, depth - 1);
				}	
				counter++;
			}	
		}
//		if(depth == targetChoiceAreasDepth) {
//			System.out.println(counter);
//		}
	}
	
	public void createGraph(Game game)
	{
		Node nodes[] = game.getCurrentMaze().graph;
		graph=new N[nodes.length];
		
		//create graph
		for(int i=0;i<nodes.length;i++)
			graph[i]=new N(nodes[i].nodeIndex);
		
		//add neighbours
		double min = Double.MAX_VALUE;
		for(int i=0;i<nodes.length;i++)
		{	
			EnumMap<MOVE,Integer> neighbours=nodes[i].neighbourhood;
			MOVE[] moves=MOVE.values();
			
			for(int j=0;j<moves.length;j++)
				if(neighbours.containsKey(moves[j])) {
					int index = neighbours.get(moves[j]);				
					graph[i].adj.add(new E(graph[index], moves[j], 1));
				}
		}				
	}
	
	public int[] computePathsAStar(int s, int t, Game game)
    {	
		N start=graph[s];
		N target=graph[t];
		
        PriorityQueue<N> open = new PriorityQueue<N>();
        ArrayList<N> closed = new ArrayList<N>();

        start.g = 0;
        start.h = game.getShortestPathDistance(start.index, target.index);

        
        open.add(start);

        while(!open.isEmpty())
        {
        	// Take node from open queue and add to closed group
            N currentNode = open.poll();
            closed.add(currentNode);
            // Stop when target is reached
            if (currentNode.isEqual(target))
                break;
            // Expand current node
            for(E next : currentNode.adj)
            {
            	// Don't backtrack
            	if(currentNode.reached == null || next.move!=currentNode.reached.opposite())
            	{
            		//double nodeCost = game.getShortestPathDistance(pacmanNode, nearestPill) + calculateGameCost(game, nearestPill);
            		double gameCost = calculateGameCost(game, next.node.index);
	                double currentDistance = next.cost + gameCost;					
	
	                if (!open.contains(next.node) && !closed.contains(next.node))
	                {
	                    next.node.g = currentDistance + currentNode.g;
	                    next.node.h = game.getShortestPathDistance(next.node.index, target.index);
	                    next.node.parent = currentNode;
	                    
	                    next.node.reached=next.move;
	
	                    open.add(next.node);
	                }
	                else if (currentDistance + currentNode.g < next.node.g)
	                {
	                    next.node.g = currentDistance + currentNode.g;
	                    next.node.parent = currentNode;
	                    
	                    next.node.reached=next.move;
	
	                    if (open.contains(next.node))
	                        open.remove(next.node);
	
	                    if (closed.contains(next.node))
	                        closed.remove(next.node);
	
	                    open.add(next.node);
	                }
	                
	             
            	}
            }
        }

        return extractPath(target);
    }


    private int[] extractPath(N target)
    {
    	ArrayList<Integer> route = new ArrayList<Integer>();
        N current = target;
        route.add(current.index);

        while (current.parent != null)
        {
            route.add(current.parent.index);
            current = current.parent;
            //System.out.println("Node " + current.index + " cost " + current.g);	
        }
        
        Collections.reverse(route);

        int[] routeArray=new int[route.size()];
        
        for(int i=0;i<routeArray.length;i++)
        	routeArray[i]=route.get(i);
        
        return routeArray;
    }
    
    public void resetGraph()
    {
    	for(N node : graph)
    	{
    		node.g=0;
    		node.h=0;
    		node.parent=null;
    		node.reached=null;
    	}
    }
    
	public static TacticalAStar LoadFromFile(String filename) {
		// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = IO.loadFile(filename);
		TacticalAStar map = gson.fromJson(json, TacticalAStar.class);
		return map;
	}
	
	public void SaveToFile(String filename) {
		// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = gson.toJson(this);
		// Save to file in myData/
		IO.saveFile(filename, json, false);
	}
	
	///////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	///////////////////////////////////////////////////////////
	
	
	
	public int getBestNode() {
		return bestNode;
	}

	public double getMaxGameCost() {
		return maxGameCost;
	}

	public void setMaxGameCost(double maxGameCost) {
		this.maxGameCost = maxGameCost;
	}

	public double getEdibleGhostCost() {
		return edibleGhostCost;
	}

	public void setEdibleGhostCost(double edibleGhostCost) {
		this.edibleGhostCost = edibleGhostCost;
	}

	public double getEdibleGhostInfluenceDecay() {
		return edibleGhostInfluenceDecay;
	}

	public void setEdibleGhostInfluenceDecay(double edibleGhostInfluenceDecay) {
		this.edibleGhostInfluenceDecay = edibleGhostInfluenceDecay;
	}

	public double getPowerPillCost() {
		return powerPillCost;
	}

	public void setPowerPillCost(double powerPillCost) {
		this.powerPillCost = powerPillCost;
	}

	public double getPillCost() {
		return pillCost;
	}

	public void setPillCost(double pillCost) {
		this.pillCost = pillCost;
	}

	public double getGhostCost() {
		return ghostCost;
	}

	public void setGhostCost(double ghostCost) {
		this.ghostCost = ghostCost;
	}

	public double getGhostInfluenceDecay() {
		return ghostInfluenceDecay;
	}

	public void setGhostInfluenceDecay(double ghostInfluenceDecay) {
		this.ghostInfluenceDecay = ghostInfluenceDecay;
	}

	public double getPowerPillCostGrowth() {
		return powerPillCostGrowth;
	}

	public void setPowerPillCostGrowth(double powerPillCostGrowth) {
		this.powerPillCostGrowth = powerPillCostGrowth;
	}

	public double getPillCostDecline() {
		return pillCostDecline;
	}

	public void setPillCostDecline(double pillCostGrowth) {
		this.pillCostDecline = pillCostGrowth;
	}

	public void setBestNode(int bestNode) {
		this.bestNode = bestNode;
	}

	public double getEdibleGhostDistanceDecline() {
		return edibleGhostDistanceDecline;
	}

	public void setEdibleGhostDistanceDecline(double edibleGhostDistanceDecline) {
		this.edibleGhostDistanceDecline = edibleGhostDistanceDecline;
	}

	public double getPpPenalty() {
		return ppPenalty;
	}

	public void setPpPenalty(double ppPenalty) {
		this.ppPenalty = ppPenalty;
	}

	public double getPpReward() {
		return ppReward;
	}

	public void setPpReward(double ppReward) {
		this.ppReward = ppReward;
	}
	
	
	
}

class N implements Comparable<N>
{
    public N parent;
    public double g, h;
    public boolean visited = false;
    public ArrayList<E> adj;
    public int index;
    public MOVE reached=null;

    public N(int index)
    {
        adj = new ArrayList<E>();
        this.index=index;
    }

    public N(double g, double h)
    {
        this.g = g;
        this.h = h;
    }

    public boolean isEqual(N another)
    {
        return index == another.index;
    }

    public String toString()
    {
        return ""+index;
    }

	public int compareTo(N another)
	{
      if ((g + h) < (another.g + another.h))
    	  return -1;
      else  if ((g + h) > (another.g + another.h))
    	  return 1;
		
		return 0;
	}
}

class E
{
	public N node;
	public MOVE move;
	public double cost;
	
	public E(N node,MOVE move,double cost)
	{
		this.node=node;
		this.move=move;
		this.cost=cost;
	}
}
