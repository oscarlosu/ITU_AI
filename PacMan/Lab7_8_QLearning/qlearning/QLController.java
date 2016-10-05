package qlearning;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Node;

public class QLController extends Controller<MOVE> {
	public QTable qTable;
	
	public QLController(String filename) {
		qTable = QTable.Deserialize(filename);
	}
	
	public QLController() {
		qTable = new QTable(MOVE.values().length);
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {	
		MOVE move = game.getPacmanLastMoveMade();
		if(game.isJunction(game.getPacmanCurrentNodeIndex())) {
			// Build state from game class
			GameState state = new GameState(game);
			move = MOVE.values()[qTable.getBestAction(state)];
		} else {
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
		GameView.addPoints(game, new Color(255, 0, 0), nearestJunction);
		return nearestJunction;
	}

}
