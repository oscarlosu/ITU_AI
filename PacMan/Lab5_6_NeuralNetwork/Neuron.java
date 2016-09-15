import java.util.ArrayList;

public class Neuron {
	private ArrayList<Connection> inputs;
	private ActivationFunction activation;
	private double value;
	
	public Neuron(ArrayList<Connection> inputs, ActivationFunction activation) {
		super();
		this.inputs = inputs;
		this.activation = activation;
		this.value = 0;
	}
	
	public Neuron(ActivationFunction activation) {
		super();
		this.inputs = new ArrayList<Connection>();
		this.activation = activation;
		this.value = 0;
	}

	public double evaluate() {
		if(inputs != null || inputs.size() > 0) {
			double sum = 0;
			for(int i = 0; i < inputs.size(); ++i) {
				Connection c = inputs.get(i);
				sum += c.getWeight() * c.getFrom().evaluate();
			}
			value = activation.value(sum);
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
	
}
