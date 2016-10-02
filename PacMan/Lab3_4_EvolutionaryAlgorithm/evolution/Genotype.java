package evolution;

import java.util.ArrayList;

import tacticalAStar.TASController;

public abstract class Genotype<T> {
	private double score;
	
	public abstract void BuildRandom(double lower, double upper, double geneMutationChance);
	public abstract void BuildGenotype(T phenotype);
	
	public abstract void BuildGenotype(ArrayList<Double> genes);
	
	public abstract T BuildPhenotype();
	
	public abstract void Mutate(double lower, double upper, double geneMutationChance);
	
	public abstract Genotype<T> Crossover(Genotype<T> other);
	
	public abstract ArrayList<Double> GetGenes();
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
	
}
