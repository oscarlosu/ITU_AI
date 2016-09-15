package behaviourTree;

import behaviourTree.controller.BTController;

public interface Node {
	public abstract NodeState Process();
}
