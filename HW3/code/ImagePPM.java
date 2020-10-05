import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePPM {
	public final int RED = 0;
	public final int GREEN = 1;
	public final int BLUE = 2;
	private int[][] colorBins;
	private String filename = null; // filename for PPM image
	private int height = 0, width = 0, maxvalue = 0;

	public ImagePPM(String filename) {
		this.filename = filename;
		try {
			readImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int[][] getcolorBins() {
		return colorBins;
	}

	private void readImage() throws FileNotFoundException, IOException, NumberFormatException {
		char buffer; // character in PPM header
		String dim = new String(); // image dimension as a string
		String value = new String(); // image max value
		File f = new File(filename);
		FileInputStream isr = new FileInputStream(f);
		
		/*******************************************
		 *                 header                  *
		 *******************************************/
		do {
			buffer = (char) isr.read();
		} while (buffer != '\n' && buffer != ' ');

		buffer = (char) isr.read();
		if (buffer == '#') {
			do {
				buffer = (char) isr.read();
			} while (buffer != '\n');
			buffer = (char) isr.read();
		}

		/*******************************************
		 * second header line is "width height\n"  *
		 *******************************************/
		do {
			dim = dim + buffer;
			buffer = (char) isr.read();
		} while (buffer != ' ' && buffer != '\n');
		width = Integer.parseInt(dim.trim());

		dim = new String();
		buffer = (char) isr.read();
		do {
			dim = dim + buffer;
			buffer = (char) isr.read();
		} while (buffer != ' ' && buffer != '\n');
		height = Integer.parseInt(dim);
		
		/**
		 * third header line is max RGB value, e.g., "255\n" 
		 */
		do { 
			buffer = (char) isr.read();
			value = value + buffer;
		} while (buffer != ' ' && buffer != '\n');
		/********************************************/
		
		
		// remainder of file is width*height*3 bytes (red/green/blue triples)
		colorBins = new int[3][height * width];
		for (int i = 0; i < height * width; i++) {
			colorBins[RED][i] = (int) isr.read();
			colorBins[GREEN][i] = (int) isr.read();
			colorBins[BLUE][i] = (int) isr.read();
		}

		isr.close();
	}
}