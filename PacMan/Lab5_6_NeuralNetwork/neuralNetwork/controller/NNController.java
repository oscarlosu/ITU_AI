package neuralNetwork.controller;

import java.util.ArrayList;
import java.util.HashMap;

import dataRecording.DataTuple;
import neuralNetwork.NeuralNetwork;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class NNController extends Controller<MOVE>
{	
	public NeuralNetwork nn = null;
	
	public NNController(String configFile) {
		super();	
		nn = NeuralNetwork.Load(configFile);
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{		
		// Get outcomes of each possible move and ask neural network which one is better
		MOVE possibleMoves[] = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());		
		double bestScore = Double.MIN_VALUE;
		MOVE bestMove = MOVE.NEUTRAL;
		int currentPacmanIndex = game.getPacmanCurrentNodeIndex();
		
		MOVE validMoves[] = game.getPossibleMoves(currentPacmanIndex);
		for(MOVE m: validMoves) {
			ArrayList<Double> inputValues = new ArrayList<Double>();
			// TODO: Fill with values from game + possibleMove			
			int newPacmanIndex = game.getNeighbour(currentPacmanIndex, m);
			// INPUT
			// Dist to ghosts
			int dist = game.getShortestPathDistance(newPacmanIndex, game.getGhostCurrentNodeIndex(GHOST.BLINKY));
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
			dist = game.getShortestPathDistance(newPacmanIndex, game.getGhostCurrentNodeIndex(GHOST.PINKY));
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
			dist = game.getShortestPathDistance(newPacmanIndex, game.getGhostCurrentNodeIndex(GHOST.INKY));
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
			dist = game.getShortestPathDistance(newPacmanIndex, game.getGhostCurrentNodeIndex(GHOST.SUE));
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
			// Edible ghosts
			inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.BLINKY)));
			inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.PINKY)));
			inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.INKY)));
			inputValues.add(DataTuple.normalizeBoolean(game.isGhostEdible(GHOST.SUE)));
			// Pills
			dist = NearestPillDist(game, newPacmanIndex);
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));
			
			dist = NearestPowerPillDist(game, newPacmanIndex);
			inputValues.add(DataTuple.normalizeDistance(dist, game.getNumberOfNodes()));

			
			// Evaluate nn
			ArrayList<Double> outputValues = nn.Evaluate(inputValues);
			// Save best move
			if(outputValues.get(0) > bestScore) {
				bestScore = outputValues.get(0);
				bestMove = m;
			}
		}		
		return bestMove;
	}

	
	private int NearestPillDist(Game game, int pacmanIndex) {
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
		return nearestDist;
	}
	
	private int NearestPowerPillDist(Game game, int pacmanIndex) {
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
		return nearestDist;
	}
}
