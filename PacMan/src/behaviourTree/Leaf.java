package behaviourTree;

import java.util.HashMap;

import pacman.entries.pacman.BehaviourTreeController;

public abstract class Leaf implements Node {
	protected HashMap<String, Object> context;
	
	public void Init(HashMap<String, Object> context) {
		this.context = context;
	}
}
