package dmeneses.maptpg.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import dmeneses.maptpg.image.gradient.Gradient;
import dmeneses.maptpg.image.gradient.Gradients;


public class Scale {
	Gradient g;
	BufferedImage img;

	public Scale(Gradient g) {
		this.g = g;
	}
	
	public static void main(String[] args) throws IOException {
		Gradient g = Gradients.createLinearHueGradient(0,10);
		Scale s = new Scale(g);
		s.generate("testScale.png", "test", false);
	}

	public void generate(String fileName, String legend, boolean majored) throws IOException {
		int color_x = 75;
		int color_y = 550;
		
		int total_x = 190;
		int total_y = 580;


		img = new BufferedImage(total_x, total_y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();

		BufferedImage imgColor = new BufferedImage(color_x+25, color_y, BufferedImage.TYPE_INT_ARGB); 

		/*
		 * render colors
		 */
		for(int y = 0; y<color_y; y++) {
			if(y % (color_y/10) == 0 || y == color_y-1) {
				for(int x = 0; x<color_x+10; x++) {
					imgColor.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
			else {
				Color c = g.getColor((((double) (color_y-y)) / ((double) color_y))*(g.getMax()-g.getMin()) + g.getMin());
				for(int x = 0; x<color_x; x++) {
					imgColor.setRGB(x, y, c.getRGB());
				}
			}
		}
		
		/*
		 * render text
		 */
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, total_x, total_y);
		g2d.drawImage(imgColor, null, 3, (total_y - color_y) / 2);
		Font font = new Font("Serif", Font.PLAIN, 25);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		
		for(int i=0; i<11; i++) {
			double pos = (double) i*color_y /10;
			double value = ((double) (10-i)) / (10.0)*(g.getMax()-g.getMin()) + g.getMin();
			String text = null;
			if(i == 0 && majored) {
				text = String.format(">%-5.1f", value);
			}
			else {
				text = String.format("%-5.1f", value);
			}
			
			g2d.drawString(text, 100, (total_y - color_y) / 2 + 10 + (int)pos);
		}
		
		font = new Font("Arial", Font.BOLD, 26);
		g2d.setFont(font);
		g2d.rotate(-Math.PI/2.0);
		g2d.drawString(legend, -550, 185);

		

		File f = new File(fileName);
		ImageIO.write(img, "PNG", f);
	}
}
