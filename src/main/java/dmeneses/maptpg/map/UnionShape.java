package dmeneses.maptpg.map;


public class UnionShape implements ShapeF {
	PointF[] box;
	public ShapeF s1;
	public ShapeF s2;

	public UnionShape(ShapeF s1, ShapeF s2) {
		this.s1 = s1;
		this.s2 = s2;

		box = new PointF[2];
		box[0] = new PointF(s1.getSurroundingBox()[0].x > s2.getSurroundingBox()[0].x ? 
				s1.getSurroundingBox()[0].x : 
				s2.getSurroundingBox()[0].x,
				s1.getSurroundingBox()[0].y > s2.getSurroundingBox()[0].y ? 
				s1.getSurroundingBox()[0].y : 
				s2.getSurroundingBox()[0].y
				);
		
		box[1] = new PointF(s1.getSurroundingBox()[1].x > s2.getSurroundingBox()[1].x ?
				s2.getSurroundingBox()[1].x :
				s1.getSurroundingBox()[1].x,
				s1.getSurroundingBox()[1].y > s2.getSurroundingBox()[1].y ? 
				s2.getSurroundingBox()[1].y : 
				s1.getSurroundingBox()[1].y);
	}

	@Override
	public PointF[] getSurroundingBox() {
		return box;
	}

	@Override
	public boolean isInside(PointF point) {
		return isInside(point.x, point.y);
	}
	
	@Override
	public boolean isInside(double x, double y) {
		return s1.isInside(x,y) && s2.isInside(x,y);
	}

}
