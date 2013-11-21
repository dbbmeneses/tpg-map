package dmeneses.maptpg.image.gradient;

import java.awt.Color;

class NonLinearHSVGradient extends Gradient {
	//Color c1 = new Color(0xE55C00);
	//Color c2 = new Color(0x0071BF);
	Color c1 = new Color(0x980A00);
	Color c2 = new Color(0x6AFF6E);
	float[] initHSB = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
	float[] finalHSB = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
	double diff;

	public NonLinearHSVGradient(double min, double max) {
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
		
		Color f = Color.getHSBColor((float) h, (float) s, (float) b);
		return new Color(255 - f.getRed(), 255 - f.getGreen(), 255 - f.getBlue());
	}
}