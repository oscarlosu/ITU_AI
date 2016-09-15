package behaviourTree;

public class Succeeder implements NodeParent {
	private Node child;
	
	@Override
	public NodeState Process() {
		child.Process();
		return NodeState.SUCCESS;
	}
	@Override
	public void AddChild(Node n) {
		child = n;
	}
	
	@Override
	public Node GetChild(int index) {
		return child;
	}

	@Override
	public int ChildCount() {
		return 1;
	}
}
