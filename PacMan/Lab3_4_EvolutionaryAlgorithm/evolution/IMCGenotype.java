package evolution;

import java.util.ArrayList;
import java.util.Random;

import controller.InfluenceMapController;
import influenceMap.InfluenceMap;

public class IMCGenotype extends Genotype<InfluenceMapController>{
	private ArrayList<Double> genes;
	
	public IMCGenotype() {
		genes = new ArrayList<Double>();
	}

	@Override
	public void BuildRandom(double lower, double upper, double geneMutationChance) {
		genes = new ArrayList<Double>();
		genes.add(InfluenceMap.defaultMaxGameCost);
		genes.add(InfluenceMap.defaultEdibleGhostCost);
		genes.add(InfluenceMap.defaultEdibleGhostCost);
		genes.add(InfluenceMap.defaultEdibleGhostInfluenceDecay);
		genes.add(InfluenceMap.defaultPowerPillCost);
		genes.add(InfluenceMap.defaultPowerPillCostGrowth);
		genes.add(InfluenceMap.defaultPillCost);
		genes.add(InfluenceMap.defaultPillCostDecline);
		genes.add(InfluenceMap.defaultGhostCost);
		genes.add(InfluenceMap.defaultGhostInfluenceDecay);
		
		Mutate(lower, upper, geneMutationChance);
	}
	@Override
	public void BuildGenotype(InfluenceMapController phenotype) {
		InfluenceMap map = phenotype.getMap();
		genes = new ArrayList<Double>();
		genes.add(map.getMaxGameCost());
		genes.add(map.getEdibleGhostCost());
		genes.add(map.getEdibleGhostInfluenceDecay());
		genes.add(map.getPowerPillCost());
		genes.add(map.getPowerPillCostGrowth());
		genes.add(map.getPillCost());
		genes.add(map.getPillCostGrowth());
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
	public InfluenceMapController BuildPhenotype() {
		InfluenceMap map = new InfluenceMap(genes.get(0), genes.get(1), genes.get(2), genes.get(3), genes.get(4), genes.get(5), genes.get(6), genes.get(7), genes.get(8));
		return new InfluenceMapController(map);
	}
	@Override
	public void Mutate(double lower, double upper, double geneMutationChance) {
		Random rng = new Random();
		for(int i = 0; i < genes.size(); ++i) {
			if(rng.nextDouble() < geneMutationChance) {
				// Mutation factor from gaussian distribution in range [lower, upper]
				double originalValue = genes.get(i);
				double mutationFactor = rng.nextGaussian();
				mutationFactor = Math.abs(lower + (mutationFactor + 1) * ((upper - lower) / 2.0));
				double newValue = mutationFactor * originalValue;
				genes.set(i, newValue);
			}			
		}
		
	}
	@Override
	public Genotype<InfluenceMapController> Crossover(Genotype<InfluenceMapController> other) {
		IMCGenotype child = new IMCGenotype();
		Random rng = new Random();
		for(int i = 0; i < genes.size(); ++i) {
				double crossoverFactor = (rng.nextGaussian() + 1) /2.0;
				double newValue = Lerp(this.genes.get(i), other.GetGenes().get(i), crossoverFactor);
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
