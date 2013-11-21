package dmeneses.maptpg.map;



public class PolygonF implements ShapeF {
	private double[] pX;
	private double[] pY;

	private double[] multipliers;
	private double[] constants;

	private PointF[] box;

	public PolygonF(PointF[] points) {
		pX = new double[points.length];
		pY = new double[points.length];

		for(int i = 0; i < points.length; i++) {
			pX[i] = points[i].x;
			pY[i] = points[i].y;
			//System.out.println(pX[i] + " " + pY[i]);
		}
		preCalculate();
	}

	public PolygonF(double[] xPoints, double[] yPoints) {
		this.pX = xPoints;
		this.pY = yPoints;
		preCalculate();
	}

	private void preCalculate() {
		multipliers = new double[pX.length];
		constants = new double[pX.length];
		box = new PointF[2];

		box[0] = new PointF(Double.MAX_VALUE, Double.MAX_VALUE);
		box[1] = new PointF(Double.MIN_VALUE, Double.MIN_VALUE);

		int j = pX.length-1;

		//pre calculate box and stuff for isInside test
		for(int i = 0; i < pX.length; i++) {	
			if(pX[i] < box[0].x) {
				box[0].x = pX[i];
			}
			if(pY[i] < box[0].y) {
				box[0].y = pY[i];
			}
			if(pX[i] > box[1].x) {
				box[1].x = pX[i];
			}
			if(pY[i] > box[1].y) {
				box[1].y = pY[i];
			}

			if(pY[i] == pY[j]) {
				multipliers[i] = 0.0;
				constants[i] = pX[i];
			}
			else {
				multipliers[i] = (pX[j]-pX[i])/(pY[j]-pY[i]);
				constants[i] = pX[i] - (pY[i]*(pX[j]-pX[i]))/(pY[j]-pY[i]);
			}
			j=i;
		}
	}

	public PointF[] getSurroundingBox() {
		return box;
	}

	@Override
	public boolean isInside(PointF p) {
		return isInside(p.x, p.y);
	}
	
	@Override
	public boolean isInside(double x, double y) {
		boolean odd = false;
		int j = pX.length-1;

		//go through all sides
		for(int i = 0; i< pX.length; i++) {
			if((pY[i] < y && pY[j] >= y) || (pY[j] < y && pY[i] >= y)) {
				double intersectX;
				intersectX = multipliers[i] * y + constants[i];
				if(intersectX < x) {
					odd = !odd;
				}
			}

			j=i;
		}
		return odd;
	}
}
