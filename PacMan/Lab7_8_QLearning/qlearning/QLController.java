package qlearning;

import java.util.ArrayList;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

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
		MOVE move = MOVE.NEUTRAL;
		if(game.isJunction(game.getPacmanCurrentNodeIndex())) {
			// Build state from game class
			GameState state = new GameState(game);
			move = MOVE.values()[qTable.getBestAction(state)];
		} else {
			MOVE lastMove = game.getPacmanLastMoveMade();
    		MOVE possibleMoves[] = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
    		ArrayList<MOVE> possibleMovesNoReversal = new ArrayList<MOVE>();
    		boolean possible = false;
    		for(MOVE m : possibleMoves) {
    			if(m != lastMove.opposite()) {
    				possibleMovesNoReversal.add(m);
    				if(m == move) {
    					possible = true;
    				}        				
    			}
    		}
    		if(!possible && possibleMovesNoReversal.size() > 0) {
    			move = possibleMovesNoReversal.get(new Random().nextInt(possibleMovesNoReversal.size()));
    		}
		}
		return move;
	}

}
