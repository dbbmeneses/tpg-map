package dmeneses.maptpg.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class MapTools {
	public static void main(String[] args) {
		loadKml("/home/dmeneses/Downloads/geneva2.kml");
	}
	/**
	 * Loads a KML file containing a polygon composed of a LinearRing. Returns a PolygonF
	 * object built with the projected coordinates (map space).
	 * @param path Path of the KML file in the file system.
	 * @return A polygon composed of map-projected coordinates.
	 */
	public static PolygonF loadKml(String path) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		Kml kml = Kml.unmarshal(new File(path));

		Feature ft = kml.getFeature();

		if(!(ft instanceof Placemark)) {
			System.out.println("Error loading kml from " + path + " - not a placemark");
			return null;
		}

		Placemark pl = (Placemark) ft;
		Geometry geo = pl.getGeometry();

		if(!(geo instanceof Polygon)) {
			System.out.println("Error loading kml from " + path + " - placemark doesn't contain Polygon");
			return null;
		}
		Polygon poly = (Polygon) geo;
		Boundary boundary = poly.getOuterBoundaryIs();
		LinearRing ring = boundary.getLinearRing();
		List<Coordinate> coordinates = ring.getCoordinates();

		PointF[] points = new PointF[coordinates.size()];

		int i = 0;
		for(Coordinate c : coordinates) {
			//System.out.println("new google.maps.LatLng(" + c.getLatitude() + ", " + c.getLongitude() + "),");
			points[i] = proj.fromLatLngToPoint(new LatLng(c.getLatitude(), c.getLongitude()), 0);
			i++;
		}

		return new PolygonF(points);
	}

	/**
	 * 
	 * @param kmlPath
	 * @param x_tiles
	 * @param y_tiles
	 * @return
	 */
	public static LatLng[][] getPointArray(String kmlPath, int x_tiles, int y_tiles) {
		PolygonF poly = loadKml(kmlPath);
		return getPointArray(poly, x_tiles, y_tiles);

	}

	public static LatLng[][] getPointArray(PolygonF poly, int x_tiles, int y_tiles) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		PointF[] box = poly.getSurroundingBox();
		LatLng[][] points = new LatLng[y_tiles][x_tiles];

		PointF delta = new PointF(box[0].x-box[1].x, box[0].y-box[1].y);
		PointF tile_size = new PointF(delta.x / x_tiles, delta.y / y_tiles);

		for(int x = 0; x < x_tiles+1; x++) {
			for(int y = 0; y < y_tiles+1; y++) {
				PointF p = new PointF(box[0].x + tile_size.x*x, box[0].y + tile_size.y*y);
				if(poly.isInside(p)) {
					points[y][x] = proj.fromPointToLatLng(p, 0);
				}
				else{
					points[y][x] = null;
				}
			}
		}

		return points;
	}
	
	public static List<LatLng> getPointList(ShapeF shape, int x_points, int y_points) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		ArrayList<LatLng> points = new ArrayList<LatLng>(x_points*y_points);
		PointF[] box = shape.getSurroundingBox();
		

		PointF total_size = new PointF(box[1].x-box[0].x, box[1].y-box[0].y);
		PointF tile_size = new PointF(total_size.x / (x_points-1), 
				total_size.y / (y_points-1));

		for(int y = 0; y < y_points; y++) {
			for(int x = 0; x < x_points; x++) {
				PointF p = new PointF(box[0].x + tile_size.x*x, box[0].y + tile_size.y*y);
				if(shape.isInside(p)) {
					points.add(proj.fromPointToLatLng(p, 0));
				}
				else {
					points.add(null);
				}
				
			}
		}

		return points;
	}
	public static List<LatLng> getPointList(PointF[] box, int x_points, int y_points) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		ArrayList<LatLng> points = new ArrayList<LatLng>(x_points*y_points);
		PointF total_size = new PointF(box[1].x-box[0].x, box[1].y-box[0].y);
		PointF tile_size = new PointF(total_size.x / (x_points-1), 
				total_size.y / (y_points-1));

		for(int y = 0; y < y_points; y++) {
			for(int x = 0; x < x_points; x++) {
				PointF p = new PointF(box[0].x + tile_size.x*x, box[0].y + tile_size.y*y);
					points.add(proj.fromPointToLatLng(p, 0));	
			}
		}

		return points;
	}


	public static LatLng[][] getPointArray(LatLng topLeft, LatLng bottomRight, int x_points, int y_points) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		LatLng[][] points = new LatLng[x_points][y_points];

		//entire world projected from 0 to 250 in x/y.
		PointF p1 = proj.fromLatLngToPoint(topLeft, 0);
		PointF p2 = proj.fromLatLngToPoint(bottomRight, 0);

		PointF total_size = new PointF(p2.x-p1.x, p2.y-p1.y);
		PointF tile_size = new PointF(total_size.x / (x_points-1), 
							total_size.y / (y_points-1));

		for(int y = 0; y < y_points; y++) {
			for(int x = 0; x < x_points; x++) {
				PointF p = new PointF(p1.x + tile_size.x*x, p1.y + tile_size.y*y);
				points[x][y] = proj.fromPointToLatLng(p, 0);
			}
		}

		return points;
	}

	public static List<LatLng> getPointList(PointF topLeft, PointF bottomRight, int x_points, int y_points) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		List<LatLng> points = new ArrayList<LatLng>(x_points*y_points);

		//entire world projected from 0 to 250 in x/y.
		PointF p1 = topLeft;
		PointF p2 = bottomRight;

		PointF total_size = new PointF(p2.x-p1.x, p2.y-p1.y);
		PointF tile_size = new PointF(total_size.x / (x_points-1), 
				total_size.y / (y_points-1));

		for(int y = 0; y < y_points; y++) {
			for(int x = 0; x < x_points; x++) {
				PointF p = new PointF(p1.x + tile_size.x*x, p1.y + tile_size.y*y);
				points.add(proj.fromPointToLatLng(p, 0));
			}
		}

		return points;
	}
	
	public static List<LatLng> getPointList(LatLng topLeft, LatLng bottomRight, int x_points, int y_points) {
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		List<LatLng> points = new ArrayList<LatLng>(x_points*y_points);

		//entire world projected from 0 to 250 in x/y.
		PointF p1 = proj.fromLatLngToPoint(topLeft, 0);
		PointF p2 = proj.fromLatLngToPoint(bottomRight, 0);

		PointF total_size = new PointF(p2.x-p1.x, p2.y-p1.y);
		PointF tile_size = new PointF(total_size.x / (x_points-1), 
				total_size.y / (y_points-1));

		for(int y = 0; y < y_points; y++) {
			for(int x = 0; x < x_points; x++) {
				PointF p = new PointF(p1.x + tile_size.x*x, p1.y + tile_size.y*y);
				points.add(proj.fromPointToLatLng(p, 0));
			}
		}

		return points;
	}
}
