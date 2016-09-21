package training;

import java.util.ArrayList;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import neuralNetwork.NeuralNetwork;

public class Trainer {	
	// NN params
	private int inputLayerSize = 10;
	private int hiddenLayerSize = 10;
	private int outputLayerSize = 1;
	

	// Backpropagation params
	private WeightUpdateMode weightUpdateMode = WeightUpdateMode.CaseUpdate;
	private int maxEpochs = 100;
	private double deltaWeightTerminationThreshold = 0.1;	
	private double learningRate = 0.9;
	
	public void Train() {
		// Create array of TrainigTuples with DataTuples from file
		ArrayList<TrainingTuple> trainingTuples = new ArrayList<TrainingTuple>();		
		DataTuple[] data = DataSaverLoader.LoadPacManData();
		// Transform DataTuples into TrainingTuples
		for(int i = 0; i < data.length; ++i) {
			TrainingTuple tt = new TrainingTuple();
			// INPUT
			// Dist to ghosts
			tt.getInputValues().add(data[i].blinkyDist);
			tt.getInputValues().add(data[i].pinkyDist);
			tt.getInputValues().add(data[i].inkyDist);
			tt.getInputValues().add(data[i].sueDist);
			// Edible ghosts
			tt.getInputValues().add(data[i].isBlinkyEdible);
			tt.getInputValues().add(data[i].isPinkyEdible);
			tt.getInputValues().add(data[i].isInkyEdible);
			tt.getInputValues().add(data[i].isSueEdible);
			// Pills
			tt.getInputValues().add(data[i].nearestPillDist);
			tt.getInputValues().add(data[i].nearestPowerPillDist);
			// OUTPUT
			// TODO: Is this enough?
			// Since this was the game state chosen by the player, the output should be high
			tt.getOutputValues().add(1.0);
			
			// Add to training tuples list
			trainingTuples.add(tt);
		}
		// Create Neural Network
		NeuralNetwork nn = new NeuralNetwork();
		nn.InitializeRandomFullyConnected(inputLayerSize, hiddenLayerSize, outputLayerSize);
		// Backpropagate
		Backpropagation backpropagation = new Backpropagation(weightUpdateMode, maxEpochs, deltaWeightTerminationThreshold);
		backpropagation.train(nn, trainingTuples, learningRate);
		// Save Neural Network to file
		
		// TODO: JSON
	}
}
