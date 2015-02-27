package dmeneses.maptpg.image.gradient;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Abstract gradient
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
@AllArgsConstructor
public abstract class Gradient {
	@Getter
	protected double min;
	@Getter
	protected double max;

	public abstract Color getColor(double value);
}
