package dmeneses.maptpg.image.gradient;

import java.awt.Color;

/**
 * Abstract gradient
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
public abstract class Gradient {
	protected double min;
	protected double max;
	
	public Gradient(double min, double max) {
		this.min = min;
		this.max = max;
	}
	public abstract Color getColor(double value);
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
}
