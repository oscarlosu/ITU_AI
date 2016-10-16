package tacticalAStar.evolution;

import java.util.ArrayList;
import java.util.Random;

import tacticalAStar.TASController;
import tacticalAStar.TacticalAStar;

public class TASGenotype extends Genotype<TASController>{
	private ArrayList<Double> genes;
	
	public TASGenotype() {
		genes = new ArrayList<Double>();
	}

	@Override
	public void BuildRandom(double lower, double upper, double geneMutationChance) {
		genes = new ArrayList<Double>();
		genes.add(TacticalAStar.defaultMaxGameCost);
		genes.add(TacticalAStar.defaultEdibleGhostCost);
		genes.add(TacticalAStar.defaultEdibleGhostInfluenceDecay);
		genes.add(TacticalAStar.defaultEdibleGhostDistanceDecline);
		genes.add(TacticalAStar.defaultPowerPillCost);
		genes.add(TacticalAStar.defaultPpPenalty);
		genes.add(TacticalAStar.defaultPpReward);
		genes.add(TacticalAStar.defaultPowerPillCostGrowth);
		genes.add(TacticalAStar.defaultPillCost);
		genes.add(TacticalAStar.defaultPillCostDecline);
		genes.add(TacticalAStar.defaultGhostCost);
		genes.add(TacticalAStar.defaultGhostInfluenceDecay);
		
		Mutate(lower, upper, geneMutationChance);
	}
	@Override
	public void BuildGenotype(TASController phenotype) {
		TacticalAStar map = phenotype.getMap();
		genes = new ArrayList<Double>();
		genes.add(map.getMaxGameCost());
		genes.add(map.getEdibleGhostCost());
		genes.add(map.getEdibleGhostInfluenceDecay());
		genes.add(map.getEdibleGhostDistanceDecline());
		genes.add(map.getPowerPillCost());
		genes.add(map.getPpPenalty());
		genes.add(map.getPpReward());
		genes.add(map.getPowerPillCostGrowth());
		genes.add(map.getPillCost());
		genes.add(map.getPillCostDecline());
		genes.add(map.getGhostCost());
		genes.add(map.getGhostInfluenceDecay());		
	}
	
	@Override
	public void BuildGenotype(ArrayList<Double> genes) {
		genes = new ArrayList<Double>();
		for(int i = 0; i < genes.size(); ++i) {
			genes.add(genes.get(i));
		}		
	}
	@Override
	public TASController BuildPhenotype() {
		TacticalAStar map = new TacticalAStar(genes.get(0), genes.get(1), genes.get(2), genes.get(3), genes.get(4), genes.get(5), genes.get(6), genes.get(7), genes.get(8), genes.get(9), genes.get(10), genes.get(11));
		return new TASController(map);
	}
	@Override
	public void Mutate(double lower, double upper, double geneMutationChance) {
		Random rng = new Random();
		for(int i = 0; i < genes.size(); ++i) {
			if(rng.nextDouble() < geneMutationChance) {
				// Mutation factor from half-gaussian distribution in range [lower, upper]
				double originalValue = genes.get(i);
//				double mutationFactor = Math.abs(rng.nextGaussian());
//				mutationFactor = Math.abs(lower + mutationFactor * (upper - lower));
				double mutationFactor = lower + ArtificialEvolution.sampleBoundedHalfNormal(rng, upper - lower);
				double newValue = mutationFactor * originalValue;
				genes.set(i, newValue);
			}			
		}
		
	}
	@Override
	public Genotype<TASController> Crossover(Genotype<TASController> other) {
		TASGenotype child = new TASGenotype();
		Random rng = new Random();
		for(int i = 0; i < genes.size(); ++i) {
				double newValue = rng.nextDouble() < 0.5 ? this.genes.get(i) : other.GetGenes().get(i);
				child.GetGenes().add(newValue);		
		}
		return child;
	}
	
	private double Lerp(double a, double b, double t) {
		return t * a + (1 - t) * b;
	}
	
	@Override
	public ArrayList<Double> GetGenes() {
		return genes;
	}

	
	
	
	
}
