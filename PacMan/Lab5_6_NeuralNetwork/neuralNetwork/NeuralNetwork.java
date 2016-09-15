package neuralNetwork;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork {
	private ArrayList<Neuron> input;
	private ArrayList<Neuron> output;
	private ArrayList<Neuron> hidden;
	
	public NeuralNetwork(ArrayList<Neuron> input, ArrayList<Neuron> output, ArrayList<Neuron> hidden) {
		super();
		this.input = input;
		this.output = output;
		this.hidden = hidden;
	}

	public NeuralNetwork() {
		super();
		input = new ArrayList<Neuron>();
		output = new ArrayList<Neuron>();
		hidden = new ArrayList<Neuron>();
	}
	
	public void InitializeRandomFullyConnected(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
		ActivationFunction af = new Sigmoid();
		Random rng = new Random();
		// Input layer
		for(int i = 0; i < inputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			input.add(n);
		}
		// Hidden layer
		for(int i = 0; i < hiddenLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			hidden.add(n);
		}
		// Output layer
		for(int i = 0; i < outputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			output.add(n);
		}
		// Connections
		// Input - Hidden
		for(Neuron from: input) {
			for(Neuron to: hidden) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
		// Hidden - Output
		for(Neuron from: hidden) {
			for(Neuron to: output) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
	}
	
	public ArrayList<Double> Evaluate(ArrayList<Double> inputValues) {
		// Mark cached neuron values as old
		for(Neuron n: input) {
			n.PropagateUpdatedValueStatus(false);
		}
		// Initialize input layer
		for(int i = 0; i < inputValues.size(); ++i) {
			input.get(i).setValue(inputValues.get(i));
		}
		// Evaluate recursively from output layer
		ArrayList<Double> outputValues = new ArrayList<Double>();
		for(Neuron o: output) {
			outputValues.add(o.evaluate());
		}
		return outputValues;
	}

	public ArrayList<Neuron> getInput() {
		return input;
	}

	public void setInput(ArrayList<Neuron> input) {
		this.input = input;
	}

	public ArrayList<Neuron> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<Neuron> output) {
		this.output = output;
	}

	public ArrayList<Neuron> getHidden() {
		return hidden;
	}

	public void setHidden(ArrayList<Neuron> hidden) {
		this.hidden = hidden;
	}
}
