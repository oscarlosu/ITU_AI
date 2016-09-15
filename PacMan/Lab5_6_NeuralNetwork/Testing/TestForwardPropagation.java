package Testing;

import java.util.ArrayList;

import neuralNetwork.ActivationFunction;
import neuralNetwork.Connection;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.Neuron;
import neuralNetwork.Sigmoid;

public class TestForwardPropagation {
	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork();
		ArrayList<Neuron> hiddenLayer = nn.getHidden();
		ArrayList<Neuron> inputLayer = nn.getInput();
		ArrayList<Neuron> outputLayer = nn.getOutput();
		
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
		n1.getOutputs().add(c);
		n4.getInputs().add(c);
		// 1 - 5
		c = new Connection(n1, n5, -0.3);
		n1.getOutputs().add(c);
		n5.getInputs().add(c);
		// 2 - 4
		c = new Connection(n2, n4, 0.4);
		n2.getOutputs().add(c);
		n4.getInputs().add(c);		
		// 2 - 5
		c = new Connection(n2, n5, 0.1);
		n2.getOutputs().add(c);
		n5.getInputs().add(c);		
		// 3 - 4
		c = new Connection(n3, n4, -0.5);
		n3.getOutputs().add(c);
		n4.getInputs().add(c);		
		// 3 - 5
		c = new Connection(n3, n5, 0.2);
		n3.getOutputs().add(c);
		n5.getInputs().add(c);		
		// 4 - 6
		c = new Connection(n4, n6, -0.3);
		n4.getOutputs().add(c);
		n6.getInputs().add(c);		
		// 5 - 6
		c = new Connection(n5, n6, -0.2);
		n5.getOutputs().add(c);
		n6.getInputs().add(c);		
		
		inputLayer.add(n1);
		inputLayer.add(n2);
		inputLayer.add(n3);
		
		hiddenLayer.add(n4);
		hiddenLayer.add(n5);
		
		outputLayer.add(n6);
		
		
		ArrayList<Double> inputValues = new ArrayList<Double>();
		inputValues.add(1.0);
		inputValues.add(0.0);
		inputValues.add(1.0);
		System.out.println("Input: " + inputValues);
		ArrayList<Double> outputValues = nn.Evaluate(inputValues);
		System.out.println("Output: " + outputValues);
	}
}
