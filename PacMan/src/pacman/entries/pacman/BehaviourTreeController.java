package pacman.entries.pacman;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import behaviourTree.Leaf;
import behaviourTree.Node;
import behaviourTree.NodeParent;
import behaviourTree.Selector;
import behaviourTree.Sequence;
import behaviourTree.leaves.ShouldChase;
import behaviourTree.leaves.Chase;
import behaviourTree.leaves.CollectNearestPill;
import behaviourTree.leaves.CollectPowerPill;
import behaviourTree.leaves.Flee;
import behaviourTree.leaves.IsThreatNear;
import behaviourTree.leaves.IsPowerPillReachable;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class BehaviourTreeController extends Controller<MOVE>
{	
	private HashMap<String, Object> context;
	
	public Node bt = null;
	
	public BehaviourTreeController() {
		super();
		context = new HashMap<String, Object>();
		context.put("move", MOVE.NEUTRAL);		
	}
	
	public void ReadFromFile(String configFile) {
		try {
			Scanner f = new Scanner(new File(configFile));
			// Read params
			// Danger distance
			String paramLine = f.nextLine();
			String splitParam[] = paramLine.split(" ");			
			int dangerDist = Math.max(Integer.parseInt(splitParam[1]), 0);
			context.put("dangerDist", dangerDist);
			// Chase distance
			paramLine = f.nextLine();
			splitParam = paramLine.split(" ");			
			int chaseDist = Math.max(Integer.parseInt(splitParam[1]), 0);
			context.put("chaseDist", chaseDist);
			// Flee search depth
			paramLine = f.nextLine();
			splitParam = paramLine.split(" ");			
			int searchDepth = Math.max(Integer.parseInt(splitParam[1]), 0);
			context.put("searchDepth", searchDepth);
			
			// Read behaviour tree
			String className = f.nextLine();
			if(className.contains("behaviourTree.leaves.")) {
				// Leaf
				className = className.replaceAll("\\s+", "");
				bt = CreateLeaf(className);
			} else {
				// Node Parent
				className = className.replaceAll("\\s+", "");
				NodeParent n = CreateNodeParent(className);
				ReadChildren(f, n);
				bt = n;
			}
					
			f.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}

	private void ReadChildren(Scanner f, NodeParent parent) {
		String line = "";
		try {
			for(line = f.nextLine(); !line.contains("}"); line = f.nextLine()) {
				if(line.contains("{")) {
					continue;
				} else if(line.contains("behaviourTree.leaves.")) {
					// Leaf
					String className = line.replaceAll("\\s+", "");
					Node n = CreateLeaf(className);
					parent.AddChild(n);
				} else {
					// Node Parent
					String className = line.replaceAll("\\s+", "");
					NodeParent n = CreateNodeParent(className);
					parent.AddChild(n);
					ReadChildren(f, n);
				}
			}
		} catch (Exception e) {
			System.out.println("BehaviourTree initialisation failed because config file had incorrect format.");
			System.out.println("Last read: " + line);
		}
		
	}
	
	public void WriteToFile(String configFile) {
		try {
			FileWriter f = new FileWriter(configFile);
			// Write params
			// Danger dist
			int dangerDist = (int)context.get("dangerDist");
			f.write("dangerDist " + dangerDist + "\n");
			// Chase dist
			int chaseDist = (int)context.get("chaseDist");
			f.write("chaseDist " + chaseDist + "\n");
			// Search depth
			int searchDepth = (int)context.get("searchDepth");
			f.write("searchDepth " + searchDepth + "\n");
			// Write behaviour tree
			WriteNode(f, bt);
					
			f.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}
	
	private void WriteNode(FileWriter f, Node n) {
		try {
			String className = n.getClass().toString();
			className = className.substring("class ".length());
			Class<?> c = n.getClass();
			c = c.getSuperclass();
			if(c == Leaf.class) {
				// Leaf
				f.write(className + "\n");
			} else {
				// Node Parent
				f.write(className + "\n");
				f.write("{\n");
				WriteChildren(f, (NodeParent)n);
				f.write("}\n");
				bt = n;
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void WriteChildren(FileWriter f, NodeParent parent) {
		for(int i = 0; i < parent.ChildCount(); ++i) {
			Node n = parent.GetChild(i);
			WriteNode(f, n);
		}
	}
	
	private NodeParent CreateNodeParent(String className) {
		try {
			Class<?> action_class = Class.forName(className);
			Constructor<?> constructor = action_class.getConstructor();
			NodeParent node = (NodeParent)constructor.newInstance();
			return node;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}		
	}
	
	private Leaf CreateLeaf(String className) {
		try {
			Class<?> action_class = Class.forName(className);
			Constructor<?> constructor = action_class.getConstructor(HashMap.class);
			Leaf leaf = (Leaf)constructor.newInstance(context);
			return leaf;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}		
	}

	public MOVE getMove(Game game, long timeDue) 
	{
		// Update context		
		context.put("game", game);		
		// Run behaviour tree
		if(bt != null) {
			bt.Process();
		}		
		return (MOVE)context.get("move");
	}

}
