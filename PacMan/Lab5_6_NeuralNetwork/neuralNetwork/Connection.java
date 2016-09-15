package neuralNetwork;

public class Connection {
	private Neuron from;
	private Neuron to;
	private double weight;
	private double delta_weight;
	
	

	public Connection(Neuron from, Neuron to, double weight) {
		super();
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.delta_weight = 0;
	}

	public Neuron getFrom() {
		return from;
	}

	public void setFrom(Neuron from) {
		this.from = from;
	}

	public Neuron getTo() {
		return to;
	}

	public void setTo(Neuron to) {
		this.to = to;
	}

	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getDelta_weight() {
		return delta_weight;
	}

	public void setDelta_weight(double delta_weight) {
		this.delta_weight = delta_weight;
	}
	
	
	
}
