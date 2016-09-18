package neuralNetwork;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork {
	private ArrayList<Neuron> inputLayer;
	private ArrayList<Neuron> outputLayer;
	private ArrayList<Neuron> hiddenLayer;
	
	public NeuralNetwork(ArrayList<Neuron> input, ArrayList<Neuron> output, ArrayList<Neuron> hidden) {
		super();
		this.inputLayer = input;
		this.outputLayer = output;
		this.hiddenLayer = hidden;
	}

	public NeuralNetwork() {
		super();
		inputLayer = new ArrayList<Neuron>();
		outputLayer = new ArrayList<Neuron>();
		hiddenLayer = new ArrayList<Neuron>();
	}
	
	public void InitializeRandomFullyConnected(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
		ActivationFunction af = new Sigmoid();
		Random rng = new Random();
		// Input layer
		for(int i = 0; i < inputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			//n.setBias((rng.nextDouble() * 2) - 1);
			n.setBias(0);
			inputLayer.add(n);
		}
		// Hidden layer
		for(int i = 0; i < hiddenLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			hiddenLayer.add(n);
		}
		// Output layer
		for(int i = 0; i < outputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			outputLayer.add(n);
		}
		// Connections
		// Input - Hidden
		for(Neuron from: inputLayer) {
			for(Neuron to: hiddenLayer) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
		// Hidden - Output
		for(Neuron from: hiddenLayer) {
			for(Neuron to: outputLayer) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
	}
	
	public ArrayList<Double> Evaluate(ArrayList<Double> inputValues) {
		// Mark cached neuron values as old
		for(Neuron n: inputLayer) {
			n.PropagateUpdatedValueStatus(false);
		}
		// Initialize input layer
		for(int i = 0; i < inputLayer.size(); ++i) {
			inputLayer.get(i).setValue(inputValues.get(i));
		}
		// Evaluate recursively from output layer
		ArrayList<Double> outputValues = new ArrayList<Double>();
		for(Neuron o: outputLayer) {
			outputValues.add(o.evaluate());
		}
		return outputValues;
	}

	public ArrayList<Neuron> getInput() {
		return inputLayer;
	}

	public void setInput(ArrayList<Neuron> input) {
		this.inputLayer = input;
	}

	public ArrayList<Neuron> getOutput() {
		return outputLayer;
	}

	public void setOutput(ArrayList<Neuron> output) {
		this.outputLayer = output;
	}

	public ArrayList<Neuron> getHidden() {
		return hiddenLayer;
	}

	public void setHidden(ArrayList<Neuron> hidden) {
		this.hiddenLayer = hidden;
	}
}
