import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class windowForm {
	private ImageIcon imgLeft, imgRight, imgTemp;
	private JLabel labelText;
	private TextField textField;
	private JPanel panelNorth, panelWest, panelEast, panelSouth, panelTempWest, panelTempEast;
	private JButton btnSelect, btnHistogram, btnNoise, btnSigma;
	private LayoutManager layout;
	private Font btnText, labelFont;
	private int height, width;
	private BufferedImage GlobalImage;
	private int[][] colorBins;

	windowForm() {
		createWindow();
	}

	private void createWindow() {
		JFrame frame = new JFrame("AIP60947074S");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			createUI(frame);
		} catch (Exception e) {}
		frame.setSize(1080, 560);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void createUI(final JFrame frame) throws Exception {
		panelNorth = new JPanel();
		panelWest = new JPanel();
		panelEast = new JPanel();
		panelSouth = new JPanel();
		panelTempWest = new JPanel();
		panelTempEast = new JPanel();
		layout = new FlowLayout();

		btnText = new Font("標楷體", 10, 20);
		btnSelect = new JButton("Select Picture");
		btnSelect.setFont(btnText);
		btnHistogram = new JButton("Histogram");
		btnHistogram.setFont(btnText);
		btnNoise = new JButton("Gaussian Noise");
		btnNoise.setFont(btnText);
		btnNoise.setFont(btnText);
		labelFont = new Font("標楷體", 10, 20);
		labelText = new JLabel();
		labelText.setFont(labelFont);
		
		textField = new TextField(); 
		labelFont = new Font("標楷體", 10, 20);
		textField.setFont(labelFont);
		textField.setText("5");

		Dimension expectedDimension = new Dimension(530, 250);
		panelWest.setPreferredSize(expectedDimension);
		panelWest.setMaximumSize(expectedDimension);
		panelWest.setMinimumSize(expectedDimension);
		panelEast.setPreferredSize(expectedDimension);
		panelEast.setMaximumSize(expectedDimension);
		panelEast.setMinimumSize(expectedDimension);

		// 選擇圖片
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new ImageFilter());
				fileChooser.setAcceptAllFileFilterUsed(false);

				int option = fileChooser.showOpenDialog(frame);
				if (option == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					clear(); // 圖像清空
					String extension = getExtension(file);
					if (extension.equals("ppm")) {
						ppm(file);
					} else if (extension.equals("bmp")) {
						try {
							bmp(file);
						} catch (IOException e1) {
						}
					} else if (extension.equals("jpg")) {
						try {
							jpg(file);
						} catch (IOException e1) {
						}
					}
				} else {
					labelText.setText("Image Only");
				}
			}
		});

		// 顯示直方圖
		btnHistogram.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();

				// 繪製直方圖
				panelTempEast = new drawHistogram(colorBins);
				
				// 輸出至右邊Panel
				Dimension expectedDimension = new Dimension(500, 400);
				panelTempEast.setPreferredSize(expectedDimension);
				panelTempEast.setMaximumSize(expectedDimension);
				panelTempEast.setMinimumSize(expectedDimension);
				panelEast.add(panelTempEast);

				// 繪製灰階圖
				panelTempWest = new drawGray(width, height, GlobalImage, colorBins);
				
				// 輸出至左邊Panel
				expectedDimension = new Dimension(width, height);
				panelTempWest.setPreferredSize(expectedDimension);
				panelTempWest.setMaximumSize(expectedDimension);
				panelTempWest.setMinimumSize(expectedDimension);
				panelWest.add(panelTempWest);
			}
		});
		
		// 高斯噪音
		btnNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
				
				// noise
				GaussianNoise GN = new GaussianNoise(Integer.valueOf(textField.getText().trim()), colorBins);
				int[] pixel = GN.noise_generation();
				
				// 繪製灰階圖
				panelTempWest = new drawGray(width, height, GlobalImage, pixel);
				
				// 輸出至左邊Panel
				Dimension expectedDimension = new Dimension(width, height);
				panelTempWest.setPreferredSize(expectedDimension);
				panelTempWest.setMaximumSize(expectedDimension);
				panelTempWest.setMinimumSize(expectedDimension);
				panelWest.add(panelTempWest);
				
				// 繪製直方圖
				panelTempEast = new drawHistogram(pixel);
				
				// 輸出至右邊Panel
				expectedDimension = new Dimension(500, 400);
				panelTempEast.setPreferredSize(expectedDimension);
				panelTempEast.setMaximumSize(expectedDimension);
				panelTempEast.setMinimumSize(expectedDimension);
				panelEast.add(panelTempEast);

			}
		});

		panelNorth.add(btnSelect);
		panelNorth.add(btnHistogram);
		panelNorth.add(btnNoise);
		panelNorth.add(textField);
		panelWest.add(panelTempWest);
		panelEast.add(panelTempEast);
		panelSouth.add(labelText);
		frame.getContentPane().add(panelNorth, BorderLayout.NORTH);
		frame.getContentPane().add(panelWest, BorderLayout.WEST);
		frame.getContentPane().add(panelEast, BorderLayout.EAST);
		frame.getContentPane().add(panelSouth, BorderLayout.SOUTH);
	}

	private void jpg(File image) throws IOException {
		BufferedImage jpg = ImageIO.read(new File(image.getPath()));
		width = jpg.getWidth();
		height = jpg.getHeight();
		getRGB(jpg);

		BufferedImage imageDraw = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		panelTempWest = new draw(jpg);
		panelTempEast = new drawRGB(width, height, imageDraw, colorBins);

		GlobalImage = imageDraw;
		update();
	}

	private void bmp(File image) throws IOException {
		BufferedImage bmp = ImageIO.read(new File(image.getPath()));
		width = bmp.getWidth();
		height = bmp.getHeight();
		getRGB(bmp);
		
		BufferedImage imageDraw = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		panelTempWest = new draw(bmp);
		panelTempEast = new drawRGB(width, height, imageDraw, colorBins);

		GlobalImage = imageDraw;
		update();
	}

	private void ppm(File image) {
		ImagePPM ppm = new ImagePPM(image.getPath());
		width = ppm.getWidth();
		height = ppm.getHeight();
		BufferedImage imageDraw = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		panelTempWest = new drawRGB(width, height, imageDraw, ppm.getcolorBins());
		panelTempEast = new drawRGB(width, height, imageDraw, ppm.getcolorBins());

		colorBins = ppm.getcolorBins();
		GlobalImage = imageDraw;
		update();
	}

	private void clear() {
		panelWest.remove(panelTempWest);
		panelWest.revalidate();
		panelWest.repaint();

		panelEast.remove(panelTempEast);
		panelEast.revalidate();
		panelEast.repaint();
	}

	private void update() {
		Dimension expectedDimension = new Dimension(width, height);
		panelTempWest.setPreferredSize(expectedDimension);
		panelTempWest.setMaximumSize(expectedDimension);
		panelTempWest.setMinimumSize(expectedDimension);
		panelWest.add(panelTempWest);

		panelTempEast.setPreferredSize(expectedDimension);
		panelTempEast.setMaximumSize(expectedDimension);
		panelTempEast.setMinimumSize(expectedDimension);
		panelEast.add(panelTempEast);

		labelText.setText("原始大小: " + width + " * " + height);
	}

	private void getRGB(BufferedImage image) throws IOException {
		extract ex = new extract(image);
		colorBins = ex.getRGB();
	}

	private String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}

class ImageFilter extends FileFilter {
	public final static String JPG = "jpg";
	public final static String BMP = "bmp";
	public final static String PPM = "ppm";

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals(JPG) || extension.equals(BMP) || extension.equals(PPM)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Image Only";
	}

	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}