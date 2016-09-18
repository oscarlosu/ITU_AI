package neuralNetwork;

import java.util.ArrayList;

public class Neuron {
	private ArrayList<Connection> inputs;
	private ArrayList<Connection> outputs;
	private ActivationFunction activation;
	private double preActivationValue;
	private double value;
	private double bias;
	private boolean updatedValue;
	private double error;
	private double deltaBias;
	
	public Neuron(ArrayList<Connection> inputs, ArrayList<Connection> outputs, ActivationFunction activation) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
		this.activation = activation;
		this.preActivationValue = 0;
		this.value = 0;
		this.bias = 0;
		this.updatedValue = false;
		this.error = 0;
		this.deltaBias = 0;
	}
	
	public Neuron(ActivationFunction activation) {
		super();
		this.inputs = new ArrayList<Connection>();
		this.outputs = new ArrayList<Connection>();
		this.activation = activation;
		this.preActivationValue = 0;
		this.value = 0;
		this.bias = 0;
		this.updatedValue = false;
		this.error = 0;
		this.deltaBias = 0;
	}

	public double evaluate() {
		// Neurons without input connections are input neurons and it's value should be set manually
		if(inputs != null && inputs.size() > 0 && !updatedValue) {
			double input = 0;
			// Weighted sum of input neurons
			for(int i = 0; i < inputs.size(); ++i) {
				Connection c = inputs.get(i);
				input += c.getWeight() * c.getFrom().evaluate();
			}
			// Plus bias
			input += bias;
			// Save input
			preActivationValue = input;
			// Filtered by activation function
			value = activation.value(input);
			// Set flag
			updatedValue = true;
			System.out.println(value);
		}
		
		return value;
	}
	
	public void PropagateUpdatedValueStatus(boolean updatedValue) {
		// Set own
		this.updatedValue = updatedValue;
		// Recursively propagate through output connections
		if(outputs != null) {
			for(Connection c : outputs) {
				c.getTo().PropagateUpdatedValueStatus(updatedValue);
			}
		}		
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

	public boolean isUpdatedValue() {
		return updatedValue;
	}

	public void setUpdatedValue(boolean updatedValue) {
		this.updatedValue = updatedValue;
	}

	public ArrayList<Connection> getOutputs() {
		return outputs;
	}

	public void setOutputs(ArrayList<Connection> outputs) {
		this.outputs = outputs;
	}

	public double getPreActivationValue() {
		return preActivationValue;
	}

	public void setPreActivationValue(double preActivationValue) {
		this.preActivationValue = preActivationValue;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getDeltaBias() {
		return deltaBias;
	}

	public void setDeltaBias(double deltaBias) {
		this.deltaBias = deltaBias;
	}
	
	
	
}
