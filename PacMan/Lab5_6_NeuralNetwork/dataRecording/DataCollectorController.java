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
		if(chosenMove == MOVE.NEUTRAL) {
			chosenMove = game.getPacmanLastMoveMade();
		}
		// Save game states for each move - without ghost actions - giving a score of 1 to the option that
		// the player chose, and 0 to the rest
		DataTuple data;
		for(MOVE move : MOVE.values()) {
			// Skip neutral
			if(move != MOVE.NEUTRAL) {
				Game copy = game.copy();
				copy.updatePacMan(move);
				if(move == chosenMove) {
					data = new DataTuple(game, 1.0);				
				} else {
					data = new DataTuple(game, 0.0);	
				}
				DataSaverLoader.SavePacManData(data, filename);
			}			
		}		
		// Execute action indicated by player input		
		return chosenMove;
	}

}
