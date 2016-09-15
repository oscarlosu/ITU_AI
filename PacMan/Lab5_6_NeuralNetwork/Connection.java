
public class Connection {
	private Neuron from;
	private Neuron to;
	private double weight;
	
	

	public Connection(Neuron from, Neuron to, double weight) {
		super();
		this.from = from;
		this.to = to;
		this.weight = weight;
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
	
}
