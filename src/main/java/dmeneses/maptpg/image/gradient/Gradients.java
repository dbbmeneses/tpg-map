package dmeneses.maptpg.image.gradient;

/**
 * A simple Gradient factory
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
public class Gradients {
	public static Gradient createLinearHSVGradient(double min, double max) {
		return new LinearHSVGradient(min, max);
	}

	public static Gradient createLinearRGBGradient(double min, double max) {
		return new LinearRGBGradient(min, max);
	}
	
	public static Gradient createLinearHueGradient(double min, double max) {
		return new LinearHueGradient(min, max);
	}
	
	public static Gradient createStepHueGradient(double min, double max) {
		return new StepHueGradient(min, max);
	}

	public static Gradient createStepHueGradient(double min, double max, int steps) {
		return new StepHueGradient(min, max, steps);
	}
	
	public static Gradient createGradient(String type, double min, double max) {
		GRADIENTS t = GRADIENTS.valueOf(GRADIENTS.class, type);
		
		return createGradient(t, min, max);
	}
	public static Gradient createGradient(GRADIENTS type, double min, double max) {
		switch(type) {
			case LINEAR_HSV:
				return createLinearHSVGradient(min, max);
			case LINEAR_RGB:
				return createLinearRGBGradient(min, max);
			case LINEAR_HUE:
				return createLinearHueGradient(min, max);
			case STEP_HUE:
				return createStepHueGradient(min, max);
		}
		
		return null;
	}

	
	public enum GRADIENTS { LINEAR_HSV, LINEAR_RGB, LINEAR_HUE, STEP_HUE };
}
