package neuralNetwork.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import neuralNetwork.Connection;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;
import neuralNetwork.dataRecording.DataTuple;
import neuralNetwork.dataRecording.Position;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;
import qlearning.NodeMOVE;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class NNController extends Controller<MOVE>
{	
	public NeuralNetwork nn = null;
	public int currentLevel = -1;
	public HashMap<Integer, Integer> nearestJunctionLookup;
	
	public NNController(String configFile) {
		super();	
		nn = NeuralNetwork.Load(configFile);
		nearestJunctionLookup = new HashMap<Integer, Integer>();
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{	
		MOVE move = game.getPacmanLastMoveMade();
		if(game.isJunction(game.getPacmanCurrentNodeIndex())) {
			move = EvaluateNN(game);
		} else {
			// Clear chached junctions when a new level is reached
			if(currentLevel != game.getCurrentLevel()) {
				nearestJunctionLookup.clear();
				currentLevel = game.getCurrentLevel();
			}
			
			// Ensure that choosen move is valid and rectify if necessary, without reversals.
			// This makes pacman turn corners that are not junctions automatically.
    		MOVE possibleMoves[] = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
    		ArrayList<MOVE> possibleMovesNoReversal = new ArrayList<MOVE>();
    		boolean possible = false;
    		for(MOVE m : possibleMoves) {
    			// No reversals
    			if(m != move.opposite()) {
    				possibleMovesNoReversal.add(m);
    				// is selected move possible?
    				if(m == move) {
    					possible = true;
    					break;
    				}        				
    			}
    		}
    		if(!possible && possibleMovesNoReversal.size() > 0) {
    			move = possibleMovesNoReversal.get(new Random().nextInt(possibleMovesNoReversal.size()));
    		}
    		
    		// Reversal if any non edible ghost closer to next junction than pacman
    		int nextJunction = getNextJunctionOnPath(game, move);
    		int pacmanDistOnPathToJunction = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), nextJunction, move);
    		for(GHOST g: GHOST.values()) {
    			int ghostIndex = game.getGhostCurrentNodeIndex(g);    			
    			if(!game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0) {
    				int ghostDistOnPathToJunction = game.getShortestPathDistance(ghostIndex, nextJunction, game.getGhostLastMoveMade(g));
    				if(ghostDistOnPathToJunction < pacmanDistOnPathToJunction) {
    					move = move.opposite();
    					break;
    				}
    				
    			}
    		}
		}
		return move;
	}
	
	private int getNextJunctionOnPath(Game game, MOVE move) {
		int pacman = game.getPacmanCurrentNodeIndex();
		int key = new NodeMOVE(pacman, move).hashCode();
		// Early exit if cached
		if(nearestJunctionLookup.containsKey(key)) {
			//GameView.addPoints(game, new Color(255, 0, 0), nearestJunctionLookup.get(key));
			return nearestJunctionLookup.get(key);
		}
		
		int junctions[] = game.getJunctionIndices();
		int minDist = Integer.MAX_VALUE;
		int nearestJunction = 0;
		for(Integer j : junctions) {
			int dist = game.getShortestPathDistance(pacman, j, move);
			if(dist < minDist) {
				minDist = dist;
				nearestJunction = j;
			}
		}
		// Cache in junction lookup
		nearestJunctionLookup.put(key, nearestJunction);
		//GameView.addPoints(game, new Color(255, 0, 0), nearestJunction);
		return nearestJunction;
	}

	
	
	private MOVE EvaluateNN(Game game) {
		// INPUT
		ArrayList<Double> inputValues = new ArrayList<Double>();
		int currentPacmanIndex = game.getPacmanCurrentNodeIndex();
		Node graph[] = game.getCurrentMaze().graph;
		// Pacman
		Position pacmanPos = DataTuple.normalizePosition(graph[currentPacmanIndex].x, graph[currentPacmanIndex].y);
		inputValues.add(pacmanPos.x);
		inputValues.add(pacmanPos.y);
//		double upLegal = 0.0;
//		double downLegal = 0.0;
//		double leftLegal = 0.0;
//		double rightLegal = 0.0;
//		MOVE possibleMoves[] = game.getPossibleMoves(currentPacmanIndex);	
//		for(MOVE m: possibleMoves) {
//			if(m == MOVE.UP) {
//				upLegal = 1.0;
//			} else if(m == MOVE.DOWN) {
//				downLegal = 1.0;
//			} else if(m == MOVE.LEFT) {
//				leftLegal = 1.0;
//			} else if(m == MOVE.RIGHT) {
//				rightLegal = 1.0;
//			}
//		}
//		inputValues.add(upLegal);
//		inputValues.add(downLegal);
//		inputValues.add(leftLegal);
//		inputValues.add(rightLegal);
		// Ghosts
		// BLINKY
		int blinkyIndex = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		Position blinkyPos = DataTuple.normalizePosition(graph[blinkyIndex].x, graph[blinkyIndex].y);
		inputValues.add(blinkyPos.x);
		inputValues.add(blinkyPos.y);
//		int dist = game.getShortestPathDistance(currentPacmanIndex, blinkyIndex);
//		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.BLINKY)));
		// PINKY
		int pinkyIndex = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		Position pinkyPos = DataTuple.normalizePosition(graph[pinkyIndex].x, graph[pinkyIndex].y);
		inputValues.add(pinkyPos.x);
		inputValues.add(pinkyPos.y);
//		dist = game.getShortestPathDistance(currentPacmanIndex, pinkyIndex);
//		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.PINKY)));
		// INKY
		int inkyIndex = game.getGhostCurrentNodeIndex(GHOST.INKY);
		Position inkyPos = DataTuple.normalizePosition(graph[inkyIndex].x, graph[inkyIndex].y);
		inputValues.add(inkyPos.x);
		inputValues.add(inkyPos.y);
//		dist = game.getShortestPathDistance(currentPacmanIndex, inkyIndex);
//		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.INKY)));
		// SUE
		int sueIndex = game.getGhostCurrentNodeIndex(GHOST.SUE);
		Position suePos = DataTuple.normalizePosition(graph[sueIndex].x, graph[sueIndex].y);
		inputValues.add(suePos.x);
		inputValues.add(suePos.y);
//		dist = game.getShortestPathDistance(currentPacmanIndex, sueIndex);
//		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.SUE)));
		
		// Pills
		int pillIndex = NearestPillIndex(game, currentPacmanIndex);
		Position pillPos = DataTuple.normalizePosition(graph[pillIndex].x, graph[pillIndex].y);
		inputValues.add(pillPos.x);
		inputValues.add(pillPos.y);
//		inputValues.add(DataTuple.normalizeDistance(game.getShortestPathDistance(currentPacmanIndex, pillIndex), game.getNumberOfNodes()));
		
		int powerPillIndex = NearestPowerPillIndex(game, currentPacmanIndex);
		Position powerPillPos = DataTuple.normalizePosition(graph[powerPillIndex].x, graph[powerPillIndex].y);
		inputValues.add(powerPillPos.x);
		inputValues.add(powerPillPos.y);
//		inputValues.add(DataTuple.normalizeDistance(game.getShortestPathDistance(currentPacmanIndex, powerPillIndex), game.getNumberOfNodes()));

		
		// Evaluate nn
		ArrayList<Double> outputValues = nn.Evaluate(inputValues);
		
		// Debug
		for(int i = 0; i < nn.getInputLayerSize(); ++i) {
			Neuron n = nn.getInputNeuron(i);
			double v = 0;
			for(Connection c : n.getOutputs()) {
				v += Math.abs(c.getWeight());
			}
			System.out.println("Neuron " + i + " aggregate weight " + v);
		}
		
		for(Double o : outputValues) {
			System.out.println(o);
		}
		// Find selected move (should have 4 outputs)
		MOVE move = MOVE.NEUTRAL;
		double max = Double.MIN_VALUE;
		for(int i = 0; i < outputValues.size(); ++i) {
			if(outputValues.get(i) > max) {
				max = outputValues.get(i);
				if(i == 0) {
					move = MOVE.UP;
				} else if(i == 1) {
					move = MOVE.DOWN;
				} else if(i == 2) {
					move = MOVE.LEFT;
				} else if(i == 3) {
					move = MOVE.RIGHT;
				}
			}
		}
		return move;
	}
	
	private int NearestPillIndex(Game game, int pacmanIndex) {
		// Nearest pill
		int pills[] = game.getActivePillsIndices();
		int nearestDist = Integer.MAX_VALUE;
		int nearestPill = -1;
		for(int i = 0; i < pills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanIndex, pills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPill = pills[i];
			}
		}
		return nearestPill;
	}
	
	private int NearestPowerPillIndex(Game game, int pacmanIndex) {
		// Find nearest power pill
		int powerPills[] = game.getActivePowerPillsIndices();
		int nearestPowerPill = -1; // Default
		int nearestDist = Integer.MAX_VALUE;
		for(int i = 0; i < powerPills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanIndex, powerPills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPowerPill = powerPills[i];
			}
		}
		return nearestPowerPill;
	}
}
