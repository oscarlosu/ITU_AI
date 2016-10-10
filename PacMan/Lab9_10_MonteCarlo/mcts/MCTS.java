package mcts;

import java.util.EnumMap;
import java.util.Random;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;

public class MCTS {

	private Random random = new Random();
	
	/*
	 * rootNode is the starting point of the present state
	 */
	Node rootNode;
	
	/*
	 * currentNode refers to the node we work at every step
	 */
	Node currentNode;
	
	/*
	 * Exploration coefficient
	 */
	private float C = (float) (1.0/Math.sqrt(2));
	
	
	protected final int maxFramesPerIteration = 100;
	protected final int terminationMargin = 2;
	
	int simulatedFrames;
	
	
	Controller<EnumMap<GHOST,MOVE>> ghostSimulator;
	
	MCTS(){
		ghostSimulator = new Legacy2TheReckoning();
	}
	
	/**
	 * run the UCT search and find the optimal action for the root node state
	 * @return
	 * @throws InterruptedException
	 */
	public MOVE runMCTS(Game game, long timeDue) throws InterruptedException{
		
            /*
             * Create root node with the present state
             */
            rootNode = new Node(game.copy());
            
            /*
             * Apply UCT search inside computational budget limit (default=100 iterations) 
             */
            int iterations = 0;
            float reward = 0;
            while(!Terminate(timeDue)){
            	iterations ++;
            	simulatedFrames = 0;
            	TreePolicy();
            	reward = DefaultPolicy();
            	Backpropagate(reward);
            	//System.out.println("Iteration: " + iterations + " simulated frames: " + simulatedFrames);
            	
            }
            System.out.println("Iterations: " + iterations);
            /*
             * Get the action that directs to the best node
             */
            currentNode = rootNode;
            //rootNode is the one we are working with 
            //and we apply the exploitation of it to find the child with the highest average reward
            BestChild(0);
            MOVE bestAction = MOVE.NEUTRAL;
            if(currentNode != null) {
            	bestAction = currentNode.parentAction;
            }
            
            return bestAction;
	}
	
	/**
	 * Expand the nonterminal nodes with one available child. 
	 * Chose a node to expand with BestChild(C) method
	 */
	private void TreePolicy() {
		currentNode = rootNode;
		while(!TerminalState(currentNode.state)) {
			if(!FullyExpanded(currentNode)) {
				Expand();
				return;
			} else {
				BestChild(C);
			}
		}
		
	}
	
	/**
	 * Simulation of the game. Choose random actions up until the game is over (goal reached or dead)
	 * @return reward (1 for win, 0 for loss)
	 */
	private float DefaultPolicy() {
		Game st = currentNode.state.copy();
		while(!TerminalState(st) && simulatedFrames < maxFramesPerIteration){
			MOVE pacmanAction = RandomAction(st, st.getPacmanCurrentNodeIndex());			
			//EnumMap<GHOST, MOVE> ghostActions = RandomGhostActions(st);
			st.advanceGame(pacmanAction, ghostSimulator.getMove(st, 0));
			simulatedFrames++;
		}		
		return st.getScore();
	}

	/**
	 * Assign the received reward to every parent of the parent up to the rootNode
	 * Increase the visited count of every node included in backpropagation
	 * @param reward
	 */
	private void Backpropagate(float reward) {
		while (currentNode != null) {
			currentNode.timesvisited += 1;
			currentNode.reward += reward;
			currentNode = currentNode.parent;
		}
	}
	
	private boolean FullyExpanded(Node nt) {
		boolean unexploredAction = true;
		MOVE possibleMoves[] = nt.state.getPossibleMoves(nt.state.getPacmanCurrentNodeIndex());
		for (MOVE m : possibleMoves){
			if(m == MOVE.NEUTRAL) {
				continue;
			}
			unexploredAction = true;
			for (int k=0;k<nt.children.size();k++){
				if (m == nt.children.get(k).parentAction){
					unexploredAction = false;
					break;
				}
			}
			if(unexploredAction) {
				return false;
			}
		}
		return true;
	}

	private boolean TerminalState(Game state) {
		return state.gameOver();
	}

	/**
	 * Choose the best child according to the UCT value
	 * Assign it as a currentNode
	 * @param c Exploration coefficient
	 */
	private void BestChild(float c) {
		Node nt = currentNode;
		Node bestChild = null;
		float bestUCTValue = Float.MIN_VALUE;
		for(Node child : nt.children) {
			float uctValue = UCTvalue(child, c);
			if(bestChild == null || uctValue > bestUCTValue) {
				bestChild = child;
				bestUCTValue = uctValue;
			}
		}
		
		currentNode = bestChild;
	}

	/**
	 * Calculate UCT value for the best child choosing
	 * @param n child node of currentNode
	 * @param c Exploration coefficient
	 * @return
	 */
	private float UCTvalue(Node n, float c) {
		if(n.timesvisited == 0) {
			return Float.MAX_VALUE;
		}
		float exploitation = n.reward / (float)n.timesvisited;
		float exploration = (float)(c * Math.sqrt(2 * Math.log(n.parent.timesvisited) / (double)n.timesvisited));
		return exploitation + exploration;
	}

	/**
	 * Expand the current node by adding new child to the currentNode
	 */
	private void Expand() {
		// Choose untried action
		MOVE action = UntriedAction(currentNode);
		// Simulate on copy of game
		Game st = currentNode.state.copy();
		st.advanceGame(action, ghostSimulator.getMove(st, 0));
		Node child = new Node(st);
		child.parent = currentNode;
		child.parentAction = action;
		currentNode.children.add(child);
		
		currentNode = child;		
	}

	/**
	 * Returns the first untried action of the node
	 * @param n
	 * @return
	 */
	private MOVE UntriedAction(Node nt) {
		boolean unexploredAction = true;
		MOVE possibleMoves[] = nt.state.getPossibleMoves(nt.state.getPacmanCurrentNodeIndex());
		for (MOVE m : possibleMoves){
			if(m == MOVE.NEUTRAL) {
				continue;
			}
			unexploredAction = true;
			for (int k=0;k<nt.children.size();k++){
				if (m == nt.children.get(k).parentAction){
					unexploredAction = false;
					break;
				}
			}
			if(unexploredAction) {
				return m;
			}
		}
		System.out.println("null");
		return null;
	}

	/**
	 * Check if the algorithm is to be terminated, e.g. reached number of iterations limit
	 * @param i
	 * @return
	 */
	private boolean Terminate(long timeDue) {
		long remaining = timeDue - System.currentTimeMillis();
		return remaining < terminationMargin;
	}
	
	private MOVE RandomAction(Game st, int node) {
		MOVE possibleMoves[] = st.getPossibleMoves(node);
		MOVE action = MOVE.NEUTRAL;
		if(possibleMoves.length > 0) {
			action = possibleMoves[random.nextInt(possibleMoves.length)];   
		}    
        return action;
	}
	
	private EnumMap<GHOST, MOVE> RandomGhostActions(Game st) {
		EnumMap<GHOST, MOVE> ghostActions = new EnumMap<GHOST, MOVE>(GHOST.class);
        for(GHOST g : GHOST.values()) {
        	MOVE action = RandomAction(st, st.getGhostCurrentNodeIndex(g));
        	ghostActions.put(g, action);
        }
        return ghostActions;
	}
}
