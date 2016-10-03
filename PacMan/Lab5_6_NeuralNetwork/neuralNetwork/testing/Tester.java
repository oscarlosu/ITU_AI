package neuralNetwork.testing;

import java.util.ArrayList;

import neuralNetwork.ActivationFunction;
import neuralNetwork.Connection;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;
import neuralNetwork.Sigmoid;
import neuralNetwork.training.Backpropagation;
import neuralNetwork.training.TrainingTuple;
import neuralNetwork.training.WeightUpdateMode;

public class Tester {
	public static void main(String[] args) {
		
		NeuralNetwork nn = BuildExampleNN();
		// Forward propagation
		System.out.println("Forward propagation");
		Evaluate(nn);
		// Backpropagation
		System.out.println("Backpropagation");
		Train(nn);
		// Evaluate again
		System.out.println("Evaluation after backpropagation");
		Evaluate(nn);
		// Save to json file
		System.out.println("Save to file: myData/MyNN.json");
		nn.Save("MyNN.json");
		
		System.out.println("Load from file: myData/MyNN.json");
		// Delete
		nn = null;
		// Load from file
		nn = NeuralNetwork.Load("MyNN.json");
		// Evaluate loaded NN
		System.out.println("Evaluate after load");
		Evaluate(nn);
	}
	
	public static NeuralNetwork BuildExampleNN() {
		NeuralNetwork nn = new NeuralNetwork();
		
		ActivationFunction activation = new Sigmoid();
		// Neurons
		Neuron n1 = new Neuron(activation);		
		Neuron n2 = new Neuron(activation);
		Neuron n3 = new Neuron(activation);
		Neuron n4 = new Neuron(activation);
		n4.setBias(-0.4);
		Neuron n5 = new Neuron(activation);
		n5.setBias(0.2);
		Neuron n6 = new Neuron(activation);
		n6.setBias(0.1);
		// Connections
		// 1 - 4
		Connection c = new Connection(n1, n4, 0.2);
		nn.addConnection(c);
		n1.getOutputs().add(c);
		n4.getInputs().add(c);		
		// 1 - 5
		c = new Connection(n1, n5, -0.3);
		nn.addConnection(c);
		n1.getOutputs().add(c);
		n5.getInputs().add(c);		
		// 2 - 4
		c = new Connection(n2, n4, 0.4);
		nn.addConnection(c);
		n2.getOutputs().add(c);
		n4.getInputs().add(c);		
		// 2 - 5
		c = new Connection(n2, n5, 0.1);
		nn.addConnection(c);
		n2.getOutputs().add(c);
		n5.getInputs().add(c);		
		// 3 - 4
		c = new Connection(n3, n4, -0.5);
		nn.addConnection(c);
		n3.getOutputs().add(c);
		n4.getInputs().add(c);		
		// 3 - 5
		c = new Connection(n3, n5, 0.2);
		nn.addConnection(c);
		n3.getOutputs().add(c);
		n5.getInputs().add(c);		
		// 4 - 6
		c = new Connection(n4, n6, -0.3);
		nn.addConnection(c);
		n4.getOutputs().add(c);
		n6.getInputs().add(c);		
		// 5 - 6
		c = new Connection(n5, n6, -0.2);
		nn.addConnection(c);
		n5.getOutputs().add(c);
		n6.getInputs().add(c);		
		
		nn.addInputNeuron(n1);
		nn.addInputNeuron(n2);
		nn.addInputNeuron(n3);
		
		nn.addHiddenNeuron(n4);
		nn.addHiddenNeuron(n5);
		
		nn.addOutputNeuron(n6);
		
		return nn;
	}
	
	public static void Evaluate(NeuralNetwork nn) {
		ArrayList<Double> inputValues = new ArrayList<Double>();
		inputValues.add(1.0);
		inputValues.add(0.0);
		inputValues.add(1.0);
		System.out.println("Input: " + inputValues);
		ArrayList<Double> outputValues = nn.Evaluate(inputValues);
		System.out.println("Output: " + outputValues);
	}
	
	public static void Train(NeuralNetwork nn) {
		Backpropagation trainer = new Backpropagation(WeightUpdateMode.CaseUpdate, 1, 0);
		// Training data
		ArrayList<TrainingTuple> data = new ArrayList<TrainingTuple>();
		// Tuple 1
		// Input
		ArrayList<Double> inputValues = new ArrayList<Double>();
		inputValues.add(1.0);
		inputValues.add(0.0);
		inputValues.add(1.0);
		// Output
		ArrayList<Double> outputValues = new ArrayList<Double>();
		outputValues.add(1.0);
		
		data.add(new TrainingTuple(inputValues, outputValues));
		// Train
		trainer.train(nn, data , 0.9);
	}
}
