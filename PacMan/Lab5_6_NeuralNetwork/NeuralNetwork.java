import java.util.ArrayList;

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
	
	public ArrayList<Double> Evaluate(ArrayList<Double> inputValues) {
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
