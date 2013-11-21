package dmeneses.maptpg.image.gradient;

import java.awt.Color;

/**
 * Does a linear interpolation from one color to another by varying
 * linearly the red, green and blue values.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
class LinearRGBGradient extends Gradient {
	Color initColor = new Color(0xE55C00);
	Color finalColor = new Color(0x0071BF);
	double diff;
	
	public LinearRGBGradient(double min, double max) {
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
		
		double r = finalColor.getRed() * ((value - min) / diff) 
				+ initColor.getRed() * ((max - value) / diff);
		double g = finalColor.getGreen() * ((value - min) / diff) 
				+ initColor.getGreen() * ((max - value) / diff);
		double b = finalColor.getBlue() * ((value - min) / diff) 
				+ initColor.getBlue() * ((max - value) / diff);
		
		return new Color((int) r, (int) g, (int) b, (int) 255);
	}
}
