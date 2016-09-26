package controller;
import influenceMap.InfluenceMap;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Node;
import pacman.game.Game;

public class InfluenceMapController extends Controller<MOVE> {
	private InfluenceMap map;
	private Game lastGame;
	public InfluenceMapController(String filename) {
		map = InfluenceMap.LoadFromFile(filename);
	}
	
	public InfluenceMapController(InfluenceMap map) {
		this.map = map;
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		double startTime = System.nanoTime() / 1000000.0;
		
		map.createGraph(game);
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		int path[] = map.computePathsAStar(pacmanNode, map.getBestNode(), game);
		
		MOVE move =  MOVE.NEUTRAL;
		if(path.length > 1) {
			move = game.getMoveToMakeToReachDirectNeighbour(pacmanNode, path[1]);
		}
		
		
		double endTime = System.nanoTime() / 1000000.0;
		//System.out.println("Total execution time: " + (endTime-startTime) + "ms");

		return move;
	}

	public InfluenceMap getMap() {
		return map;
	}

	public void setMap(InfluenceMap map) {
		this.map = map;
	}
	
	

}
