package qlearning;

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
		}
		return move;
	}

}
