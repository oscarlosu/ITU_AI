package behaviourTree;

public class Succeeder implements Node {
	private Node child;
	
	public Succeeder(Node child) {
		this.child = child;
	}
	@Override
	public NodeState Process() {
		child.Process();
		return NodeState.SUCCESS;
	}
}
