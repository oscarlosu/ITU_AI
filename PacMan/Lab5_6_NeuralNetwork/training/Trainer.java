package training;

import java.util.ArrayList;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import neuralNetwork.NeuralNetwork;

public class Trainer {	
	private static String dataFilename = "trainingData.data";
	private static String trainedNNFilename = "trainedNN.json";
	// NN params
	private static int inputLayerSize = 10;
	private static int hiddenLayerSize = 10;
	private static int outputLayerSize = 1;
	

	// Backpropagation params
	private static WeightUpdateMode weightUpdateMode = WeightUpdateMode.CaseUpdate;
	private static int maxEpochs = 10;
	private static double deltaWeightTerminationThreshold = 0.1;	
	private static double learningRate = 0.9;
	
	public static void main(String[] args) {
		System.out.println("Training started. Params:");
		
		System.out.println("dataFilename " + dataFilename);
		System.out.println("trainedNNFilename " + trainedNNFilename);
		
		System.out.println("inputLayerSize " + inputLayerSize);
		System.out.println("hiddenLayerSize " + hiddenLayerSize);
		System.out.println("outputLayerSize " + outputLayerSize);
		
		System.out.println("weightUpdateMode " + weightUpdateMode);
		System.out.println("maxEpochs " + maxEpochs);
		System.out.println("deltaWeightTerminationThreshold " + deltaWeightTerminationThreshold);
		System.out.println("learningRate " + learningRate);		
		
		System.out.println("...");
		Train();
		
		System.out.println("Training ended.");
	}
	
	
	public static void Train() {
		// Create array of TrainigTuples with DataTuples from file
		ArrayList<TrainingTuple> trainingTuples = new ArrayList<TrainingTuple>();		
		DataTuple[] data = DataSaverLoader.LoadPacManData(dataFilename);
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
			tt.getOutputValues().add(data[i].score);			
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
		nn.Save(trainedNNFilename);
	}
}
