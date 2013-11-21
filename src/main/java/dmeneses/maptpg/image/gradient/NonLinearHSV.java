package dmeneses.maptpg.image.gradient;

import java.awt.Color;

import dmeneses.maptpg.image.NonLinearFilter;


class NonLinearHSV extends Gradient {
	float[] initHSB = { 0, 1, 1};
	float[] finalHSB = { 1, 1, 1};
	double diff;
	
	public NonLinearHSV(double min, double max) {
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
		
		value = min + diff*NonLinearFilter.f((value-min) / diff);

		double h = finalHSB[0] * ((value - min) / diff) 
				+ initHSB[0] * ((max - value) / diff);
		double s = finalHSB[1] * ((value - min) / diff) 
				+ initHSB[1] * ((max - value) / diff);
		double b = finalHSB[2] * ((value - min) / diff) 
				+ initHSB[2] * ((max - value) / diff);

		return Color.getHSBColor((float) h, (float) s, (float) b);
	}
}
