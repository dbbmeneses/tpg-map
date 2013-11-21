package dmeneses.maptpg.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import dmeneses.maptpg.image.gradient.Gradient;
import dmeneses.maptpg.map.PointF;
import dmeneses.maptpg.map.ShapeF;


public class Renderer {
	private BufferedImage img = null;
	
	public void generate(Double[][] data, Gradient grad, int x_size, int y_size) {
		int x_blocks = data[0].length;
		int y_blocks = data.length;
		
		int x_res = x_blocks * x_size;
		int y_res = y_blocks * y_size;
		
		img = new BufferedImage(x_res, y_res, BufferedImage.TYPE_INT_ARGB);
		
		for(int y=0; y < y_blocks; y++) {
			for(int x=0; x < x_blocks; x++) {
				Color v = grad.getColor(data[y][x]);
				for(int yy = 0; yy < y_size; yy++) {
					for(int xx = 0; xx < x_size; xx++) {
						img.setRGB(x*x_size+xx, y*y_size+yy, v.getRGB());
					}
				}
			}
		}
	}
	
	public void generateBilinear(Double[][] data, Gradient grad, int x_size, int y_size, ShapeF shape) {
		int x_points = data[0].length;
		int y_points = data.length;
		
		int x_res = (x_points-1) * x_size;
		int y_res = (y_points-1) * y_size;
		
		PointF[] box = shape.getSurroundingBox();
		PointF pixel_size = new PointF((box[1].x - box[0].x)/(double) x_res,
											(box[1].y - box[0].y)/(double) y_res);
		
		img = new BufferedImage(x_res, y_res, BufferedImage.TYPE_INT_ARGB);
		PointF pos = new PointF(0,0);
		PointF box_size = new PointF(box[1].x-box[0].x, box[1].y-box[0].y);
		
		for(int y=0; y < (y_points-1); y++) {
			for(int x=0; x < (x_points-1); x++) {
				if(data[y][x] == null || data[y][x+1] == null || data[y+1][x] == null ||
						data[y+1][x+1] == null) {
					continue;
				}
				Color q11 = grad.getColor(data[y][x]);
				Color q12 = grad.getColor(data[y][x+1]);
				Color q21 = grad.getColor(data[y+1][x]);
				Color q22 = grad.getColor(data[y+1][x+1]);
				
				for(int yy = 0; yy < y_size; yy++) {
					for(int xx = 0; xx < x_size; xx++) {
						if(!shape.isInside((x*x_size+xx)*pixel_size.x + box[0].x, 
											(y*y_size+yy)*pixel_size.y + box[0].y)) {
							continue;
						}
						Color c = bilinear3(q11, q12, q21, q22, x_size, y_size, xx, yy);
						pos.x = box[0].x + box_size.x*(((double) x*x_size+xx)/ (double) x_res);
						pos.y = box[0].y + box_size.y*(((double) y*y_size+yy)/ (double) y_res);
					
						img.setRGB(x*x_size+xx, y*y_size+yy, c.getRGB());
					}
				}
			}
		}
	}
	
	private Color bilinear3(Color q11, Color q12, Color q21, Color q22, int x_size, int y_size, int x, int y) {
		return new Color(
				bilinear(q11.getRed(), q12.getRed(), q21.getRed(), q22.getRed(), x_size, y_size, x, y),
				bilinear(q11.getGreen(), q12.getGreen(), q21.getGreen(), q22.getGreen(), x_size, y_size, x, y),
				bilinear(q11.getBlue(), q12.getBlue(), q21.getBlue(), q22.getBlue(), x_size, y_size, x, y),
				255);		
	}
	
	private int bilinear(int q11, int q12, int q21, int q22, int x_size, int y_size, int x, int y) {
		float r1 = weight_av(x, x_size, q11, q12);
		float r2 = weight_av(x, x_size, q21, q22);
		
		return (int) weight_av(y, y_size, r1, r2);
	}
	
	private float weight_av(int x, int x_size, float v1, float v2) {
		float w1 = ((float) x_size - x) / ((float) x_size);
		float w2 = ((float) x) / ((float) x_size);
		
		return w1*v1+w2*v2;
	}
	
	public void export(String path) throws IOException {
		if(img != null) { 
			File f = new File(path);
			ImageIO.write(img, "PNG", f);
		}
	}
}
