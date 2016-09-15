package neuralNetwork;

public interface ActivationFunction {
	public abstract double value(double x);
	public abstract double derivative(double x);
}
