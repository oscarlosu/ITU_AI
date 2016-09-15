package neuralNetwork;

public class Sigmoid implements ActivationFunction {

	@Override
	public double value(double x) {
		return (1.0 / (1.0 + Math.pow(Math.E, - x)));
	}

	@Override
	public double derivative(double x) {
		double value = value(x);
		return value * (1 - value);
	}
}
