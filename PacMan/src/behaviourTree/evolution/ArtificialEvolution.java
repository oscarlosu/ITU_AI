package behaviourTree.evolution;

import java.util.ArrayList;
import java.util.Random;

import behaviourTree.Leaf;
import behaviourTree.Node;
import pacman.entries.pacman.BehaviourTreeController;

public class ArtificialEvolution {
	private int eliteSize;
	private int offspringSize;
	private int populationSize;
	
	// Initialization params
	private float addNodeChance = 0.25f;
	
	private ArrayList<BehaviourTreeController> population;
	
	public ArtificialEvolution(int eliteSize, int offspringSize) {
		this.eliteSize = eliteSize;
		this.offspringSize = offspringSize;
		populationSize = eliteSize + offspringSize;
		InitializePopulation();
	}
	
	private void InitializePopulation() {
		population = new ArrayList<BehaviourTreeController>();
		for(int i = 0; i < populationSize; ++i) {
			BehaviourTreeController bt = CreateRandomBTC();
		}
	}
	
	private BehaviourTreeController CreateRandomBTC() {
		BehaviourTreeController controller = new BehaviourTreeController();
		Random rng = new Random();
		// Add nodes (at least one)
		do {
			
		} while(rng.nextFloat() < addNodeChance);
		return controller;
	}
	

	
	private int Fitness(BehaviourTreeController bt) {
		return 0;
	}
	
	private void Selection() {
		
	}
	
	private void GenerateOffspring() {
		
	}
	
	private void MutateNode(Node n) {
		
	}
}
