package neuralNetwork.dataRecording;

import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

public class DataTuple {
	
	public MOVE DirectionChosen;
	
	//General game state info - not normalized!
	public double mazeIndex;
	public double currentLevel;
	public double pacmanIndex;
	public double pacmanLivesLeft;
	public double currentScore;
	public double totalGameTime;
	public double currentLevelTime;
	public double numOfPillsLeft;
	public double numOfPowerPillsLeft;
	
	//Ghost this, dir, dist, edible - BLINKY, INKY, PINKY, SUE
	public double isBlinkyEdible = 0;
	public double isInkyEdible = 0;
	public double isPinkyEdible = 0;
	public double isSueEdible = 0;
	
	public double blinkyDist = -1;
	public double inkyDist = -1;
	public double pinkyDist = -1;
	public double sueDist = -1;
	
	public MOVE blinkyDir;
	public MOVE inkyDir;
	public MOVE pinkyDir;
	public MOVE sueDir;
	
	
	//Util data - useful for normalization
	public int numberOfNodesInLevel;
	public int numberOfTotalPillsInLevel;
	public int numberOfTotalPowerPillsInLevel;
	
	// Added data
	public double blinkyIndex;
	public double inkyIndex;
	public double pinkyIndex;
	public double sueIndex;
	
	public double pacmanX;
	public double pacmanY;
	
	public double blinkyX;
	public double blinkyY;
	public double inkyX;
	public double inkyY;
	public double pinkyX;
	public double pinkyY;
	public double sueX;
	public double sueY;
	
	
	public double nearestPillX;
	public double nearestPillY;
	public double nearestPowerPillX;
	public double nearestPowerPillY;
	public double nearestPillDist;
	public double nearestPowerPillDist;
	
	public double upLegal;
	public double downLegal;
	public double leftLegal;
	public double rightLegal;
	
	
	// Empirically observed bounds for positions
	public static final int MIN_X=0;
	public static final int MAX_X=108;
	public static final int MIN_Y=4;		
	public static final int MAX_Y=116;	
	
	
	public DataTuple(Game game, MOVE move)
	{
		this.numberOfNodesInLevel = game.getNumberOfNodes();
		this.numberOfTotalPillsInLevel = game.getNumberOfPills();
		this.numberOfTotalPowerPillsInLevel = game.getNumberOfPowerPills();
		
		if(move == MOVE.NEUTRAL) {
			this.DirectionChosen = game.getPacmanLastMoveMade();
		} else {
			this.DirectionChosen = move;
		}
		
		
		this.mazeIndex = normalizeLevel(game.getMazeIndex());
		this.currentLevel = normalizeLevel(game.getCurrentLevel());
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		this.pacmanIndex = normalizeIndex(pacmanIndex, this.numberOfNodesInLevel);
		this.pacmanLivesLeft = normalizeLives(game.getPacmanNumberOfLivesRemaining());
		this.currentScore = normalizeCurrentScore(game.getScore());
		this.totalGameTime = normalizeTotalGameTime(game.getTotalTime());
		this.currentLevelTime = normalizeCurrentLevelTime(game.getCurrentLevelTime());
		this.numOfPillsLeft = normalizeNumberOfPills(game.getNumberOfActivePills(), this.numberOfTotalPillsInLevel);
		this.numOfPowerPillsLeft = normalizeNumberOfPowerPills(game.getNumberOfActivePowerPills(), this.numberOfTotalPowerPillsInLevel);
		
		if (game.getGhostLairTime(GHOST.BLINKY) == 0) {
			this.isBlinkyEdible = normalizeBoolean(game.isGhostEdible(GHOST.BLINKY));
			this.blinkyDist = normalizeDistance(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.BLINKY)), this.numberOfNodesInLevel);
		}
		
		if (game.getGhostLairTime(GHOST.INKY) == 0) {
		this.isInkyEdible = normalizeBoolean(game.isGhostEdible(GHOST.INKY));
		this.inkyDist = normalizeDistance(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.INKY)), this.numberOfNodesInLevel);
		}
		
		if (game.getGhostLairTime(GHOST.PINKY) == 0) {
		this.isPinkyEdible = normalizeBoolean(game.isGhostEdible(GHOST.PINKY));
		this.pinkyDist = normalizeDistance(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.PINKY)), this.numberOfNodesInLevel);
		}
		
		if (game.getGhostLairTime(GHOST.SUE) == 0) {
		this.isSueEdible = normalizeBoolean(game.isGhostEdible(GHOST.SUE));
		this.sueDist = normalizeDistance(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.SUE)), this.numberOfNodesInLevel);
		}
		
		this.blinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.PATH);
		this.inkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.INKY), DM.PATH);
		this.pinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.PATH);
		this.sueDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(GHOST.SUE), DM.PATH);
		
		
		
		// Ghost indexes
		int blinkyIndex = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
		int inkyIndex = game.getGhostCurrentNodeIndex(GHOST.INKY);
		int pinkyIndex = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int sueIndex = game.getGhostCurrentNodeIndex(GHOST.SUE);
		
		this.blinkyIndex = normalizeIndex(blinkyIndex, this.numberOfNodesInLevel);
		this.inkyIndex = normalizeIndex(inkyIndex, this.numberOfNodesInLevel);
		this.pinkyIndex = normalizeIndex(pinkyIndex, this.numberOfNodesInLevel);
		this.sueIndex = normalizeIndex(sueIndex, this.numberOfNodesInLevel);
		
		Node nodes[] = game.getCurrentMaze().graph;
		
		
		// Pacman position
		Node n = nodes[pacmanIndex];
		Position p = normalizePosition(n.x, n.y);
		this.pacmanX = p.x;
		this.pacmanY = p.y;
		//System.out.println("pacman x " + this.pacmanX + " y " + this.pacmanY);
		// Ghost positions
		n = nodes[blinkyIndex];
		p = normalizePosition(n.x, n.y);
		this.blinkyX = p.x;
		this.blinkyY = p.y;
		n = nodes[inkyIndex];
		p = normalizePosition(n.x, n.y);
		this.inkyX = p.x;
		this.inkyY = p.y;
		n = nodes[pinkyIndex];
		p = normalizePosition(n.x, n.y);
		this.pinkyX = p.x;
		this.pinkyY = p.y;
		n = nodes[sueIndex];
		p = normalizePosition(n.x, n.y);
		this.sueX = p.x;
		this.sueY = p.y;
		//System.out.println("sue x " + this.sueX + " y " + this.sueY);
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
		// Set max dist if no more pills
		if(nearestPill < 0) {
			this.nearestPillX = 1;
			this.nearestPillY = 1;
			this.nearestPillDist = 1;
		} else {
			Position pillPos = normalizePosition(nodes[nearestPill].x, nodes[nearestPill].y);
			this.nearestPillX = pillPos.x;
			this.nearestPillY = pillPos.y;
			this.nearestPillDist = normalizeDistance(nearestDist, this.numberOfNodesInLevel);
		}
		// Find nearest power pill
		int powerPills[] = game.getActivePowerPillsIndices();
		int nearestPowerPill = -1; // Default
		nearestDist = Integer.MAX_VALUE;
		for(int i = 0; i < powerPills.length; ++i) {
			int dist = game.getShortestPathDistance(pacmanIndex, powerPills[i]);
			if(dist < nearestDist) {
				nearestDist = dist;
				nearestPowerPill = powerPills[i];
			}
		}
		// Set max dist if no more power pills
		if(nearestPowerPill < 0) {
			this.nearestPowerPillX = 1;
			this.nearestPowerPillY = 1;
			this.nearestPowerPillDist = 1;
		} else {
			Position powerPillPos = normalizePosition(nodes[nearestPowerPill].x, nodes[nearestPowerPill].y);
			this.nearestPowerPillX = powerPillPos.x;
			this.nearestPowerPillY = powerPillPos.y;
			this.nearestPowerPillDist = normalizeDistance(nearestDist, this.numberOfNodesInLevel);
		}
		
		
		// Legal moves
		this.upLegal = 0.0;
		this.downLegal = 0.0;
		this.leftLegal = 0.0;
		this.rightLegal = 0.0;
		for(MOVE m: game.getPossibleMoves(pacmanIndex)) {
			if(m == MOVE.UP) {
				this.upLegal = 1.0;
			} else if(m == MOVE.DOWN) {
				this.downLegal = 1.0;
			} else if(m == MOVE.LEFT) {
				this.leftLegal = 1.0;
			} else if(m == MOVE.RIGHT) {
				this.rightLegal = 1.0;
			}
		}
	}
	
	public DataTuple(String data)
	{
		String[] dataSplit = data.split(";");
		
		this.DirectionChosen = MOVE.valueOf(dataSplit[0]);
		
		this.mazeIndex = Double.parseDouble(dataSplit[1]);
		this.currentLevel = Double.parseDouble(dataSplit[2]);
		this.pacmanIndex = Double.parseDouble(dataSplit[3]);
		this.pacmanLivesLeft = Double.parseDouble(dataSplit[4]);
		this.currentScore = Double.parseDouble(dataSplit[5]);
		this.totalGameTime = Double.parseDouble(dataSplit[6]);
		this.currentLevelTime = Double.parseDouble(dataSplit[7]);
		this.numOfPillsLeft = Double.parseDouble(dataSplit[8]);
		this.numOfPowerPillsLeft = Double.parseDouble(dataSplit[9]);
		this.isBlinkyEdible = Double.parseDouble(dataSplit[10]);
		this.isInkyEdible = Double.parseDouble(dataSplit[11]);
		this.isPinkyEdible = Double.parseDouble(dataSplit[12]);
		this.isSueEdible = Double.parseDouble(dataSplit[13]);
		this.blinkyDist = Double.parseDouble(dataSplit[14]);
		this.inkyDist = Double.parseDouble(dataSplit[15]);
		this.pinkyDist = Double.parseDouble(dataSplit[16]);
		this.sueDist = Double.parseDouble(dataSplit[17]);
		this.blinkyDir = MOVE.valueOf(dataSplit[18]);
		this.inkyDir = MOVE.valueOf(dataSplit[19]);
		this.pinkyDir = MOVE.valueOf(dataSplit[20]);
		this.sueDir = MOVE.valueOf(dataSplit[21]);
//		this.numberOfNodesInLevel = Integer.parseInt(dataSplit[22]);
//		this.numberOfTotalPillsInLevel = Integer.parseInt(dataSplit[23]);
//		this.numberOfTotalPowerPillsInLevel = Integer.parseInt(dataSplit[24]);
		
		// Added data
		this.blinkyIndex = Double.parseDouble(dataSplit[22]);
		this.inkyIndex = Double.parseDouble(dataSplit[23]);
		this.pinkyIndex = Double.parseDouble(dataSplit[24]);
		this.sueIndex = Double.parseDouble(dataSplit[25]);
		
		this.pacmanX = Double.parseDouble(dataSplit[26]);
		this.pacmanY = Double.parseDouble(dataSplit[27]);	
		
		this.blinkyX = Double.parseDouble(dataSplit[28]);
		this.blinkyY = Double.parseDouble(dataSplit[29]);
		this.inkyX = Double.parseDouble(dataSplit[30]);
		this.inkyY = Double.parseDouble(dataSplit[31]);
		this.pinkyX = Double.parseDouble(dataSplit[32]);
		this.pinkyY = Double.parseDouble(dataSplit[33]);
		this.sueX = Double.parseDouble(dataSplit[34]);
		this.sueY = Double.parseDouble(dataSplit[35]);
		
		this.nearestPillDist = Double.parseDouble(dataSplit[36]);
		this.nearestPowerPillDist = Double.parseDouble(dataSplit[37]);
		
		this.nearestPillX = Double.parseDouble(dataSplit[38]);
		this.nearestPillY = Double.parseDouble(dataSplit[39]);
		this.nearestPowerPillX = Double.parseDouble(dataSplit[40]);
		this.nearestPowerPillY = Double.parseDouble(dataSplit[41]);
		
		this.upLegal = Double.parseDouble(dataSplit[42]);
		this.downLegal = Double.parseDouble(dataSplit[42]);
		this.leftLegal = Double.parseDouble(dataSplit[42]);
		this.rightLegal = Double.parseDouble(dataSplit[42]);
	}
	
	public String getSaveString()
	{
		StringBuilder stringbuilder = new StringBuilder();
		
		stringbuilder.append(this.DirectionChosen+";");
		stringbuilder.append(this.mazeIndex+";");
		stringbuilder.append(this.currentLevel+";");
		stringbuilder.append(this.pacmanIndex+";");
		stringbuilder.append(this.pacmanLivesLeft+";");
		stringbuilder.append(this.currentScore+";");
		stringbuilder.append(this.totalGameTime+";");
		stringbuilder.append(this.currentLevelTime+";");
		stringbuilder.append(this.numOfPillsLeft+";");
		stringbuilder.append(this.numOfPowerPillsLeft+";");
		stringbuilder.append(this.isBlinkyEdible+";");
		stringbuilder.append(this.isInkyEdible+";");
		stringbuilder.append(this.isPinkyEdible+";");
		stringbuilder.append(this.isSueEdible+";");
		stringbuilder.append(this.blinkyDist+";");
		stringbuilder.append(this.inkyDist+";");
		stringbuilder.append(this.pinkyDist+";");
		stringbuilder.append(this.sueDist+";");
		stringbuilder.append(this.blinkyDir+";");
		stringbuilder.append(this.inkyDir+";");
		stringbuilder.append(this.pinkyDir+";");
		stringbuilder.append(this.sueDir+";");
//		stringbuilder.append(this.numberOfNodesInLevel+";");
//		stringbuilder.append(this.numberOfTotalPillsInLevel+";");
//		stringbuilder.append(this.numberOfTotalPowerPillsInLevel+";");
		
		// Added data
		stringbuilder.append(this.blinkyIndex+";");
		stringbuilder.append(this.inkyIndex+";");
		stringbuilder.append(this.pinkyIndex+";");
		stringbuilder.append(this.sueIndex+";");
		
		stringbuilder.append(this.pacmanX+";");
		stringbuilder.append(this.pacmanY+";");
		
		stringbuilder.append(this.blinkyX+";");
		stringbuilder.append(this.blinkyY+";");
		stringbuilder.append(this.inkyX+";");
		stringbuilder.append(this.inkyY+";");
		stringbuilder.append(this.pinkyX+";");
		stringbuilder.append(this.pinkyY+";");
		stringbuilder.append(this.sueX+";");
		stringbuilder.append(this.sueY+";");
		
		stringbuilder.append(this.nearestPillDist+";");
		stringbuilder.append(this.nearestPowerPillDist+";");
		
		stringbuilder.append(this.nearestPillX+";");
		stringbuilder.append(this.nearestPillY+";");
		stringbuilder.append(this.nearestPowerPillX+";");
		stringbuilder.append(this.nearestPowerPillY+";");
		
		stringbuilder.append(this.upLegal+";");
		stringbuilder.append(this.downLegal+";");
		stringbuilder.append(this.leftLegal+";");
		stringbuilder.append(this.rightLegal+";");
		
		return stringbuilder.toString();
	}

	/**
	 * Used to normalize distances. Done via min-max normalization.
	 * Assumes that minimum possible distance is 0. Assumes that
	 * the maximum possible distance is the total number of nodes in
	 * the current level.
	 * @param dist Distance to be normalized
	 * @return Normalized distance
	 */
	public static double normalizeDistance(int dist, int numberOfNodesInLevel)
	{
		return ((dist-0)/(double)(numberOfNodesInLevel-0))*(1-0)+0;
	}
	
	public static double normalizeLevel(int level)
	{
		return ((level-0)/(double)(Constants.NUM_MAZES-0))*(1-0)+0;
	}
	
	public static double normalizeIndex(int position, int numberOfNodesInLevel)
	{
		return ((position-0)/(double)(numberOfNodesInLevel-0))*(1-0)+0;
	}
	
	public static double normalizeBoolean(boolean bool)
	{
		if(bool)
		{
			return 1.0;
		}
		else
		{
			return 0.0;
		}
	}
	
	public static double normalizeNumberOfPills(int numOfPills, int numberOfTotalPillsInLevel)
	{
		return ((numOfPills-0)/(double)(numberOfTotalPillsInLevel-0))*(1-0)+0;
	}
	
	public static double normalizeNumberOfPowerPills(int numOfPowerPills, int numberOfTotalPowerPillsInLevel)
	{
		return ((numOfPowerPills-0)/(double)(numberOfTotalPowerPillsInLevel-0))*(1-0)+0;
	}
	
	public static double normalizeTotalGameTime(int time)
	{
		return ((time-0)/(double)(Constants.MAX_TIME-0))*(1-0)+0;
	}
	
	public static double normalizeCurrentLevelTime(int time)
	{
		return ((time-0)/(double)(Constants.LEVEL_LIMIT-0))*(1-0)+0;
	}
	
	public static Position normalizePosition(int x, int y)
	{		
		return new Position((x - MIN_X) / (double)(MAX_X - MIN_X), (y - MIN_Y) / (double)(MAX_Y - MIN_Y));
	}
	
	public static double normalizeLives(int val) {
		return val / (double)Constants.NUM_LIVES;
	}
	
	/**
	 * 
	 * Max score value lifted from highest ranking PacMan controller on PacMan vs Ghosts
	 * website: http://pacman-vs-ghosts.net/controllers/1104
	 * @param score
	 * @return
	 */
	public static double normalizeCurrentScore(int score)
	{
		return ((score-0)/(double)(82180-0))*(1-0)+0;
	}
	
}
