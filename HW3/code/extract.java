import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class extract {
	BufferedImage image;

	// Red, Green, Blue
	private int NUMBER_OF_COLOURS = 3;
	public final int RED = 0;
	public final int GREEN = 1;
	public final int BLUE = 2;

	private int[][] colorBins;

	public extract(BufferedImage image) throws IOException {
		this.image = image;
		initial();
		RGB();
	}

	private void initial() {
		colorBins = new int[NUMBER_OF_COLOURS][];
		for (int i = 0; i < NUMBER_OF_COLOURS; i++) {
			colorBins[i] = new int[image.getWidth() * image.getHeight()];
		}
	}

	private void RGB() {
		// Reset all the bins
		for (int i = 0; i < NUMBER_OF_COLOURS; i++) {
			for (int j = 0; j < image.getWidth() * image.getHeight(); j++) {
				colorBins[i][j] = 0;
			}
		}

		Color c;
		int p = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				c = new Color(image.getRGB(x, y));

				colorBins[RED][p] = c.getRed();
				colorBins[GREEN][p] = c.getGreen();
				colorBins[BLUE][p] = c.getBlue();
				p++;
			}
		}
	}

	public int[][] getRGB() {
		return colorBins;
	}
}