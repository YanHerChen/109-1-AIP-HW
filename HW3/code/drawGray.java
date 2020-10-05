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

public class drawGray extends JPanel {
	private BufferedImage image;
	private final int RED = 0;
	private final int GREEN = 1;
	private final int BLUE = 2;
	private int[][] colorBins;
	private int[] Pixel_Noise;

	public drawGray(int width, int height, BufferedImage image, int[] Pixel_Noise) {
		this.image = image;
		this.Pixel_Noise = Pixel_Noise;
		repaint();
	}

	public drawGray(int width, int height, BufferedImage image, int[][] colorbins) {
		this.image = image;
		this.colorBins = colorbins;
		repaint();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		int p = 0;
		if (colorBins != null) {
			int pixel;
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++, p++) {
					pixel = (colorBins[RED][p] + colorBins[GREEN][p] + colorBins[BLUE][p]) / 3;
					image.setRGB(j, i, new Color(pixel, pixel, pixel).getRGB());
				}
			}
		} else {
			for (int i = 0; i < image.getHeight(); i++) {
				for (int j = 0; j < image.getWidth(); j++, p++) {
					if(Pixel_Noise[p]>255) {
						Pixel_Noise[p] = 255;
					}
					image.setRGB(j, i, new Color(Pixel_Noise[p], Pixel_Noise[p], Pixel_Noise[p]).getRGB());
				}
			}
		}

		graphics.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}
}