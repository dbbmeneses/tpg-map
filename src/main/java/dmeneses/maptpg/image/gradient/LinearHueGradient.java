package dmeneses.maptpg.image.gradient;

import java.awt.Color;

/**
 * Does a linear interpolation throught the entire color spectrum with
 * maximum saturation and brightness, by varying linearly the hue.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
class LinearHueGradient extends Gradient {
	float[] initHSB = { 0, 1, 1};
	float[] finalHSB = { 1, 1, 1};
	double diff;


	public LinearHueGradient(double min, double max) {
		super(min, max);
		this.diff = max - min;
	}

	@Override
	public Color getColor(double value) {
		if(value > max) {
			value = max;
		}
		if(value < min) {
			value = min;
		}

		double h = finalHSB[0] * ((value - min) / diff) 
				+ initHSB[0] * ((max - value) / diff);
		double s = finalHSB[1] * ((value - min) / diff) 
				+ initHSB[1] * ((max - value) / diff);
		double b = finalHSB[2] * ((value - min) / diff) 
				+ initHSB[2] * ((max - value) / diff);

		return Color.getHSBColor((float) h, (float) s, (float) b);
	}
}
