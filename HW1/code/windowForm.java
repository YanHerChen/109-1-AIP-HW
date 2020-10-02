import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class windowForm {
	private ImageIcon imgLeft, imgRight, imgTemp;
	private JLabel labelText;
	private JPanel panelNorth, panelWest, panelEast, panelSouth, panelTempWest, panelTempEast;
	private JButton btnSelect;
	private LayoutManager layout;
	private Font btnText, labelFont;
	private int height, width;

	windowForm() {
		createWindow();
	}

	private void createWindow() {
		JFrame frame = new JFrame("AIP60947074S");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createUI(frame);
		frame.setSize(1080, 560);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void createUI(final JFrame frame) {
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
		labelFont = new Font("標楷體", 10, 20);
		labelText = new JLabel();
		labelText.setFont(labelFont);

		Dimension expectedDimension = new Dimension(530, 250);
		panelWest.setPreferredSize(expectedDimension);
		panelWest.setMaximumSize(expectedDimension);
		panelWest.setMinimumSize(expectedDimension);
		panelEast.setPreferredSize(expectedDimension);
		panelEast.setMaximumSize(expectedDimension);
		panelEast.setMinimumSize(expectedDimension);
		

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
						bmp(file);
					} else if (extension.equals("jpg")){
						try {
							jpg(file);
						} catch (IOException e1) {}
					}
				} else {
					labelText.setText("Image Only");
				}
			}
		});

		panelNorth.add(btnSelect);
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
		panelTempWest = new drawBMP(jpg);
		panelTempEast = new drawBMP(jpg);
		
		update();
	}
	
	private void bmp(File image) {
		ImageBMP imageFile = ImageBMP.readFromFileAtPath(image.getPath());
		BufferedImage bmp = imageFile.convertToSystemImage();
		width = bmp.getWidth();
		height = bmp.getHeight();
		panelTempWest = new drawBMP(bmp);
		panelTempEast = new drawBMP(bmp);
		
		update();
	}

	private void ppm(File image) {
		ImagePPM imageFile = new ImagePPM(image.getPath());
		width = imageFile.getWidth();
		height = imageFile.getHeight();
		BufferedImage ppm = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		panelTempWest = new drawPPM(width, height, ppm, imageFile.r, imageFile.g, imageFile.b);
		panelTempEast = new drawPPM(width, height, ppm, imageFile.r, imageFile.g, imageFile.b);
		
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