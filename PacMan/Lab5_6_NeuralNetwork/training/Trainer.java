package training;

import java.util.ArrayList;

import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import neuralNetwork.NeuralNetwork;
import pacman.game.Constants.MOVE;

public class Trainer {	
	private static String dataFilename = "oscar.data";
	private static String trainedNNFilename = "moveNN.json";
	// NN params
	private static int inputLayerSize = 28;
	private static int hiddenLayerSize = 20;
	private static int outputLayerSize = 4;
	

	// Backpropagation params
	private static WeightUpdateMode weightUpdateMode = WeightUpdateMode.CaseUpdate;
	private static int maxEpochs = 1000;
	private static double deltaWeightTerminationThreshold = 0.001;	
	private static double learningRate = 0.2;
	
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
		System.out.println("Training data size: " + data.length);
		// Transform DataTuples into TrainingTuples
		for(int i = 0; i < data.length; ++i) {
			MOVE m = data[i].DirectionChosen;
			// Skip neutral
			if(m == MOVE.NEUTRAL) {
				continue;
			}
			
			TrainingTuple tt = new TrainingTuple();
			// INPUT
			// Pacman pos
			tt.getInputValues().add(data[i].pacmanX);
			tt.getInputValues().add(data[i].pacmanY);
			// Legal moves
			tt.getInputValues().add(data[i].upLegal);
			tt.getInputValues().add(data[i].downLegal);
			tt.getInputValues().add(data[i].leftLegal);
			tt.getInputValues().add(data[i].rightLegal);
			// Ghosts
			tt.getInputValues().add(data[i].blinkyX);
			tt.getInputValues().add(data[i].blinkyY);
			tt.getInputValues().add(data[i].blinkyDist);
			tt.getInputValues().add(data[i].isBlinkyEdible);
			
			tt.getInputValues().add(data[i].pinkyX);
			tt.getInputValues().add(data[i].pinkyY);
			tt.getInputValues().add(data[i].pinkyDist);
			tt.getInputValues().add(data[i].isPinkyEdible);
			
			tt.getInputValues().add(data[i].inkyX);
			tt.getInputValues().add(data[i].inkyY);
			tt.getInputValues().add(data[i].inkyDist);
			tt.getInputValues().add(data[i].isInkyEdible);
			
			tt.getInputValues().add(data[i].sueX);
			tt.getInputValues().add(data[i].sueY);
			tt.getInputValues().add(data[i].sueDist);
			tt.getInputValues().add(data[i].isSueEdible);
			// Pills
			tt.getInputValues().add(data[i].nearestPillX);
			tt.getInputValues().add(data[i].nearestPillY);
			tt.getInputValues().add(data[i].nearestPillDist);
			tt.getInputValues().add(data[i].nearestPowerPillX);
			tt.getInputValues().add(data[i].nearestPowerPillY);
			tt.getInputValues().add(data[i].nearestPowerPillDist);
			
			
			// OUTPUT			
			tt.getOutputValues().add((m == MOVE.UP ? 1.0 : 0.0));
			tt.getOutputValues().add((m == MOVE.DOWN ? 1.0 : 0.0));	
			tt.getOutputValues().add((m == MOVE.LEFT ? 1.0 : 0.0));	
			tt.getOutputValues().add((m == MOVE.RIGHT ? 1.0 : 0.0));	
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
