package dataRecording;

import pacman.controllers.*;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * The DataCollectorHumanController class is used to collect training data from playing PacMan.
 * Data about game state and what MOVE chosen is saved every time getMove is called.
 * @author andershh
 *
 */
public class DataCollectorController extends HumanController{
	private String filename;
	public DataCollectorController(KeyBoardInput input, String filename){
		super(input);
		this.filename = filename;
	}
	
	@Override
	public MOVE getMove(Game game, long dueTime) {	
		MOVE chosenMove = super.getMove(game, dueTime);
		MOVE possibleMoves[] = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		boolean legalMove = false;
		for(MOVE m: possibleMoves) {
			if(m == chosenMove) {
				legalMove = true;
				break;
			}
		}
		if(chosenMove == MOVE.NEUTRAL || !legalMove) {
			chosenMove = game.getPacmanLastMoveMade();
		}
		
		DataTuple data = new DataTuple(game, chosenMove);
		DataSaverLoader.SavePacManData(data, filename);
		
		// Execute action indicated by player input		
		return chosenMove;
	}

}
