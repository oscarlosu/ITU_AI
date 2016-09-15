package neuralNetwork;

import java.util.ArrayList;

public class Neuron {
	private ArrayList<Connection> inputs;
	private ArrayList<Connection> output;
	private ActivationFunction activation;
	private double value;
	private double bias;
	
	public Neuron(ArrayList<Connection> inputs, ArrayList<Connection> output, ActivationFunction activation) {
		super();
		this.inputs = inputs;
		this.output = output;
		this.activation = activation;
		this.value = 0;
		this.bias = 0;
	}
	
	public Neuron(ActivationFunction activation) {
		super();
		this.inputs = new ArrayList<Connection>();
		this.output = new ArrayList<Connection>();
		this.activation = activation;
		this.value = 0;
		this.bias = 0;
	}

	public double evaluate() {
		// Neurons without input connections are input neurons and it's value should be set manually
		if(inputs != null || inputs.size() > 0) {
			double input = 0;
			// Weighted sum of input neurons
			for(int i = 0; i < inputs.size(); ++i) {
				Connection c = inputs.get(i);
				input += c.getWeight() * c.getFrom().evaluate();
			}
			// Plus bias
			input += bias;
			// Filtered by activation function
			value = activation.value(input);
		}		
		return value;
	}

	public ArrayList<Connection> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<Connection> inputs) {
		this.inputs = inputs;
	}

	public ActivationFunction getActivation() {
		return activation;
	}

	public void setActivation(ActivationFunction activation) {
		this.activation = activation;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}
	
}
