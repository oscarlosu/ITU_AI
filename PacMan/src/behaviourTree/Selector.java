package behaviourTree;

import java.util.ArrayList;

public class Selector implements NodeParent {
	private ArrayList<Node> children;
	
	public Selector() {
		this.children = new ArrayList<Node>();
	}
		
	public void AddChild(Node node) {
		children.add(node);
	}
	@Override
	public NodeState Process() {
		for(int i = 0; i < children.size(); ++i) {
			if(children.get(i).Process() == NodeState.SUCCESS) {
				return NodeState.SUCCESS;
			}
		}
		return NodeState.FAILURE;
	}
	
	@Override
	public Node GetChild(int index) {
		return children.get(index);
	}

	@Override
	public int ChildCount() {
		return children.size();
	}
}
