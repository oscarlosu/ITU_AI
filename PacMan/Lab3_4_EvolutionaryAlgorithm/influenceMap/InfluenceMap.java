package influenceMap;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.PriorityQueue;

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

public class InfluenceMap {
	@Expose
	private double maxGameCost;
	@Expose
	private double edibleGhostCost;
	@Expose
	private double edibleGhostInfluenceDecay;
	@Expose
	private double powerPillCost;
	@Expose
	private double powerPillCostGrowth;
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
	
	
	// Defaults
	public static double defaultMaxGameCost = 100;
	public static double defaultEdibleGhostCost = 25;
	public static double defaultEdibleGhostInfluenceDecay = 0.01;
	public static double defaultPowerPillCost = 5;
	public static double defaultPowerPillCostGrowth = 0.1;
	public static double defaultPillCost = 50;
	public static double defaultPillCostDecline = 0.1;
	public static double defaultGhostCost = 10;
	public static double defaultGhostInfluenceDecay = 0.01;
	
	
	public InfluenceMap() {
	}
	
	public InfluenceMap(double maxGameCost, double edibleGhostCost, double edibleGhostInfluenceDecay, double powerPillCost, double powerPillCostGrowth, double pillCost, double pillCostDecline, double ghostCost, double ghostInfluenceDecay) {
		this.maxGameCost = maxGameCost;
		this.edibleGhostCost = edibleGhostCost;
		this.edibleGhostInfluenceDecay = edibleGhostInfluenceDecay;
		this.powerPillCost = powerPillCost;
		this.pillCost = pillCost;
		this.ghostCost = ghostCost;
		this.ghostInfluenceDecay = ghostInfluenceDecay;
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
			boolean edible = game.isGhostEdible(g);
			if(edible) {
				double distToGhost = game.getShortestPathDistance(nodeIndex, ghostIndex);
				double edibleGhostContribution = - influenceCost(edibleGhostCost, edibleGhostInfluenceDecay, distToGhost);
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
			powerPills = - Math.pow(powerPillCost, badGhosts * powerPillCostGrowth);
		}
		// Pills
		double pills = 0;
		int pillIndex = game.getPillIndex(nodeIndex);
		if(pillIndex != -1 && game.isPillStillAvailable(pillIndex)) {
			pills = - Math.pow(pillCost, - badGhosts * pillCostDecline);
		}
		// Intersections
		
		gameCost = badGhosts + goodGhosts + pills + powerPills;

			System.out.println("node " + nodeIndex + " " + maxGameCost * sigmoidFilter(gameCost));
			System.out.println("bad ghosts " + badGhosts + " goodGhosts" + goodGhosts + " pills " + pills + " power pills " + powerPills);

		
		return maxGameCost * sigmoidFilter(gameCost);
	}
	
	private double influenceCost(double sourceCost, double decayRate, double distance) {		
		double cost = sourceCost / Math.pow(Math.E, decayRate * distance);
//		if(cost > 10) {
//			System.out.println("influence cost " + cost + " distance " + distance);
//		}		
		return cost * sourceCost;
	}
	
	public void ChooseTarget(Game game) {
		// Evaluate power pill nodes, junctions, nearest pill and compare with last best
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		double minCost = Double.MAX_VALUE;
		int newBestNode = -1;
		// Power pills
		int powerPills[] = game.getActivePowerPillsIndices();
		for(Integer ppIndex : powerPills) {
			double gameCost = calculateGameCost(game, ppIndex);
			double nodeCost = game.getShortestPathDistance(pacmanNode, ppIndex) + gameCost;
			// Debugging
			double v = gameCost / maxGameCost;
			int red = (int)(255 * v);
			int green = (int)(255 * (1 -v));
			int blue = 0;
			GameView.addPoints(game,new Color(red, green, blue), ppIndex);
//			System.out.println("Node " + ppIndex + " cost " + nodeCost + " distance " + (nodeCost - gameCost) + " game cost " + gameCost);
			if((nodeCost - gameCost) < minCost) {
				minCost = (nodeCost - gameCost);
				newBestNode = ppIndex;
			}
		}
		// Junctions
		int junctions[] = game.getJunctionIndices();
		for(Integer junctionIndex : junctions) {
			double gameCost = calculateGameCost(game, junctionIndex);
			double nodeCost = game.getShortestPathDistance(pacmanNode, junctionIndex) + gameCost;
			// Debugging
			double v = gameCost / maxGameCost;
			int red = (int)(255 * v);
			int green = (int)(255 * (1 -v));
			int blue = 0;
			GameView.addPoints(game,new Color(red, green, blue), junctionIndex);
//			System.out.println("Node " + junctionIndex + " cost " + nodeCost + " distance " + (nodeCost - gameCost) + " game cost " + gameCost);
			if((nodeCost - gameCost) < minCost) {
				minCost = (nodeCost - gameCost);
				newBestNode = junctionIndex;
			}
		}
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
			double gameCost = calculateGameCost(game, nearestPill);
			double nodeCost = game.getShortestPathDistance(pacmanNode, nearestPill) + gameCost;
			// Debugging
			double v = gameCost / maxGameCost;
			int red = (int)(255 * v);
			int green = (int)(255 * (1 -v));
			int blue = 0;
			GameView.addPoints(game,new Color(red, green, blue), nearestPill);
//			System.out.println("Node " + nearestPill + " cost " + nodeCost + " distance " + (nodeCost - gameCost) + " game cost " + gameCost);
			if((nodeCost - gameCost) < minCost) {
				minCost = (nodeCost - gameCost);
				newBestNode = nearestPill;
			}
		}
		// Previous best node
		if(this.bestNode != -1) {
			double gameCost = calculateGameCost(game, this.bestNode);
			double nodeCost = game.getShortestPathDistance(pacmanNode, this.bestNode) + gameCost;
			// Debugging
			double v = gameCost / maxGameCost;
			int red = (int)(255 * v);
			int green = (int)(255 * (1 -v));
			int blue = 0;
			GameView.addPoints(game,new Color(red, green, blue), this.bestNode);
//			System.out.println("Node " + this.bestNode + " cost " + nodeCost + " distance " + (nodeCost - gameCost) + " game cost " + gameCost);
			if((nodeCost - gameCost) < minCost) {
				minCost = (nodeCost - gameCost);
				newBestNode = this.bestNode;
			}
		}
		// Random selection of nodes?
		// TODO
		
		this.bestNode = newBestNode;
		System.out.println("best " + newBestNode);
		System.out.println("pacman " + pacmanNode);
		GameView.addPoints(game,new Color(0, 0, 255), newBestNode);
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
//					double gameCost = calculateGameCost(game, index);
					
					
					// Debugging
//					double t = gameCost / maxGameCost;
//					int red = (int)(255 * t);
//					int green = (int)(255 * (1 -t));
//					int blue = 0;
//					GameView.addPoints(game,new Color(red, green, blue),index);
					
					
					graph[i].adj.add(new E(graph[index], moves[j], 1));	
					// Save best node
//					double pathDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), index);
//					if(pathDist + gameCost < min) {
//						min = pathDist + gameCost;
//						bestNode = index;
//					}
				}				
		}
		//GameView.addPoints(game,new Color(0, 0, 255),bestNode);
//		System.out.println("============================================================================");	
//		System.out.println("BEST NODE: " + bestNode + " cost: " + min);	
//		System.out.println("coords: " + game.getCurrentMaze().graph[bestNode].x + " " + game.getCurrentMaze().graph[bestNode].y);
//		System.out.println("============================================================================");	
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
	                
	                // Debugging
					double v = gameCost / maxGameCost;
					int red = (int)(255 * v);
					int green = (int)(255 * (1 -v));
					int blue = 0;
					GameView.addPoints(game,new Color(red, green, blue), next.node.index);
	
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
    
	public static InfluenceMap LoadFromFile(String filename) {
		// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = IO.loadFile(filename);
		InfluenceMap map = gson.fromJson(json, InfluenceMap.class);
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

	public double getPillCostGrowth() {
		return pillCostDecline;
	}

	public void setPillCostGrowth(double pillCostGrowth) {
		this.pillCostDecline = pillCostGrowth;
	}

	public void setBestNode(int bestNode) {
		this.bestNode = bestNode;
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
