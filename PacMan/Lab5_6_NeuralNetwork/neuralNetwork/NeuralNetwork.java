package neuralNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import pacman.game.util.IO;

public class NeuralNetwork {
	@Expose
	private ArrayList<Neuron> inputLayer;	
	@Expose
	private ArrayList<Neuron> hiddenLayer;
	@Expose
	private ArrayList<Neuron> outputLayer;
	@Expose
	private ArrayList<Connection> connections;
	
	private HashMap<Integer, Neuron> neuronLookup;
 	
//	public NeuralNetwork(ArrayList<Neuron> input, ArrayList<Neuron> output, ArrayList<Neuron> hidden) {
//		super();
//		this.inputLayer = input;
//		this.outputLayer = output;
//		this.hiddenLayer = hidden;
//	}

	public NeuralNetwork() {
		super();
		inputLayer = new ArrayList<Neuron>();
		outputLayer = new ArrayList<Neuron>();
		hiddenLayer = new ArrayList<Neuron>();
		connections = new ArrayList<Connection>();
		neuronLookup = new HashMap<Integer, Neuron>();
	}
	
	public void InitializeRandomFullyConnected(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
		ActivationFunction af = new Sigmoid();
		Random rng = new Random();
		// Input layer
		for(int i = 0; i < inputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			//n.setBias((rng.nextDouble() * 2) - 1);
			n.setBias(0);
			addInputNeuron(n);
		}
		// Hidden layer
		for(int i = 0; i < hiddenLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			addHiddenNeuron(n);
		}
		// Output layer
		for(int i = 0; i < outputLayerSize; ++i) {
			Neuron n = new Neuron(af);
			n.setBias((rng.nextDouble() * 2) - 1);
			addOutputNeuron(n);
		}
		// Connections
		// Input - Hidden
		for(Neuron from: inputLayer) {
			for(Neuron to: hiddenLayer) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				connections.add(c);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
		// Hidden - Output
		for(Neuron from: hiddenLayer) {
			for(Neuron to: outputLayer) {
				Connection c = new Connection(from, to, (rng.nextDouble() * 2) - 1);
				connections.add(c);
				from.getOutputs().add(c);
				to.getInputs().add(c);
			}
		}
	}
	
	public void CleanTrainingValues() {
		// Clean delta weights and delta biases
		for(Connection c: connections) {
			c.setDeltaWeight(0);
		}
		for(Neuron n: inputLayer) {
			n.setDeltaBias(0);
		}
		for(Neuron n: hiddenLayer) {
			n.setDeltaBias(0);
		}
		for(Neuron n: outputLayer) {
			n.setDeltaBias(0);
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
	
	public void Save(String filename) {
		// Ensure that all the connections have the real neuron ids.
		for(Connection c: connections) {
			c.UpdateNeuronIds();
		}		
		// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = gson.toJson(this);
		// Save to file in myData/
		IO.saveFile(filename, json, false);
	}
	
	public static NeuralNetwork Load(String filename) {
		// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = IO.loadFile(filename);
		NeuralNetwork loadedNN = gson.fromJson(json, NeuralNetwork.class);
		loadedNN.Link();
		return loadedNN;
	}
	
	public void Link() {
		// Fill neuronLookup
		for(Neuron n: inputLayer) {
			neuronLookup.put(n.getId(), n);
		}
		for(Neuron n: hiddenLayer) {
			neuronLookup.put(n.getId(), n);
		}
		for(Neuron n: outputLayer) {
			neuronLookup.put(n.getId(), n);
		}
		// Link connections and neurons
		for(Connection c: connections) {
			int fromId = c.getFromId();
			int toId = c.getToId();
			Neuron from = neuronLookup.get(fromId);
			Neuron to = neuronLookup.get(toId);
			// Link connections
			c.setFrom(from);
			c.setTo(to);
			// Link neurons
			from.getOutputs().add(c);
			to.getInputs().add(c);
		}
	}
	
	
	public void addInputNeuron(Neuron n) {
		int id = inputLayer.size() + hiddenLayer.size() + outputLayer.size();
		n.setId(id);
		inputLayer.add(n);
	}
	
	public void addHiddenNeuron(Neuron n) {
		int id = inputLayer.size() + hiddenLayer.size() + outputLayer.size();
		n.setId(id);
		hiddenLayer.add(n);
	}
	
	public void addOutputNeuron(Neuron n) {
		int id = inputLayer.size() + hiddenLayer.size() + outputLayer.size();
		n.setId(id);
		outputLayer.add(n);
	}
	
	public Neuron getInputNeuron(int index) {
		return inputLayer.get(index);
	}
	
	public int getInputLayerSize() {
		return inputLayer.size();
	}
	
	public Neuron getHiddenNeuron(int index) {
		return hiddenLayer.get(index);
	}
	
	public int getHiddenLayerSize() {
		return hiddenLayer.size();
	}
	
	public Neuron getOutputNeuron(int index) {
		return outputLayer.get(index);
	}
	
	public int getOutputLayerSize() {
		return outputLayer.size();
	}
	
	public void addConnection(Connection c) {
		connections.add(c);
	}
	
	public Connection getConnection(int index) {
		return connections.get(index);
	}
}
