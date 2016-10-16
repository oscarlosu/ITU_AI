package oslo.mcts;

import java.util.ArrayList;
import java.util.List;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * Class to store node information, e.g.
 * state, children, parent, accumulative reward, visited times
 * @author dariusv
 * @modified A. Hartzen
 *
 */
public class Node{
	
	public Game state;
	public List<Node> children = new ArrayList<Node>();
	public Node parent = null;
	public MOVE parentAction = null;
	public float reward =0;
	public int timesvisited = 0;
	
	
	Node(Game state){
		this.state = state;
	}
}
