package neuralNetwork.training;

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
			// Stats
			double avgError = 0;
			double avgDeltaWeightBias = 0;
			// Ensure delta weights and delta biases are set to 0
			nn.CleanTrainingValues();
			// Will stop unless maxEpochs have been computed and some deltaWeights are bigger than the threshold
			terminate = true;
			for(TrainingTuple tuple: data) {
				// Propagate inputs forward
				ArrayList<Double> outputValues = nn.Evaluate(tuple.getInputValues());
				// Backpropagate errors
				// Output layer
				for(int i = 0; i < nn.getOutputLayerSize(); ++i) {
					// Compute error
					Neuron n = nn.getOutputNeuron(i);
					double error = n.getActivation().derivative(n.getPreActivationValue()) * (tuple.getOutputValues().get(i) - n.getValue());
					n.setError(error);
					avgError += Math.abs(error);
					//System.out.println("error " + error);
				}
				// Hidden layer
				for(int i = 0; i < nn.getHiddenLayerSize(); ++i) {
					// Compute error
					Neuron n = nn.getHiddenNeuron(i);
					double outputErrorSum = 0;
					for(Connection c: n.getOutputs()) {
						outputErrorSum += c.getTo().getError() * c.getWeight();
					}
					double error = n.getActivation().derivative(n.getPreActivationValue()) * outputErrorSum;
					n.setError(error);
					avgError += Math.abs(error);
					//System.out.println("error " + error);
				}
				// Update weights
				// Input - Hidden
				for(int i = 0; i < nn.getInputLayerSize(); ++i) {
					Neuron n = nn.getInputNeuron(i);
					for(Connection c: n.getOutputs()) {
						double deltaWeight = (learningRate / (double)epoch) * c.getTo().getError() * c.getFrom().getValue();
						// Save deltaWeight for Epoch Update and termination
						c.setDeltaWeight(c.getDeltaWeight() + deltaWeight);
						avgDeltaWeightBias += Math.abs(deltaWeight);
						if(Math.abs(c.getDeltaWeight()) > deltaWeightTerminationThreshold) {
							terminate = false;
						}
						// Update now if update mode is Case Update
						if(updateMode == WeightUpdateMode.CaseUpdate) {
							c.setWeight(c.getWeight() + deltaWeight);
							//System.out.println("new weight " + c.getWeight());
						}
					}
				}
				// Hidden - Output
				for(int i = 0; i < nn.getHiddenLayerSize(); ++i) {
					Neuron n = nn.getHiddenNeuron(i);
					for(Connection c: n.getOutputs()) {
						double deltaWeight = (learningRate / (double)epoch) * c.getTo().getError() * c.getFrom().getValue();
						// Save deltaWeight for Epoch Update and termination
						c.setDeltaWeight(c.getDeltaWeight() + deltaWeight);
						avgDeltaWeightBias += Math.abs(deltaWeight);
						if(Math.abs(c.getDeltaWeight()) > deltaWeightTerminationThreshold) {
							terminate = false;
						}
						// Update now if update mode is Case Update
						if(updateMode == WeightUpdateMode.CaseUpdate) {
							c.setWeight(c.getWeight() + deltaWeight);
							//System.out.println("new weight " + c.getWeight());
						}
					}
				}
				// Update bias				
//				for(Neuron n: nn.getInput()) {
//					double deltaBias = (learningRate / (double)epoch) * n.getError();
//					n.setDeltaBias(deltaBias);
//					if(updateMode == WeightUpdateMode.CaseUpdate) {
//						n.setBias(n.getBias() + deltaBias);
//						System.out.println("new bias " + n.getBias());
//					}
//				}
				for(int i = 0; i < nn.getHiddenLayerSize(); ++i) {
					Neuron n = nn.getHiddenNeuron(i);
					double deltaBias = (learningRate / (double)epoch) * n.getError();
					n.setDeltaBias(n.getDeltaBias() + deltaBias);
					if(Math.abs(n.getDeltaBias()) > deltaWeightTerminationThreshold) {
						terminate = false;
					}
					if(updateMode == WeightUpdateMode.CaseUpdate) {
						n.setBias(n.getBias() + deltaBias);
						//System.out.println("new bias " + n.getBias());
					}
				}
				for(int i = 0; i < nn.getOutputLayerSize(); ++i) {
					Neuron n = nn.getOutputNeuron(i);
					double deltaBias = (learningRate / (double)epoch) * n.getError();
					n.setDeltaBias(n.getDeltaBias() + deltaBias);
					if(Math.abs(n.getDeltaBias()) > deltaWeightTerminationThreshold) {
						terminate = false;
					}
					if(updateMode == WeightUpdateMode.CaseUpdate) {
						n.setBias(n.getBias() + deltaBias);
						//System.out.println("new bias " + n.getBias());
					}
				}				
			}
			// Update weights and bias if Epoch Update
			if(updateMode == WeightUpdateMode.EpochUpdate) {
				// Update weights
				// Input - Hidden
				for(int i = 0; i < nn.getInputLayerSize(); ++i) {
					Neuron n = nn.getInputNeuron(i);
					for(Connection c: n.getOutputs()) {
						c.setWeight(c.getWeight() + c.getDeltaWeight());
						//System.out.println("new weight " + c.getWeight());
						if(c.getDeltaWeight() > deltaWeightTerminationThreshold) {
							terminate = false;
						}
					}
				}
				// Hidden - Output
				for(int i = 0; i < nn.getHiddenLayerSize(); ++i) {
					Neuron n = nn.getHiddenNeuron(i);
					for(Connection c: n.getOutputs()) {
						c.setWeight(c.getWeight() + c.getDeltaWeight());
						//System.out.println("new weight " + c.getWeight());
						if(c.getDeltaWeight() > deltaWeightTerminationThreshold) {
							terminate = false;
						}
					}
				}
				
				// Update bias
//				for(Neuron n: nn.getInput()) {
//					n.setBias(n.getBias() + n.getDeltaBias());
//					System.out.println("new bias " + n.getBias());
//				}
				for(int i = 0; i < nn.getHiddenLayerSize(); ++i) {
					Neuron n = nn.getHiddenNeuron(i);
					n.setBias(n.getBias() + n.getDeltaBias());
					//System.out.println("new bias " + n.getBias());
					if(Math.abs(n.getDeltaBias()) > deltaWeightTerminationThreshold) {
						terminate = false;
					}
				}
				for(int i = 0; i < nn.getOutputLayerSize(); ++i) {
					Neuron n = nn.getOutputNeuron(i);
					n.setBias(n.getBias() + n.getDeltaBias());
					//System.out.println("new bias " + n.getBias());
					if(Math.abs(n.getDeltaBias()) > deltaWeightTerminationThreshold) {
						terminate = false;
					}
				}	
			}
			// Check termination conditions
			if(epoch > maxEpochs) {
				terminate = true;
			}
			avgError /= data.size();
			avgDeltaWeightBias /= data.size();
			if(epoch % 100 == 0) {
				System.out.println("Epoch " + epoch + ": " + " avgError " + avgError + " avgDeltaWeightBias " + avgDeltaWeightBias);
			}			
			++epoch;
		}
	}

}
