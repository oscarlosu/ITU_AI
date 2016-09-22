package neuralNetwork;

import com.google.gson.annotations.Expose;

public class Connection {
	private Neuron from;
	@Expose
	private int fromId;
	private Neuron to;
	@Expose
	private int toId;
	@Expose
	private double weight;
	private double deltaWeight;
	
	

	public Connection(Neuron from, Neuron to, double weight) {
		super();
		this.from = from;
		this.fromId = from.getId();
		this.to = to;
		this.toId = to.getId();
		this.weight = weight;
		this.deltaWeight = 0;
	}
	
	public void UpdateNeuronIds() {
		fromId = from.getId();
		toId = to.getId();
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

	public double getDeltaWeight() {
		return deltaWeight;
	}

	public void setDeltaWeight(double deltaWeight) {
		this.deltaWeight = deltaWeight;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}
	
	
	
}
