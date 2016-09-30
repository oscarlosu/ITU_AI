package controller;
import influenceMap.InfluenceMap;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Node;
import pacman.game.Game;

public class InfluenceMapController extends Controller<MOVE> {
	private InfluenceMap map;
	private Game lastGame;
	
	private int levelIndex = -1;
	
	public InfluenceMapController(String filename) {
		map = InfluenceMap.LoadFromFile(filename);
	}
	
	public InfluenceMapController(InfluenceMap map) {
		this.map = map;
	}
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		double startTime = System.nanoTime() / 1000000.0;
		// Only rebuild graph at the start of each level
		if(levelIndex != game.getCurrentLevel()) {
			map.createGraph(game);
			levelIndex = game.getCurrentLevel();
			System.out.println("MAP RECREATED");
		}
		
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		map.ChooseTarget(game);
		int path[] = map.computePathsAStar(pacmanNode, 1000, game);
		
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
