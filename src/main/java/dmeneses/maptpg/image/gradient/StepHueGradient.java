package dmeneses.maptpg.image.gradient;

import java.awt.Color;

/**
 * Defines a gradient composed of several values of hue equally distanced in the
 * hue spectrum, with maximum saturation and brightness.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
class StepHueGradient extends LinearHueGradient {
	float[] initHSB = { 0, 1, 1};
	float[] finalHSB = { 1, 1, 1};
	double diff;
	int steps;

	public StepHueGradient(double min, double max) {
		super(min, max);
		this.steps = 20;
	}
	
	public StepHueGradient(double min, double max, int steps) {
		super(min, max);
		this.steps = steps;
	}
	
	@Override
	public Color getColor(double value) {
		if(value > max) {
			value = max;
		}
		if(value < min) {
			value = min;
		}
		
		double steps = 20.0;
		double step_size = (max-min) / steps;
		int step = (int) (value / step_size);
		value = step*step_size + step_size/2;

		return super.getColor(value);
	}
}
