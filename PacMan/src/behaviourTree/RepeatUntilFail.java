package behaviourTree;

import java.util.ArrayList;

public class RepeatUntilFail implements Node {
	private ArrayList<Node> children;
	
	public RepeatUntilFail() {
		this.children = new ArrayList<Node>();
	}
	
	public void AddChild(Node node) {
		children.add(node);
	}
	
	@Override
	public NodeState Process() {
		for(int i = 0; i < children.size(); ++i) {
			if(children.get(i).Process() == NodeState.FAILURE) {
				return NodeState.SUCCESS;
			}
		}
		return NodeState.FAILURE;
	}
}
