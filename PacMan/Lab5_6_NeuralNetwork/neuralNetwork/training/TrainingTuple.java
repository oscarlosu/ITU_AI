package neuralNetwork.training;

import java.util.ArrayList;

public class TrainingTuple {
	private ArrayList<Double> inputValues;
	private ArrayList<Double> outputValues;
	
	public TrainingTuple() {
		inputValues = new ArrayList<Double>();
		outputValues = new ArrayList<Double>();
	}
	
	public TrainingTuple(ArrayList<Double> inputValues, ArrayList<Double> outputValues) {
		this.inputValues = inputValues;
		this.outputValues = outputValues;
	}

	public ArrayList<Double> getInputValues() {
		return inputValues;
	}

	public void setInputValues(ArrayList<Double> inputValues) {
		this.inputValues = inputValues;
	}

	public ArrayList<Double> getOutputValues() {
		return outputValues;
	}

	public void setOutputValues(ArrayList<Double> outputValues) {
		this.outputValues = outputValues;
	}
	
	
}
