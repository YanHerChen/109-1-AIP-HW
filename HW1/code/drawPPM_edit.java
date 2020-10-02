import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class drawPPM_edit extends JPanel {
	private BufferedImage image;
	private int[] rgb, r, g, b;
	private String path;

	public drawPPM_edit(int width, int height, BufferedImage image, int[] r, int[] g, int[] b, String path) {
		this.image = image;
		this.r = r;
		this.g = g;
		this.b = b;
		this.path = path;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		int p = 0;
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++, p++) {
				image.setRGB(j, i, new Color(r[p], g[p], b[p]).getRGB());
			}
		}
		graphics.drawImage(image, 0, 0, getWidth(), getHeight(), this);

		try{
	        ImageIO.write(image, "jpg", new File(path.replace(".ppm", ".jpg")));
		}catch(IOException e){}
	}
}