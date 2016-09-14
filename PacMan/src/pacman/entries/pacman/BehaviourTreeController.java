package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.HashMap;

import behaviourTree.Node;
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
	
	private int dangerDist;
	
	private Node bt;
	
	public BehaviourTreeController(int dangerDist, int chaseDist, int searchDepth) {
		super();
		context = new HashMap<String, Object>();
		context.put("dangerDist", dangerDist);
		context.put("chaseDist", chaseDist);
		context.put("move", MOVE.NEUTRAL);
		context.put("searchDepth", searchDepth);
		
		// Build behaviour tree
		Selector root = new Selector();
		// Danger
		Sequence danger = new Sequence();
			// Threat near?
		danger.AddChild(new IsThreatNear(context));
			// Handle threat
		Selector handleThreat = new Selector();
				// Eat power pill?
		Sequence powerPills = new Sequence();
		powerPills.AddChild(new IsPowerPillReachable(context));	
		powerPills.AddChild(new CollectPowerPill(context));
		
		handleThreat.AddChild(powerPills);
				// Flee
		handleThreat.AddChild(new Flee(context));
		
		danger.AddChild(handleThreat);
		root.AddChild(danger);
		// Chase ghost?
		Sequence chaseGhost = new Sequence();
		chaseGhost.AddChild(new ShouldChase(context));
		chaseGhost.AddChild(new Chase(context));
		
		root.AddChild(chaseGhost);		
		
		// Collect pills
		root.AddChild(new CollectNearestPill(context));		
		
		// Save reference
		bt = root;
	}
	
	public MOVE getMove(Game game, long timeDue) 
	{
		// Update context		
		context.put("game", game);		
		// Run behaviour tree
		bt.Process();	
		return (MOVE)context.get("move");
	}
}
