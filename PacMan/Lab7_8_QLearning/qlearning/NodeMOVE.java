package qlearning;

import pacman.game.Constants.MOVE;

public class NodeMOVE {
	public int nodeIndex;
	public MOVE lastMove;
	
	public NodeMOVE(int nodeIndex, MOVE lastMove) {
		this.nodeIndex = nodeIndex;
		this.lastMove = lastMove;
	}
	
	public int hashCode() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(nodeIndex);
		builder.append(lastMove.ordinal());
		return builder.toString().hashCode();
	}
	
}
