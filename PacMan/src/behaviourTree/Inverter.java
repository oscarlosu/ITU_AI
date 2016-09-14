package behaviourTree;

public class Inverter implements Node {
	private Node child;
	
	public Inverter(Node child) {
		this.child = child;
	}
	@Override
	public NodeState Process() {
		if(child.Process() == NodeState.SUCCESS) {
			return NodeState.FAILURE;
		} else {
			return NodeState.SUCCESS;
		}		
	}
}
