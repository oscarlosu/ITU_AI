package mcts;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MCTSController extends Controller<MOVE> {

	private MCTS mcts;
	
	public MCTSController() {
		mcts = new MCTS();
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE move = MOVE.NEUTRAL;
		try {
			long startTime = System.currentTimeMillis();
			move =  mcts.runMCTS(game, timeDue);
			long endTime = System.currentTimeMillis();			
			System.out.println("MCTS execution time: " + (endTime -startTime) + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return move;
	}

}
