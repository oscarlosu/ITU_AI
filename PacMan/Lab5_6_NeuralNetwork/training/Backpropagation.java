package training;

import java.util.ArrayList;

import neuralNetwork.Connection;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;

public class Backpropagation {
	private WeightUpdateMode updateMode;
	private int maxEpochs;
	private double deltaWeightTerminationThreshold;
	
	public Backpropagation(WeightUpdateMode updateMode, int maxEpochs, double deltaWeightTerminationThreshold) {
		this.updateMode = updateMode;
		this.maxEpochs = maxEpochs;
		this.deltaWeightTerminationThreshold = deltaWeightTerminationThreshold;
	}
	
	public void train(NeuralNetwork nn, ArrayList<TrainingTuple> data, double learningRate) {
		boolean terminate = false;
		int epoch = 1;
		while(!terminate) {
			// Epoch
			// Will stop unless maxEpochs have been computed and some deltaWeights are bigger than the threshold
			terminate = true;
			for(TrainingTuple tuple: data) {
				// Propagate inputs forward
				ArrayList<Double> outputValues = nn.Evaluate(tuple.inputValues);
				// Backpropagate errors
				// Output layer
				for(int i = 0; i < nn.getOutput().size(); ++i) {
					// Compute error
					Neuron n = nn.getOutput().get(i);
					double error = n.getActivation().derivative(n.getPreActivationValue()) * (tuple.outputValues.get(i) - n.getValue());
					n.setError(error);
				}
				// Hidden layer
				for(Neuron n: nn.getHidden()) {
					// Compute error
					double outputErrorSum = 0;
					for(Connection c: n.getOutputs()) {
						outputErrorSum += c.getTo().getError() * c.getWeight();
					}
					double error = n.getActivation().derivative(n.getPreActivationValue()) * outputErrorSum;
					n.setError(error);
				}
				// Update weights
				// Input - Hidden
				for(Neuron n: nn.getInput()) {
					for(Connection c: n.getOutputs()) {
						double deltaWeight = (learningRate / (double)epoch) * c.getTo().getError() * c.getFrom().getValue();
						// Save deltaWeight for Epoch Update and termination
						c.setDeltaWeight(deltaWeight);
						// Update now if update mode is Case Update
						if(updateMode == WeightUpdateMode.CaseUpdate) {
							c.setWeight(c.getWeight() + deltaWeight);
						}
					}
				}
				// Hidden - Output
				for(Neuron n: nn.getHidden()) {
					for(Connection c: n.getOutputs()) {
						double deltaWeight = (learningRate / (double)epoch) * c.getTo().getError() * c.getFrom().getValue();
						// Save deltaWeight for Epoch Update and termination
						c.setDeltaWeight(deltaWeight);
						// Update now if update mode is Case Update
						if(updateMode == WeightUpdateMode.CaseUpdate) {
							c.setWeight(c.getWeight() + deltaWeight);
						}
					}
				}
				// Update bias				
				for(Neuron n: nn.getInput()) {
					double deltaBias = (learningRate / (double)epoch) * n.getError();
					n.setDeltaBias(deltaBias);
					if(updateMode == WeightUpdateMode.CaseUpdate) {
						n.setBias(n.getBias() + deltaBias);
					}
				}
				for(Neuron n: nn.getHidden()) {
					double deltaBias = (learningRate / (double)epoch) * n.getError();
					n.setDeltaBias(deltaBias);
					if(updateMode == WeightUpdateMode.CaseUpdate) {
						n.setBias(n.getBias() + deltaBias);
					}
				}
				for(Neuron n: nn.getOutput()) {
					double deltaBias = (learningRate / (double)epoch) * n.getError();
					n.setDeltaBias(deltaBias);
					if(updateMode == WeightUpdateMode.CaseUpdate) {
						n.setBias(n.getBias() + deltaBias);
					}
				}				
			}
			// Update weights and bias if Epoch Update
			if(updateMode == WeightUpdateMode.EpochUpdate) {
				// Update weights
				// Input - Hidden
				for(Neuron n: nn.getInput()) {
					for(Connection c: n.getOutputs()) {
						c.setWeight(c.getWeight() + c.getDeltaWeight());
						if(c.getDeltaWeight() > deltaWeightTerminationThreshold) {
							terminate = false;
						}
					}
				}
				// Hidden - Output
				for(Neuron n: nn.getHidden()) {
					for(Connection c: n.getOutputs()) {
						c.setWeight(c.getWeight() + c.getDeltaWeight());
						if(c.getDeltaWeight() > deltaWeightTerminationThreshold) {
							terminate = false;
						}
					}
				}
				
				// Update bias
				for(Neuron n: nn.getInput()) {
					n.setBias(n.getBias() + n.getDeltaBias());
				}
				for(Neuron n: nn.getHidden()) {
					n.setBias(n.getBias() + n.getDeltaBias());
				}
				for(Neuron n: nn.getOutput()) {
					n.setBias(n.getBias() + n.getDeltaBias());
				}	
			}
			// Check termination conditions
			if(epoch > maxEpochs) {
				terminate = true;
			}
		}
	}

}
