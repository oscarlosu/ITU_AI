package behaviourTree;

import pacman.entries.pacman.BehaviourTreeController;

public interface Node {
	public abstract NodeState Process();
}
