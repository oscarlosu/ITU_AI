package behaviourTree;

import java.util.HashMap;

import behaviourTree.controller.BTController;

public abstract class Leaf implements Node {
	protected HashMap<String, Object> context;
	
	public void Init(HashMap<String, Object> context) {
		this.context = context;
	}
	
}
