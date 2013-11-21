package dmeneses.maptpg.map;

public interface ShapeF {
	public PointF[] getSurroundingBox();
	public boolean isInside(PointF point);
	public boolean isInside(double x, double y);
}
