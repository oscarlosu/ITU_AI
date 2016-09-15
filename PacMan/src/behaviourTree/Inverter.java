package behaviourTree;

public class Inverter implements NodeParent {
	private Node child;
	
	@Override
	public NodeState Process() {
		if(child.Process() == NodeState.SUCCESS) {
			return NodeState.FAILURE;
		} else {
			return NodeState.SUCCESS;
		}		
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
