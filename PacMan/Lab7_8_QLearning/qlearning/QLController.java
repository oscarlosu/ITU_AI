package qlearning;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class QLController extends Controller<MOVE> {
	public QTable qTable;
	
	public QLController() {
		qTable = new QTable(MOVE.values().length);
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {	
		// Build state from game class
		GameState state = new GameState();
		return MOVE.values()[qTable.getBestAction(state)];
	}

}
