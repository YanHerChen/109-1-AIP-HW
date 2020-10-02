import java.awt.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

class ByteOrder {
	public static void reverse(byte[] bytesToConvert) {
		int numberOfBytes = bytesToConvert.length;
		int numberOfBytesHalf = numberOfBytes / 2;

		for (int b = 0; b < numberOfBytesHalf; b++) {
			byte byteFromStart = bytesToConvert[b];
			bytesToConvert[b] = bytesToConvert[numberOfBytes - 1 - b];
			bytesToConvert[numberOfBytes - 1 - b] = byteFromStart;
		}
	}

	public static int reverse(int intToReverse) {
		byte[] intAsBytes = new byte[] { (byte) (intToReverse & 0xFF), (byte) ((intToReverse >> 8) & 0xFF),
				(byte) ((intToReverse >> 16) & 0xFF), (byte) ((intToReverse >> 24) & 0xFF), };

		intToReverse = ((intAsBytes[3] & 0xFF) + ((intAsBytes[2] & 0xFF) << 8) + ((intAsBytes[1] & 0xFF) << 16)
				+ ((intAsBytes[0] & 0xFF) << 24));

		return intToReverse;
	}

	public static long reverse(long valueToReverse) {
		byte[] valueAsBytes = new byte[] { (byte) (valueToReverse & 0xFF), (byte) ((valueToReverse >> 8) & 0xFF),
				(byte) ((valueToReverse >> 16) & 0xFF), (byte) ((valueToReverse >> 24) & 0xFF),
				(byte) ((valueToReverse >> 32) & 0xFF), (byte) ((valueToReverse >> 40) & 0xFF),
				(byte) ((valueToReverse >> 48) & 0xFF), (byte) ((valueToReverse >> 56) & 0xFF), };

		long returnValue = (valueAsBytes[7] & 0xFF);
		returnValue += ((valueAsBytes[6] & 0xFF) << 8);
		returnValue += ((valueAsBytes[5] & 0xFF) << 16);
		returnValue += ((valueAsBytes[4] & 0xFF) << 24);
		returnValue += ((valueAsBytes[3] & 0xFF) << 32);
		returnValue += ((valueAsBytes[2] & 0xFF) << 40);
		returnValue += ((valueAsBytes[1] & 0xFF) << 48);
		returnValue += ((valueAsBytes[0] & 0xFF) << 56);

		return returnValue;
	}

	public static short reverse(short valueToReverse) {
		byte[] valueAsBytes = new byte[] { (byte) (valueToReverse & 0xFF), (byte) ((valueToReverse >> 8) & 0xFF), };

		valueToReverse = (short) (((valueAsBytes[1] & 0xFF)) + ((valueAsBytes[0] & 0xFF) << 8));

		return valueToReverse;
	}
}

class Coords {
	public int x;
	public int y;

	public Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class DataInputStreamLittleEndian {
	private DataInputStream systemStream;

	public DataInputStreamLittleEndian(DataInputStream systemStream) {
		this.systemStream = systemStream;
	}

	public void close() throws IOException {
		this.systemStream.close();
	}

	public void read(byte[] bytesToReadInto) throws IOException {
		this.systemStream.read(bytesToReadInto);

		// not necessary?
		// ByteOrder.reverse(bytesToReadInto);
	}

	public int readInt() throws IOException {
		return ByteOrder.reverse(this.systemStream.readInt());
	}

	public long readLong() throws IOException {
		return ByteOrder.reverse(this.systemStream.readLong());
	}

	public short readShort() throws IOException {
		return ByteOrder.reverse(this.systemStream.readShort());
	}

	public String readString(int numberOfCharacters) throws IOException {
		byte[] bytesRead = new byte[numberOfCharacters];

		this.systemStream.read(bytesRead);

		return new String(bytesRead);
	}
}

class ImageBMP {
	public String filePath;
	public FileHeader fileHeader;
	public DIBHeader dibHeader;
	public int[] colorTable;
	public byte[] pixelData;

	public ImageBMP(String filePath, FileHeader fileHeader, DIBHeader dibHeader, int[] colorTable, byte[] pixelData) {
		this.filePath = filePath;
		this.fileHeader = fileHeader;
		this.dibHeader = dibHeader;
		this.colorTable = colorTable;
		this.pixelData = pixelData;
	}

	public BufferedImage convertToSystemImage() {
		Coords imageSizeInPixels = this.dibHeader.imageSizeInPixels();

		java.awt.image.BufferedImage returnValue;

		returnValue = new java.awt.image.BufferedImage(imageSizeInPixels.x, imageSizeInPixels.y,
				java.awt.image.BufferedImage.TYPE_INT_ARGB);

		int bitsPerPixel = this.dibHeader.bitsPerPixel(); //取dibheader bit大小
		int bytesPerPixel = bitsPerPixel / 8; //轉byte大小
		int colorOpaqueBlackAsArgb = 0xFF << bytesPerPixel * 8;

		// run所有pixel
		for (int y = 0; y < imageSizeInPixels.y; y++) {
			for (int x = 0; x < imageSizeInPixels.x; x++) {
				int bitOffsetForPixel = ((imageSizeInPixels.y - y - 1) // invert y
						* imageSizeInPixels.x + x) * bitsPerPixel;

				int byteOffsetForPixel = bitOffsetForPixel / 8; //轉byte大小

				int pixelColorArgb = colorOpaqueBlackAsArgb; //color
				for (int b = 0; b < bytesPerPixel; b++) {
					pixelColorArgb += (this.pixelData[byteOffsetForPixel + b] & 0xFF) << (8 * b);
				}

				returnValue.setRGB(x, y, pixelColorArgb); //設置每點RGB值
			}
		}

		return returnValue;
	}

	public static ImageBMP readFromFileAtPath(String filePathToReadFrom) {
		ImageBMP returnValue = null;

		try {
			DataInputStreamLittleEndian reader = new DataInputStreamLittleEndian(
					new DataInputStream(new FileInputStream(filePathToReadFrom)));

			FileHeader fileHeader = FileHeader.readFromStream(reader);

			DIBHeader dibHeader = DIBHeader.buildFromStream(reader);

			int[] colorTable = dibHeader.readColorTable(reader);

			int numberOfBytesInPixelData = dibHeader.imageSizeInBytes();

			byte[] pixelData = new byte[numberOfBytesInPixelData];

			reader.read(pixelData);

			returnValue = new ImageBMP(filePathToReadFrom, fileHeader, dibHeader, colorTable, pixelData);

			reader.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return returnValue;
	}

	// inner classes
	public static class FileHeader {
		// 14 bytes

		public String signature;
		public int fileSize;
		public short reserved1;
		public short reserved2;
		public int fileOffsetToPixelArray;

		public FileHeader(String signature, int fileSize, short reserved1, short reserved2,
				int fileOffsetToPixelArray) {
			this.signature = signature;
			this.fileSize = fileSize;
			this.reserved1 = reserved1;
			this.reserved2 = reserved2;
			this.fileOffsetToPixelArray = fileOffsetToPixelArray;
		}

		public static FileHeader readFromStream(DataInputStreamLittleEndian reader) {
			FileHeader returnValue = null;

			try {
				returnValue = new FileHeader(reader.readString(2), // signature
						reader.readInt(), // fileSize,
						reader.readShort(), // reserved1
						reader.readShort(), // reserved2
						reader.readInt() // fileOffsetToPixelArray
				);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return returnValue;
		}

		public String toString() {
			String returnValue = "<FileHeader " + "signature='" + this.signature + "' " + "fileSize='" + this.fileSize
					+ "' " + "fileOffsetToPixelArray ='" + this.fileOffsetToPixelArray + "' " + "/>";

			return returnValue;
		}
	}

	public static abstract class DIBHeader {
		public String name;
		public int sizeInBytes;

		public DIBHeader(String name, int sizeInBytes) {
			this.name = name;
			this.sizeInBytes = sizeInBytes;
		}

		public static class Instances {
			public static DIBHeader BitmapInfo = new DIBHeaderBitmapInfo();
			// public static DIBHeader BitmapV5 = new DIBHeaderV5();
		}

		public static DIBHeader buildFromStream(DataInputStreamLittleEndian reader) {
			DIBHeader returnValue = null;

			try {
				int dibHeaderSizeInBytes = reader.readInt();

				// hack
				if (dibHeaderSizeInBytes == 40) {
					returnValue = new DIBHeaderBitmapInfo().readFromStream(reader);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return returnValue;
		}

		public int[] readColorTable(DataInputStreamLittleEndian reader) {
			// todo
			return new int[] {};
		}

		// abstract method headers

		public abstract int bitsPerPixel();

		public abstract DIBHeader readFromStream(DataInputStreamLittleEndian reader);

		public abstract int imageSizeInBytes();

		public abstract Coords imageSizeInPixels();
	}

	public static class DIBHeaderBitmapInfo extends DIBHeader {
		public Coords imageSizeInPixels;
		public short planes;
		public short bitsPerPixel;
		public int compression;
		public int imageSizeInBytes;
		public Coords pixelsPerMeter;
		public int numberOfColorsInPalette;
		public int numberOfColorsUsed;

		public DIBHeaderBitmapInfo() {
			super("BitmapInfo", 40);
		}

		public DIBHeaderBitmapInfo(int sizeInBytes, Coords imageSizeInPixels, short planes, short bitsPerPixel,
				int compression, int imageSizeInBytes, Coords pixelsPerMeter, int numberOfColorsInPalette,
				int numberOfColorsUsed) {
			this();

			this.sizeInBytes = sizeInBytes;
			this.imageSizeInPixels = imageSizeInPixels;
			this.planes = planes;
			this.bitsPerPixel = bitsPerPixel;
			this.compression = compression;
			this.imageSizeInBytes = imageSizeInBytes;
			this.pixelsPerMeter = pixelsPerMeter;
			this.numberOfColorsInPalette = numberOfColorsInPalette;
			this.numberOfColorsUsed = numberOfColorsUsed;

			if (this.imageSizeInBytes == 0) {
				this.imageSizeInBytes = this.imageSizeInPixels.x * this.imageSizeInPixels.y * this.bitsPerPixel / 8;
			}
		}

		public String toString() {
			String returnValue = "<DIBHeader " + "size='" + this.sizeInBytes + "' " + "imageSizeInPixels='"
					+ this.imageSizeInPixels.x + "," + this.imageSizeInPixels.y + "' " + "planes='" + this.planes + "' "
					+ "bitsPerPixel='" + this.bitsPerPixel + "' " + "compression='" + this.compression + "' "
					+ "imageSizeInBytes='" + this.imageSizeInBytes + "' " + "pixelsPerMeter='" + this.pixelsPerMeter.x
					+ "," + this.pixelsPerMeter.y + "' " + "numberOfColorsInPalette='" + this.numberOfColorsInPalette
					+ "' " + "numberOfColorsUsed='" + this.numberOfColorsUsed + "' " + "/>";

			return returnValue;
		}

		// DIBHeader members

		public int bitsPerPixel() {
			return this.bitsPerPixel;
		}

		public DIBHeader readFromStream(DataInputStreamLittleEndian reader) {
			DIBHeader dibHeader = null;

			try {
				dibHeader = new DIBHeaderBitmapInfo(this.sizeInBytes, // dibHeaderSize;
						// imageSizeInPixels
						new Coords(reader.readInt(), reader.readInt()), reader.readShort(), // planes;
						reader.readShort(), // bitsPerPixel;
						reader.readInt(), // compression;
						reader.readInt(), // imageSizeInBytes;
						// pixelsPerMeter
						new Coords(reader.readInt(), reader.readInt()), reader.readInt(), // numberOfColorsInPalette
						reader.readInt() // numberOfColorsUsed
				);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return dibHeader;
		}

		public int imageSizeInBytes() {
			return this.imageSizeInBytes;
		}

		public Coords imageSizeInPixels() {
			return this.imageSizeInPixels;
		}
	}
}