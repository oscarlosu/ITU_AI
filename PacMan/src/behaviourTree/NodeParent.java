package behaviourTree;

public interface NodeParent extends Node {
	public abstract void AddChild(Node n);
	public abstract Node GetChild(int index);
	public abstract int ChildCount();
}
