package neuralNetwork.controller;

import java.util.ArrayList;
import java.util.HashMap;

import dataRecording.DataTuple;
import dataRecording.Position;
import neuralNetwork.NeuralNetwork;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class NNController extends Controller<MOVE>
{	
	public NeuralNetwork nn = null;
	public double threshold = 0.5;
	public NNController(String configFile, double threshold) {
		super();	
		nn = NeuralNetwork.Load(configFile);
		this.threshold = threshold;
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{	
		// INPUT
		ArrayList<Double> inputValues = new ArrayList<Double>();
		int currentPacmanIndex = game.getPacmanCurrentNodeIndex();
		Node graph[] = game.getCurrentMaze().graph;
		// Pacman
		Position pacmanPos = DataTuple.normalizePosition(graph[currentPacmanIndex].x, graph[currentPacmanIndex].y);
		inputValues.add(pacmanPos.x);
		inputValues.add(pacmanPos.y);
		double upLegal = 0.0;
		double downLegal = 0.0;
		double leftLegal = 0.0;
		double rightLegal = 0.0;
		MOVE possibleMoves[] = game.getPossibleMoves(currentPacmanIndex);	
		for(MOVE m: possibleMoves) {
			if(m == MOVE.UP) {
				upLegal = 1.0;
			} else if(m == MOVE.DOWN) {
				downLegal = 1.0;
			} else if(m == MOVE.LEFT) {
				leftLegal = 1.0;
			} else if(m == MOVE.RIGHT) {
				rightLegal = 1.0;
			}
		}
		inputValues.add(upLegal);
		inputValues.add(downLegal);
		inputValues.add(leftLegal);
		inputValues.add(rightLegal);
		// Ghosts
		// BLINKY
		int blinkyIndex = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		Position blinkyPos = DataTuple.normalizePosition(graph[blinkyIndex].x, graph[blinkyIndex].y);
		inputValues.add(blinkyPos.x);
		inputValues.add(blinkyPos.y);
		int dist = game.getShortestPathDistance(currentPacmanIndex, blinkyIndex);
		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.BLINKY)));
		// PINKY
		int pinkyIndex = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		Position pinkyPos = DataTuple.normalizePosition(graph[pinkyIndex].x, graph[pinkyIndex].y);
		inputValues.add(pinkyPos.x);
		inputValues.add(pinkyPos.y);
		dist = game.getShortestPathDistance(currentPacmanIndex, pinkyIndex);
		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.PINKY)));
		// INKY
		int inkyIndex = game.getGhostCurrentNodeIndex(GHOST.INKY);
		Position inkyPos = DataTuple.normalizePosition(graph[inkyIndex].x, graph[inkyIndex].y);
		inputValues.add(inkyPos.x);
		inputValues.add(inkyPos.y);
		dist = game.getShortestPathDistance(currentPacmanIndex, inkyIndex);
		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.INKY)));
		// SUE
		int sueIndex = game.getGhostCurrentNodeIndex(GHOST.SUE);
		Position suePos = DataTuple.normalizePosition(graph[sueIndex].x, graph[sueIndex].y);
		inputValues.add(suePos.x);
		inputValues.add(suePos.y);
		dist = game.getShortestPathDistance(currentPacmanIndex, sueIndex);
		inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
		inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.SUE)));
		
		// Pills
		int pillIndex = NearestPillIndex(game, currentPacmanIndex);
		Position pillPos = DataTuple.normalizePosition(graph[pillIndex].x, graph[pillIndex].y);
		inputValues.add(pillPos.x);
		inputValues.add(pillPos.y);
		inputValues.add(DataTuple.normalizeDistance(game.getShortestPathDistance(currentPacmanIndex, pillIndex), game.getNumberOfNodes()));
		
		int powerPillIndex = NearestPowerPillIndex(game, currentPacmanIndex);
		Position powerPillPos = DataTuple.normalizePosition(graph[powerPillIndex].x, graph[powerPillIndex].y);
		inputValues.add(powerPillPos.x);
		inputValues.add(powerPillPos.y);
		inputValues.add(DataTuple.normalizeDistance(game.getShortestPathDistance(currentPacmanIndex, powerPillIndex), game.getNumberOfNodes()));

		
		// Evaluate nn
		ArrayList<Double> outputValues = nn.Evaluate(inputValues);
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
		// Last move if below threshold
		if(max < threshold) {
			move = MOVE.NEUTRAL;
		}
		System.out.println(move + " " + max);
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
