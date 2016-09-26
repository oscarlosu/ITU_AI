package evolution;

import static pacman.game.Constants.DELAY;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import behaviourTree.Leaf;
import behaviourTree.Node;
import behaviourTree.controller.BTController;
import controller.InfluenceMapController;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class ArtificialEvolution {
	// Evolution params
	private int eliteSize = 4;
	private int offspringSize = 6;
	private int populationSize = 10;	
	// Mutation
	private double mutationChance = 0.2;
	private double geneMutationChance = 0.2;
	private double mutationFactorLowerBound = 0.5;
	private double mutationFactorUpperBound = 1.5;
	// Initialisation mutation
	private double initGeneMutationChance = 1.0;
	private double initMutationFactorLowerBound = 0.25;
	private double initMutationFactorUpperBound = 1.75;
	// Evaluation
	private int evaluationTrials = 1;	
	
	// Stats
	private double avgFitness;
	private double bestFitness;
	private double worstFitness;
	
	private LinkedList<IMCGenotype> sortedPopulation;	
	private ArrayList<IMCGenotype> population;
	
	public static void main(String args[]) {
		ArtificialEvolution ae = new ArtificialEvolution();
		int generations = 10;
		String championFilename = "/InfluenceMap/evolvedIMC.json";
		System.out.println("Evolving: (" + generations + " generations)");
		ae.Evolve(generations, championFilename);
		System.out.println("Done");
	}
	
	public ArtificialEvolution() {
		populationSize = eliteSize + offspringSize;
	}
	
	public void Evolve(int generations, String championFilename) {
		InitializePopulation();
		Evaluation();
		for(int i = 0; i < generations; ++i) {
			// Perform generation step			
			Replacement();
			Evaluation();
			// Save best individual
			IMCGenotype best = sortedPopulation.get(0);
			best.BuildPhenotype().getMap().SaveToFile(championFilename);
			// Stats
			System.out.println("Generation: " + i + " Best fitness: " + bestFitness + " Avg fitness: " + avgFitness + " Worst fitness: " + worstFitness);			
		}
	}
	
	private void InitializePopulation() {
		population = new ArrayList<IMCGenotype>();
		for(int i = 0; i < populationSize; ++i) {
			IMCGenotype genotype = new IMCGenotype();
			genotype.BuildRandom(initMutationFactorLowerBound, initMutationFactorUpperBound, initGeneMutationChance);
			population.add(genotype);
		}
	}
		
	private void Evaluation() {
		sortedPopulation = new LinkedList<IMCGenotype>();
		avgFitness = 0;
		for(int i = 0; i < population.size(); ++i) {
			IMCGenotype current = population.get(i);
			InfluenceMapController imController = current.BuildPhenotype();
			double score = runExperiment(imController, new StarterGhosts(), evaluationTrials);
			current.setScore(score);
			avgFitness += score;
			// Sort by score
			boolean added = false;
			for(int j = 0; j < sortedPopulation.size(); ++j) {
				if(current.getScore() >= sortedPopulation.get(j).getScore()) {
					sortedPopulation.add(j, current);
					added = true;
					break;
				}
			}
			if(!added) {
				sortedPopulation.add(current);
			}			
		}
		// Stats
		avgFitness = avgFitness / (double)populationSize;
		bestFitness = sortedPopulation.get(0).getScore();
		worstFitness = sortedPopulation.get(sortedPopulation.size() - 1).getScore();
	}
	
	public double runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials)
    {
    	double avgScore=0;
    	
    	Random rnd=new Random(0);
		Game game;
		
		for(int i=0;i<trials;i++)
		{
			game=new Game(rnd.nextLong());
			
			while(!game.gameOver())
			{
		        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
			}
			
			avgScore+=game.getScore();
		}
		avgScore = avgScore / trials;
		return avgScore;
    }
	
	private void Selection() {
		// Remove worst individuals
		for(int i = 0; i < offspringSize; ++i) {
			sortedPopulation.removeLast();
		}		
	}
	
	private void Replacement() {		
		Random rng = new Random();		
		LinkedList<IMCGenotype> newPopulation = new LinkedList<IMCGenotype>();
		// Create new individuals
		for(int i = 0; i < offspringSize; ++i) {
			// Crossover
			int parent1Index = sampleBoundedHalfNormal(rng, populationSize);
			int parent2Index = sampleBoundedHalfNormal(rng, populationSize);
			IMCGenotype parent1 = sortedPopulation.get(parent1Index);
			IMCGenotype parent2 = sortedPopulation.get(parent2Index);
			IMCGenotype child = (IMCGenotype) parent1.Crossover(parent2);
			// Mutation
			if(rng.nextDouble() < mutationChance) {
				child.Mutate(mutationFactorLowerBound, mutationFactorUpperBound, geneMutationChance);
			}
			// Add to new population
			newPopulation.add(child);
		}
		// Remove worst individuals from sorted population
		Selection();
		// Add elite to new population
		newPopulation.addAll(sortedPopulation);
		// Clear population
		population.clear();
		// Shuffle population to remove any bias from ordering due to algorithm		
		while(newPopulation.size() > 0) {
			IMCGenotype g = newPopulation.remove(rng.nextInt(newPopulation.size()));
			population.add(g);
		}
	}
	
	private int sampleBoundedHalfNormal(Random rng, int bound) {
		// Debug
		int counter = 0;
		
		int index = 0;
		// 3 times the standard deviation ~ 99.7% of population
		double sdx3 = 3.0;
		// Sample until we get a valid result
		do {
			double sample = Math.abs(rng.nextGaussian()) / sdx3; // ~ range [0, 1]
			index = (int)(sample * (bound - 1)); // ~ range [0, bound)
			counter++;
			if(counter > 2) {
				System.out.println("sampleBoundedHalfNormal required " + counter + " attempts");
			}
		} while(index >= bound);
		
		// Debug
		if(counter > 2) {
			System.out.println("sampleBoundedHalfNormal required " + counter + " attempts");
		}
		
		return index;
	}

}
