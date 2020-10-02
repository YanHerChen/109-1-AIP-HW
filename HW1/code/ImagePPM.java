import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePPM {
	private byte bytes[] = null; // bytes which make up binary PPM image
	public int r[] = null;
	public int g[] = null;
	public int b[] = null;
	private double doubles[] = null;
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

	/**
	 * @return the height of the image.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width of the image.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return The data of the image.
	 */
	public byte[] getBytes() {
		return bytes;
	}

	private void readImage() throws FileNotFoundException, IOException, NumberFormatException {
		// read PPM format image
		bytes = null;
		char buffer; // character in PPM header
		String id = new String(); // PPM magic number
		String dim = new String(); // image dimension as a string
		String value = new String(); // image max value
		int count = 0;
		File f = new File(filename);
		FileInputStream isr = new FileInputStream(f);
		boolean weird = false;

		do {
			buffer = (char) isr.read();
			id = id + buffer;
			count++;
		} while (buffer != '\n' && buffer != ' ');

		
		if (id.charAt(0) == 'P') {
			buffer = (char) isr.read();
			count++;
			if (buffer == '#') {
				do {
					buffer = (char) isr.read();
					count++;
				} while (buffer != '\n');
				count++;
				buffer = (char) isr.read();
			}
			
			
			// second header line is "width height\n"
			do {
				dim = dim + buffer;
				buffer = (char) isr.read();
				count++;
			} while (buffer != ' ' && buffer != '\n');
			width = Integer.parseInt(dim.trim());

			dim = new String();
			buffer = (char) isr.read();
			count++;
			do {
				dim = dim + buffer;
				buffer = (char) isr.read();
				count++;
			} while (buffer != ' ' && buffer != '\n');
			height = Integer.parseInt(dim);
			
			do { // third header line is max RGB value, e.g., "255\n"
				buffer = (char) isr.read();
				count++;
				value = value + buffer;
			} while (buffer != ' ' && buffer != '\n');
			maxvalue = Integer.parseInt(value.trim());
			
			// System.out.print("Reading image...");
			// System.out.flush();

			// remainder of file is width*height*3 bytes (red/green/blue triples)

			bytes = new byte[height * width];
			doubles = new double[height * width];
			r = new int[height*width];
			g = new int[height*width];
			b = new int[height*width];

			/*
			 * Check for weird stuff
			 */
			if ((height * width + count * 2) < f.length())
				weird = true;

			// PPM
			if ((id.charAt(1) == '5') || (id.charAt(1) == '6')) {
				if (!weird)
					isr.read(bytes, 0, height * width);
				// Now read in as double
				else {
					// There are nine bytes per RGB-tuple. Good for 32-bit color,
					// not for us.
					int v = 0;
					for (int i = 0; i < height * width; i++) {
						r[i] = (int) isr.read();
						g[i] = (int) isr.read();
						b[i] = (int) isr.read();
						//System.out.println(i+" "+r[i]+" "+g[i]+" "+b[i]);
						/*
						v = isr.read();
						v = v + isr.read();
						v = v + isr.read();
						v = v / 3;
						bytes[i] = (byte) (v & 0xFF);*/
					}
				}
			}
			
			// PGM
			else if (id.charAt(1) == '2') {
				int i = 0;
				for (i = 0; i < width * height; i++) {
					dim = new String();
					do {
						buffer = (char) isr.read();
						if (buffer != ' ' && buffer != '\n')
							dim = dim + buffer;
					} while (buffer != ' ' && buffer != '\n');
					bytes[i] = (byte) (Integer.parseInt(dim) & 0xFF);
				}
			}
			
			isr.close();
		} else {
			width = height = 0;
			doubles = new double[0];
			bytes = new byte[0];
			throw new NumberFormatException("Wrong header information!");
		}
	}
}