package behaviourTree;

import java.util.ArrayList;

public class Sequence implements NodeParent {
	private ArrayList<Node> children;
	
	public Sequence() {
		this.children = new ArrayList<Node>();
	}
	
	public void AddChild(Node node) {
		children.add(node);
	}
	@Override
	public NodeState Process() {
		for(int i = 0; i < children.size(); ++i) {
			if(children.get(i).Process() == NodeState.FAILURE) {
				return NodeState.FAILURE;
			}
		}
		return NodeState.SUCCESS;
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
