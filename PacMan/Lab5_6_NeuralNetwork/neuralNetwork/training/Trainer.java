package neuralNetwork.training;

import java.util.ArrayList;

import neuralNetwork.NeuralNetwork;
import neuralNetwork.dataRecording.DataSaverLoader;
import neuralNetwork.dataRecording.DataTuple;
import pacman.game.Constants.MOVE;
/**
 * Improvements:
 * - Cross-validate (train with subsets of data)
 * - Train to imitate non-human agent
 * - Evaluate only at junctions
 * - Replace positions with direction to objects + distances
 * - Remove distances
 * 
 * 
 * @author Oscar
 *
 */
public class Trainer {	
	private static String dataFilename = "martin.data";
	private static String trainedNNFilename = "moveJunctionsNN.json";
	// NN params
	private static int inputLayerSize = 18;
	private static int hiddenLayerSize = 20;
	private static int outputLayerSize = 4;
	

	// Backpropagation params
	private static WeightUpdateMode weightUpdateMode = WeightUpdateMode.CaseUpdate;
	private static int maxEpochs = 5000;
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
			// Pacman pos (0-1)
			tt.getInputValues().add(data[i].pacmanX);
			tt.getInputValues().add(data[i].pacmanY);
			// Legal moves ( 2-5)
//			tt.getInputValues().add(data[i].upLegal);
//			tt.getInputValues().add(data[i].downLegal);
//			tt.getInputValues().add(data[i].leftLegal);
//			tt.getInputValues().add(data[i].rightLegal);
			// Ghosts (6 -9)
			tt.getInputValues().add(data[i].blinkyX);
			tt.getInputValues().add(data[i].blinkyY);
			//tt.getInputValues().add(data[i].blinkyDist);
			tt.getInputValues().add(data[i].isBlinkyEdible);
			// (10.13)
			tt.getInputValues().add(data[i].pinkyX);
			tt.getInputValues().add(data[i].pinkyY);
			//tt.getInputValues().add(data[i].pinkyDist);
			tt.getInputValues().add(data[i].isPinkyEdible);
			// 14 -17
			tt.getInputValues().add(data[i].inkyX);
			tt.getInputValues().add(data[i].inkyY);
			//tt.getInputValues().add(data[i].inkyDist);
			tt.getInputValues().add(data[i].isInkyEdible);
			// 18 - 21
			tt.getInputValues().add(data[i].sueX);
			tt.getInputValues().add(data[i].sueY);
			//tt.getInputValues().add(data[i].sueDist);
			tt.getInputValues().add(data[i].isSueEdible);
			// Pills (22 -24)
			tt.getInputValues().add(data[i].nearestPillX);
			tt.getInputValues().add(data[i].nearestPillY);
			//tt.getInputValues().add(data[i].nearestPillDist);
			// 25-27
			tt.getInputValues().add(data[i].nearestPowerPillX);
			tt.getInputValues().add(data[i].nearestPowerPillY);
			//tt.getInputValues().add(data[i].nearestPowerPillDist);
			
			
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
		
		
		// Validate
		
	}
}
