package dmeneses.maptpg.map;

public class CircleF implements ShapeF {
	double radius;
	PointF center;
	PointF[] box;
	
	public CircleF(PointF center, double radius) {
		this.center = center;
		this.radius = radius;
		
		box = new PointF[2];
		
		box[0] = new PointF(center.x - radius, center.y-radius);
		box[1] = new PointF(center.x + radius, center.y+radius);
	}
	
	public PointF[] getSurroundingBox() {
		return box;
	}
	
	public boolean isInside(PointF point) {
		return isInside(point.x, point.y);
	}
	
	public boolean isInside(double x, double y) {
		double dist = Math.sqrt((x - center.x)*(x - center.x)+(y - center.y)*(y - center.y));
		return dist <= radius;
	}
}
